package com.tutorial.two.modulith.events.internal;

import com.tutorial.two.modulith.events.Meetup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface MeetupRepository extends ListCrudRepository<Meetup, Long> {

}
