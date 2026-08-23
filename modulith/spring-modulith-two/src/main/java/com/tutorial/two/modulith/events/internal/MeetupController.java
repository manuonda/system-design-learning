package com.tutorial.two.modulith.events.internal;


import com.tutorial.two.modulith.events.Meetup;
import com.tutorial.two.modulith.events.Rsvp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/meetups")
public class MeetupController {


    private final MeetupService meetupService;

    public MeetupController(MeetupService meetupService) {
        this.meetupService = meetupService;
    }

    public record CreateMeetupRequest(String title, LocalDateTime date) {}

    public record RsvRequest(String name, String email){}

    @PostMapping
    public ResponseEntity<Meetup> create(@RequestBody CreateMeetupRequest request) {
        Meetup created = this.meetupService.create(new Meetup(null,request.title(),request.date()));
        return ResponseEntity.created(URI.create("/meetups" + created.id())).body(created);
    }

    @PostMapping("/{id}/rsvp")
    public ResponseEntity<Rsvp> rsvp(@PathVariable Long id,  @RequestBody RsvRequest rsvRequest) {
        Rsvp saved = meetupService.rsvp(id, rsvRequest.name(), rsvRequest.email());
        return ResponseEntity.ok().body(saved);
    }
}
