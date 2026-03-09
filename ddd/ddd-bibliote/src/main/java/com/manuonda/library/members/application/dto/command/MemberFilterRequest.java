package com.manuonda.library.members.application.dto.command;

public record MemberFilterRequest(
        String email,
        int page,
        int size)
{
}
