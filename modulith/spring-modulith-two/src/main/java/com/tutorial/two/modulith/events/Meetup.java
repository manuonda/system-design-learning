package com.tutorial.two.modulith.events;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Reunion
 * @param id
 * @param title
 * @param date
 */
public record Meetup(@Id Long id, String title, LocalDateTime date) {

    public Meetup {

    }

    //fabrica static de method
    public static Meetup of(String title, LocalDateTime date) {
        return new Meetup(null, title, date);
    }

}
