package com.example.map.services;

import com.example.map.models.City;
import com.example.map.exceptions.NoSuchCountryException;
import com.example.map.exceptions.ResourceNotFoundException;
import com.example.map.exceptions.ValidationClass;
import com.example.map.exceptions.ValidationException;
import com.example.map.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityServiceImpl implements CityService{

    @Autowired
    private CityRepository cityRepository;
    private ValidationClass validationClass = new ValidationClass();

    @Override
    public List<City> getAllCities() {

        List<City> all = cityRepository.findAll();
        List<City> countries = new ArrayList<City>();
        for(City c : all)
        {
            if(c.getCity_deleted() == 0)
                countries.add(c);
        }
        return countries;
    }

    @Override
    public City getCityById(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City", "city_id", id));
    }

    @Override
    public City createCity(City city) {
        List<String> validateMessages = validationClass.validate(city);
        if (validateMessages.size() > 0) {
            throw new ValidationException(validateMessages);
        } else {
            City c;
            try
            {
                c = cityRepository.save(city);
            }
            catch (DataIntegrityViolationException e)
            {
                throw new NoSuchCountryException(city.getCountry_id());
            }
            return c;
        }
    }

    @Override
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id).get();
        if(city.getCity_deleted() == 1)
            throw new ResourceNotFoundException("City", "city_id", id);
        city.setCity_deleted(1);
        cityRepository.save(city);
    }

    @Override
    public City updateCity(City newCity, Long id) {
        List<String> validateMessages = validationClass.validate(newCity);
        if (validateMessages.size() > 0) {
            throw new ValidationException(validateMessages);
        }
            else {
            City c;
            try {
                return cityRepository.findById(id)
                        .map(city -> {
                            city.setCity_name(newCity.getCity_name());
                            city.setCountry_id(newCity.getCountry_id());
                            city.setCity_population(newCity.getCity_population());
                            return cityRepository.save(city);
                        })
                        .orElseGet(() -> {
                            newCity.setCity_id(id);
                            return cityRepository.save(newCity);
                        });
            }
            catch (DataIntegrityViolationException e) {
                throw new NoSuchCountryException(newCity.getCountry_id());
            }
        }
        }
}
