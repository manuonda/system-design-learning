package com.tutorial.two.modulith.notifications.internal;

import org.springframework.data.annotation.Id;

public record Confirmation(@Id Long id, Long meetupId, String email) {
    public static Confirmation of(Long meetupId, String email) {
        return new Confirmation(null, meetupId, email);
    }
}

