package com.example.map.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "countries")
@EntityListeners(AuditingEntityListener.class)
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long country_id;

    @NotBlank
    private String country_name;

    private Long country_population;
    private Long country_area;
    @NotNull
    private int country_deleted;

        @JsonIgnore
        @OneToMany
        @JoinColumn(name = "country_id")
        private List<City> cities;

    public Country(String name, Long country)
    {
        country_name = name;
        country_deleted = 0;
    }

    public  Country(){}

    public Long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(Long country_id) {
        this.country_id = country_id;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public List<City> getCities() {
        return cities;
    }

    public Long getCountry_area() {
        return country_area;
    }

    public void setCountry_area(Long country_area) {
        this.country_area = country_area;
    }

    public Long getCountry_population() {
        return country_population;
    }

    public void setCountry_population(Long country_population) {
        this.country_population = country_population;
    }

    public int getCountry_deleted() {
        return country_deleted;
    }

    public void setCountry_deleted(int country_deleted) {
        this.country_deleted = country_deleted;
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