package com.example.eurekaclient;

import com.example.map.models.CityMessage;
import com.example.map.models.CountryMessage;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class MsgProducer {

    @Autowired
    @Qualifier("rabbitTemplateCountry")
    private RabbitTemplate rabbitCountry;

    @Autowired
    @Qualifier("rabbitTemplateCity")
    private RabbitTemplate rabbitCity;

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgProducer.class);

    public void sendCountryMsg(CountryMessage msg)
    {
        try {
            LOGGER.debug("<<<<<< SENDING MESSAGE");
            rabbitCountry.convertAndSend(msg);
            LOGGER.debug(MessageFormat.format("MESSAGE SENT TO {0} >>>>>>", rabbitCountry.getRoutingKey()));

        } catch (AmqpException e) {
            LOGGER.error("Error sending Customer: ",e);
        }
    }

    public void sendCityMsg(CityMessage msg)
    {
        try {
            LOGGER.debug("<<<<< SENDING MESSAGE");
            rabbitCity.convertAndSend(msg);
            LOGGER.debug(MessageFormat.format("MESSAGE SENT TO {0} >>>>>>", rabbitCity.getRoutingKey()));
        } catch (AmqpException e) {
            LOGGER.error("Error sending Shop: ",e);
        }
    }

    public ObjectNode info()
    {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("host", rabbitCountry.getConnectionFactory().getHost());
        root.put("port", rabbitCountry.getConnectionFactory().getPort());
        root.put("Country UUID", rabbitCountry.getUUID());
        root.put("City UUID", rabbitCity.getUUID());
        root.put("queueCountry", rabbitCountry.getRoutingKey());
        root.put("queueCity", rabbitCity.getRoutingKey());

        return root;
    }
}
