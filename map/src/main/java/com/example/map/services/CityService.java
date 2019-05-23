package com.example.map.services;


import com.example.map.models.City;

import java.util.List;

public interface CityService {
    List<City> getAllCities();

    City getCityById(Long id);

    City createCity(City city);

    void deleteCity(Long id);

    City updateCity(City newCity, Long id);
}
