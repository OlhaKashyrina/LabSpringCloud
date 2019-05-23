package com.example.map.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "cities")
@EntityListeners(AuditingEntityListener.class)
public class City implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long city_id;

    @NotBlank
    private String city_name;

    @NotNull
    private Long country_id;

    private Long city_population;

    @NotNull
    private int city_deleted;

    public City(String name, Long country)
    {
        city_name = name;
        country_id = country;
        city_deleted = 0;
    }

    public  City(){}

    public Long getCity_id() {
        return city_id;
    }

    public void setCity_id(Long city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public Long getCity_population() {
        return city_population;
    }

    public void setCity_population(Long city_population) {
        this.city_population = city_population;
    }

    public int getCity_deleted() {
        return city_deleted;
    }

    public void setCity_deleted(int city_deleted) {
        this.city_deleted = city_deleted;
    }

    @Override
    public String toString()
    {
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "";
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonString;
    }
}