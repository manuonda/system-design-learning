package com.manuonda.library.members.domain.exception;

import com.manuonda.library.shared.DomainException;

public class MemberBlockedException extends DomainException {
    public MemberBlockedException(String memberId) {
        super("Member with ID " + memberId + " is blocked and cannot perform this action.");
    }
}
