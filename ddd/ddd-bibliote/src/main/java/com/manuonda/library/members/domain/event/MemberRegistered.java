package com.manuonda.library.members.domain.event;

/**
 * MemberRegistered Event
 */
public record MemberRegistered(String memberId,
                               String email,
                               String name,
                               String phoneNUmber) {
}
