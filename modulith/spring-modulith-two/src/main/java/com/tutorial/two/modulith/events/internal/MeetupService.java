package com.tutorial.two.modulith.events.internal;


import com.tutorial.two.modulith.events.Meetup;
import com.tutorial.two.modulith.events.Rsvp;
import com.tutorial.two.modulith.events.RsvpReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetupService {

    private static final Logger log = LoggerFactory.getLogger(MeetupController.class);

    private final MeetupRepository meetupRepository;
    private final RsvpRepository rsvpRepository;
    private final ApplicationEventPublisher publisher;


    public MeetupService(MeetupRepository meetupRepository, RsvpRepository rsvpRepository, ApplicationEventPublisher publisher) {
        this.meetupRepository = meetupRepository;
        this.rsvpRepository = rsvpRepository;
        this.publisher = publisher;
    }


    //Creacion del meetup
    public Meetup create(Meetup meetup) {
        log.info("Creating meetup {}", meetup);
        Meetup created = this.meetupRepository.save(Meetup.of(meetup.title(), meetup.date()));
        log.info("Created meetup {}", created);
        return created;
    }

    //Confirma la asistencia del evento
    public Rsvp rsvp(Long meetupId, String name, String email){
        Rsvp saved = this.rsvpRepository.save(Rsvp.of(meetupId, name, email));
        log.info("Saved meetup {}", saved);
        this.publisher.publishEvent(new RsvpReceived(saved.id(),saved.name(),saved.email()));
        return saved;

    }
}
