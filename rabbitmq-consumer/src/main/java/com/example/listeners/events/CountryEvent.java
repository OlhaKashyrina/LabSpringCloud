package com.example.listeners.events;

import com.example.map.models.CountryMessage;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class CountryEvent extends ApplicationEvent {

    private CountryMessage msg;

    public CountryEvent(Object source, CountryMessage msg) {
        super(source);
        this.msg = msg;
    }

    public CountryMessage getMessage() {
        return msg;
    }



}
