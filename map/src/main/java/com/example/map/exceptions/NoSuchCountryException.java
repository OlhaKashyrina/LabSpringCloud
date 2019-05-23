package com.example.map.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class NoSuchCountryException extends RuntimeException {
    private Long countryId;

    public NoSuchCountryException(Long countryId) {
        super(String.format("There is no country with this id (%s)", countryId));
        this.countryId = countryId;
    }

    public Long getCountryId() {
        return countryId;
    }
}