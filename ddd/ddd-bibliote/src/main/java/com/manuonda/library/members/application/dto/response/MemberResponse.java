package com.manuonda.library.members.application.dto.response;

public record MemberResponse(
        String memberId,
        String email,
        String memberName,
        String phoneNumber,
        boolean blocked
) {
}
