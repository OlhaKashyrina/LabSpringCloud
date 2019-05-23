package com.example.map.services;


import com.example.map.models.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries();

    Country getCountryById(Long id);

    Country createCountry(Country country);

    void deleteCountry(Long id);

    Country updateCountry(Country newCountry, Long id);
}
