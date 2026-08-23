package com.tutorial.two.modulith.events.internal;

import com.tutorial.two.modulith.events.Rsvp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface RsvpRepository extends ListCrudRepository<Rsvp, Long> {
}
