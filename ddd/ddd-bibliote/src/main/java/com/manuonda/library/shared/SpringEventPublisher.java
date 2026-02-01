package com.manuonda.library.shared;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 *
 */
@Service
public class SpringEventPublisher implements DomainEventPublisher{

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Collection<DomainEvent> events) {
       events.forEach(applicationEventPublisher::publishEvent);
    }
}
