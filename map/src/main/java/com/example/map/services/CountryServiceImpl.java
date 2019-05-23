package com.example.map.services;

import com.example.map.models.Country;
import com.example.map.exceptions.CannotDeleteCountryException;
import com.example.map.exceptions.ResourceNotFoundException;
import com.example.map.exceptions.ValidationClass;
import com.example.map.exceptions.ValidationException;
import com.example.map.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {
    @Autowired
    private CountryRepository countryRepository;
    private ValidationClass validationClass = new ValidationClass();

    @Override
    public List<Country> getAllCountries() {
        List<Country> all = countryRepository.findAll();
        List<Country> countries = new ArrayList<Country>();
        for(Country c : all)
        {
            if(c.getCountry_deleted() == 0)
                countries.add(c);
        }
        return countries;
    }

    @Override
    public Country getCountryById(Long id) {
        return countryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Country", "country_id", id));
    }

    @Override
    public Country createCountry(Country country) {
        List<String> validateMessages = validationClass.validate(country);
        if (validateMessages.size() > 0) {
            throw new ValidationException(validateMessages);
        }
        else
            return countryRepository.save(country);
    }

    @Override
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id).get();
        if(!country.getCities().isEmpty())
            throw new CannotDeleteCountryException(country.getCountry_name());
        if(country.getCountry_deleted() == 1)
            throw new ResourceNotFoundException("Country", "country_id", id);
        else
            //countryRepository.deleteById(id);
        {
            country.setCountry_deleted(1);
            countryRepository.save(country);
        }
    }

    @Override
    public Country updateCountry(Country newCountry, Long id) {
        List<String> validateMessages = validationClass.validate(newCountry);
        if (validateMessages.size() > 0) {
            throw new ValidationException(validateMessages);
        } else {
            return countryRepository.findById(id)
                    .map(country -> {
                        country.setCountry_name(newCountry.getCountry_name());
                        country.setCountry_area(newCountry.getCountry_area());
                        country.setCountry_population(newCountry.getCountry_population());
                        return countryRepository.save(country);
                    })
                    .orElseGet(() -> {
                        newCountry.setCountry_id(id);
                        return countryRepository.save(newCountry);
                    });
        }
    }
}
