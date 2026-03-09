package com.manuonda.library.members.domain.dto;


/**
 * @author dgarcia
 * @version 1.0
 * @since   7/3/2026
 * @param name
 * @param email
 */
public record MemberSearchCriteria(String name, String email, int page, int size) {
}
