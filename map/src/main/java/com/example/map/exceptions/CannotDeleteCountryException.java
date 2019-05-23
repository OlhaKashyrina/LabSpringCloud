package com.example.map.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CannotDeleteCountryException extends RuntimeException {
    private String countryName;

    public CannotDeleteCountryException(String countryName) {
        super(String.format("Country %s contains cities and cannot be deleted", countryName));
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

}