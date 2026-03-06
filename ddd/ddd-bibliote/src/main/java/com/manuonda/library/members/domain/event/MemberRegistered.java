package com.manuonda.library.members.domain.event;

import com.manuonda.library.shared.DomainEvent;

/**
 * MemberRegistered Event
 * @author dgarcia
 * @version 1.0
 * @date 5/03/2026
 */
public record MemberRegistered(String memberId,
                               String email,
                               String name,
                               String phoneNUmber) implements DomainEvent {
}
