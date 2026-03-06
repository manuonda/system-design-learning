package com.manuonda.library.members.domain.exception;

import com.manuonda.library.shared.DomainException;

/**
 * Exception thrown when a member is blocked and attempts to perform an action that is not allowed for blocked members.
 * This is a domain exception that can be used by application and infrastructure layers for error handling.
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 * @date 5/03/2026
 */
public class MemberBlockedException extends DomainException {
    public MemberBlockedException(String memberId) {
        super("Member with ID " + memberId + " is blocked and cannot perform this action.");
    }
}
