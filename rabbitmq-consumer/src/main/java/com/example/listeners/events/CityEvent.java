package com.example.listeners.events;

import com.example.map.models.CityMessage;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class CityEvent extends ApplicationEvent {

    private CityMessage msg;

    public CityEvent(Object source, CityMessage msg) {
        super(source);
        this.msg = msg;
    }

    public CityMessage getMessage() {
        return msg;
    }

}
