package com.tutorial.two.modulith.events;


/**
 * Evento que publicaremos
 * @param id
 * @param name
 * @param email
 */
public record RsvpReceived(Long meetupId, String  name ,String email) {
}
