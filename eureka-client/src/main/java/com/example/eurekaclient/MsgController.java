package com.example.eurekaclient;

import com.example.map.models.Country;
import com.example.map.models.CountryMessage;
import com.example.map.models.OperationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableDiscoveryClient
public class MsgController {

    @Autowired
    private MsgProducer producer;

    private static Logger log = LoggerFactory.getLogger(EurekaClientApplication.class);

    private final RestTemplate restTemplate;

    @Autowired
    public MsgController(RestTemplateBuilder restTemplateBuilder,
                         RestTemplateResponseErrorHandler myResponseErrorHandler
    ) {

        this.restTemplate = restTemplateBuilder
                .errorHandler(myResponseErrorHandler)
                .build();
    }

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private Environment env;

    @PostMapping(value = "/refreshing", produces = "application/json; charset=UTF-8")
    public String checkRefresh() throws JsonProcessingException
    {
        return refresh() + getPropertiesClient();
    }

    @PostMapping(value = "/actuator/bus-refresh", produces = "application/json; charset=UTF-8")
    public String refresh()
    {
        return "Refreshed";
    }

    @GetMapping(value = "/properties", produces = "application/json; charset=UTF-8")
    public String getPropertiesClient() throws JsonProcessingException
    {
        Map<String, Object> props = new HashMap<>();
        CompositePropertySource bootstrapProperties = (CompositePropertySource)  ((AbstractEnvironment) env).getPropertySources().get("bootstrapProperties");
        for (String propertyName : bootstrapProperties.getPropertyNames()) {
            props.put(propertyName, bootstrapProperties.getProperty(propertyName));
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(props);
    }

    @RequestMapping(value = "/instances")
    public String getInstancesRun(){
        ServiceInstance instance = client.choose("map");
        return instance.getUri().toString();
    }

    @RequestMapping(value = "/countries/{id}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getCountry(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity httpEntity = new HttpEntity(headers);

        String url = getInstancesRun();
        log.info("Getting all details for country " + id + " from " + url);
        ResponseEntity response = restTemplate.exchange(String.format("%s/map/countries/%s", url, Long.toString(id)),
                HttpMethod.GET, httpEntity, Country.class, id);

        log.info("Info about country: " + id);

        if (response.getStatusCode() == HttpStatus.OK) {
            CountryMessage msg = new CountryMessage("Country was successfully got - " + id.toString(), OperationType.GET, "200", "");
            producer.sendCountryMsg(msg);
        }
        else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            CountryMessage msg = new CountryMessage("Country was unsuccessfully got - " + id.toString(), OperationType.GET, "404", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            CountryMessage msg = new CountryMessage("Internal server error when getting country - " + id.toString(), OperationType.GET, "500", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        else {
            CountryMessage msg = new CountryMessage("Something gone wrong when getting country - " + id.toString(), OperationType.GET, "", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }

        return response.getBody().toString();
    }

    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    public String getCountries() {
        String url = getInstancesRun();
        log.info("Getting all countries" + " from " + url);
        String response = this.restTemplate.exchange(String.format("%s/map/countries", url),
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }).getBody();

        return "All countries: \n" + response;
    }

    @RequestMapping(value = "/countries", method = RequestMethod.POST)
    public String createCountry(@RequestBody String object) {
        String url = getInstancesRun();
        log.info("Posting country from json from " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object, headers);

        ResponseEntity response = this.restTemplate.exchange(String.format("%s/map/countries", url),
                HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
                });
        if (response.getStatusCode() == HttpStatus.CREATED) {
            CountryMessage msg = new CountryMessage("Country was successfully created - ", OperationType.POST, "200", "");
            producer.sendCountryMsg(msg);
        }
        else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            CountryMessage msg = new CountryMessage("Internal server error when creating Country - ", OperationType.POST, "500", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        else {
            CountryMessage msg = new CountryMessage("Something gone wrong when creating Country - ", OperationType.POST, "", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        return "Posted country: \n" + response.getBody();
    }

    @RequestMapping(value = "/countries/{id}", method = RequestMethod.PUT)
    public String updateCountry(@RequestBody String object, @PathVariable Long id) {
        String url = getInstancesRun();
        log.info("Updating country from json from " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object, headers);

        String response = this.restTemplate.exchange(String.format("%s/map/countries/%s", url, id),
                HttpMethod.PUT, entity, new ParameterizedTypeReference<String>() {
                }, id).getBody();

        return "Updated country: \n" + response;
    }

    @RequestMapping(value = "/countries/{id}", method = RequestMethod.DELETE)
    public String deleteCountry(@PathVariable Long id) {
        String url = getInstancesRun();
        log.info("Deleting country from " + url);
        ResponseEntity response = this.restTemplate.exchange(String.format("%s/map/countries/%s", url, id),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
                }, id);
        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
            CountryMessage msg = new CountryMessage("Country was successfully deleted - " + id.toString(), OperationType.DELETE, "200", "");
            producer.sendCountryMsg(msg);
        }
        else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            CountryMessage msg = new CountryMessage("Country was not found when delete - " + id.toString(), OperationType.DELETE, "404", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            CountryMessage msg = new CountryMessage("Internal server error when deleting country - " + id.toString(), OperationType.DELETE, "500", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        else {
            CountryMessage msg = new CountryMessage("Something gone wrong when deleting country - " + id.toString(), OperationType.DELETE, "", response.getBody().toString());
            producer.sendCountryMsg(msg);
        }
        return "Deleted country: \n" + id + "\n" + response.getBody();
    }

    @RequestMapping(value = "/cities/{id}", method = RequestMethod.GET)
    public String getCity(@PathVariable Long id) {
        String url = getInstancesRun();
        log.info("Getting all details for City " + id + " from " + url);
        String response = this.restTemplate.exchange(String.format("%s/map/cities/%s", url, id),
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }, id).getBody();

        log.info("Info about City: " + response);

        return "Id -  " + id + " \n City Details " + response;
    }

    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    public String getCitys() {
        String url = getInstancesRun();
        log.info("Getting all Citys from " + url);
        String response = this.restTemplate.exchange(String.format("%s/map/cities", url),
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {
                }).getBody();

        return "All Citys: \n" + response;
    }

    @RequestMapping(value = "/cities", method = RequestMethod.POST)
    public String createCity(@RequestBody String object) {
        String url = getInstancesRun();
        log.info("Posting City from json from " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object, headers);

        String response = this.restTemplate.exchange(String.format("%s/map/cities", url),
                HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
                }).getBody();

        return "All Citys: \n" + response;
    }

    @RequestMapping(value = "/cities/{id}", method = RequestMethod.PUT)
    public String updateCity(@RequestBody String object, @PathVariable Long id) {
        String url = getInstancesRun();
        log.info("Updating City from json from " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(object, headers);

        String response = this.restTemplate.exchange(String.format("%s/map/cities/%s", url, id),
                HttpMethod.PUT, entity, new ParameterizedTypeReference<String>() {
                }, id).getBody();

        return "Updated City: \n" + response;
    }

    @RequestMapping(value = "/cities/{id}", method = RequestMethod.DELETE)
    public String deleteCity(@PathVariable Long id) {
        String url = getInstancesRun();
        log.info("Deleting City from " + url);
        String response = this.restTemplate.exchange(String.format("%s/map/cities/%s", url, id),
                HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {
                }, id).getBody();

        return "Deleted City: \n" + response;
    }

    @RequestMapping(value="/info-producer",method=RequestMethod.GET,produces="application/json")
    public String info()
    {
        ObjectNode root = producer.info();

        return root.toString();
    }
}
