package com.manuonda.library.members.application.dto.command;

public record AddMemberRequest(
        String email,
        String memberName,
        String phoneNumber,
        boolean blocked
) {
}
