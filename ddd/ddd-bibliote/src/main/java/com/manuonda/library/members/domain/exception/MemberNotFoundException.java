package com.manuonda.library.members.domain.exception;

import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.shared.DomainException;

public class MemberNotFoundException extends DomainException {
    public MemberNotFoundException(MemberId memberId) {
        super("Member with ID " + memberId.id() + " not found.");
    }
}
