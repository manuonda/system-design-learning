package com.manuonda.library.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean class for aggregate roots
 * @author dgarcia
 * @version 1.0
 * @date    20/1/2026
 */
public abstract class AggregateRoot {

    /**
     * List of domain events registered in the aggregate root
     * Events are registered when the aggregate root is modified
     * and are used to notify other aggregates or external systems
     */
    private final List<DomainEvent> domainEvents = new ArrayList<>();


    protected void register(DomainEvent event){
        if(event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        domainEvents.add(event);
    }
    
    /**
     * Pull the domain events registered in the aggregate root
     * and clear the list of domain events
     * @return the list of domain events
     */
    public List<DomainEvent> pullDomainEvents(){
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    /**
     * Check if the aggregate root has domain events registered
     * @return true if the aggregate root has domain events registered, false otherwise
     */
    public boolean hasDomainEvents(){
        return !domainEvents.isEmpty();
    }

}


