package com.manuonda.library.members.domain.vo;

import com.manuonda.library.shared.AssertUtil;

/**
 * Email Value Object representing a member's email address.
 * @author dgarcia
 * @param email
 */
public record Email(String email) {

    private static final String STRING_EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

      public Email {
          AssertUtil.requireNotBlank(email, "Email cannot be blank");
          AssertUtil.requireNotNull(email, "Email cannot be null");
          AssertUtil.requirePattern(email, STRING_EMAIL_REGEX, "Email address be a valid email");
      }

      public static Email parse(String email) {
          return new Email(email);
      }

}

