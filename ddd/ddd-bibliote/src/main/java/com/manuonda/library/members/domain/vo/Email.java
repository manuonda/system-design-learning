package com.manuonda.library.members.domain.vo;

import com.manuonda.library.shared.AssertUtil;

/**
 * Email Value Object representing a member's email address.
 * @author dgarcia
 * @param email
 */
public record Email(String email) {

      public Email {
          AssertUtil.requireNotBlank(email, "Email cannot be blank");
          AssertUtil.requireNotNull(email, "Email cannot be null");
      }

}

