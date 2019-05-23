package com.example.listeners.events;

import com.example.map.models.CountryMessage;
import com.example.map.models.CityMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class EventsPublisher implements ApplicationEventPublisherAware {

    protected ApplicationEventPublisher appPublisher;

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher appPublisher) {

        this.appPublisher = appPublisher;
    }

    public void publishCountry(CountryMessage message)
    {
        CountryEvent evt = new CountryEvent(this, message);
        appPublisher.publishEvent(evt);
    }

    public void publishCity(CityMessage message)
    {
        CityEvent evt = new CityEvent(this, message);
        appPublisher.publishEvent(evt);
    }
}
