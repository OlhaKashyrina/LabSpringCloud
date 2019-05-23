package com.example.map.controllers;

import com.example.map.models.Country;
import com.example.map.services.CountryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/map")
public class CountryController {

    @Autowired
    CountryServiceImpl countryService;

    @RequestMapping(value = "/countries")
    public ResponseEntity<Object> getCountries() {
        return new ResponseEntity<>(countryService.getAllCountries(), HttpStatus.OK);
    }
    @RequestMapping(value = "/countries/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCountry(@PathVariable("id") Long id) {
        return new ResponseEntity<>(countryService.getCountryById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/countries/{id}/cities", method = RequestMethod.GET)
    public ResponseEntity<Object> getCitiesOfCountry(@PathVariable("id") Long id) {
        Country country = countryService.getCountryById(id);
        return new ResponseEntity<>(country.getCities(), HttpStatus.OK);
    }

    @RequestMapping(value = "/countries", method = RequestMethod.POST)
    public ResponseEntity<Object> createCountry(@RequestBody Country country) {
        countryService.createCountry(country);
        return new ResponseEntity<>("Country is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/countries/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object>
    updateCountry(@PathVariable("id") Long id, @RequestBody Country country) {

        countryService.updateCountry(country, id);
        return new ResponseEntity<>("Country is updated successfully", HttpStatus.OK);
    }
    @RequestMapping(value = "/countries/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCountry(@PathVariable("id") Long id) {
        countryService.deleteCountry(id);
        return new ResponseEntity<>("Country is deleted successfully", HttpStatus.OK);
    }
}