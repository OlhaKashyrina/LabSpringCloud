package com.example.map.controllers;

import com.example.map.models.City;
import com.example.map.services.CityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/map")
public class CityController {

    @Autowired
    CityServiceImpl cityService;

    @RequestMapping(value = "/cities")
    public ResponseEntity<Object> getCities() {
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }
    @RequestMapping(value = "/cities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCity(@PathVariable("id") Long id) {
        return new ResponseEntity<>(cityService.getCityById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/cities", method = RequestMethod.POST)
    public ResponseEntity<Object> createCity(@RequestBody City city) {
        cityService.createCity(city);
        return new ResponseEntity<>("City is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/cities/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object>
    updateCity(@PathVariable("id") Long id, @RequestBody City city) {

        cityService.updateCity(city, id);
        return new ResponseEntity<>("City is updated successfully", HttpStatus.OK);
    }
    @RequestMapping(value = "/cities/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteCity(@PathVariable("id") Long id) {
        cityService.deleteCity(id);
        return new ResponseEntity<>("City is deleted successfully", HttpStatus.OK);
    }
}