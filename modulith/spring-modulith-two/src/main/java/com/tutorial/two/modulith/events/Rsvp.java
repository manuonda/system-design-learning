package com.tutorial.two.modulith.events;

import org.springframework.data.annotation.Id;

/**
 * Record mesa para confirmar
 * @param id
 * @param meetupId
 * @param name
 * @param email
 */
public record Rsvp(@Id Long id, Long meetupId, String name , String email) {
    public static Rsvp of(Long meetupId, String name, String email) {
        return new Rsvp(null, meetupId, name, email);
    }
}
