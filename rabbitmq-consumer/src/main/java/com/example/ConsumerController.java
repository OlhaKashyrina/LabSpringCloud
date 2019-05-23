package com.example;

import com.example.listeners.events.CountryEvent;
import com.example.listeners.events.CityEvent;
import com.example.map.models.CityMessage;
import com.example.map.models.CountryMessage;
import com.example.map.models.Message;
import com.example.map.models.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ConsumerController {
    private List<SseEmitter> lsEmitters = new ArrayList<SseEmitter>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);

    @Autowired
    private MessageRepository msgRepo;

    @EventListener({CountryEvent.class})
    public void handleCountryEvt(CountryEvent evt)
    {
        System.out.println("EVENT RECEIVED: " + evt.getMessage().getDescription());
        CountryMessage message = evt.getMessage();
        msgRepo.save(new Message(message.getDescription(), message.getOperationType(), message.getStatusCode(), message.getError()));
        List<SseEmitter> deadEmitters = new ArrayList<SseEmitter>();
        this.lsEmitters.forEach(emitter -> {
            try {
                emitter.send(message);
            }
            catch (Exception e) {
                LOGGER.error("Error ",e);
                deadEmitters.add(emitter);
            }
        });

        this.lsEmitters.removeAll(deadEmitters);
    }

    @EventListener({CityEvent.class})
    public void handleCityEvt(CityEvent evt)
    {
        System.out.println("EVENT RECEIVED: " + evt.getMessage().getDescription());
        CityMessage message = evt.getMessage();
        this.msgRepo.save(new Message(message.getDescription(), message.getOperationType(), message.getStatusCode(), message.getError()));

        List<SseEmitter> deadEmitters = new ArrayList<SseEmitter>();
        this.lsEmitters.forEach(emitter -> {
            try {
                emitter.send(message);
            }
            catch (Exception e) {
                LOGGER.error("Error ",e);
                deadEmitters.add(emitter);
            }
        });

        this.lsEmitters.removeAll(deadEmitters);
    }
}
