package com.manuonda.library.members.domain.vo;

import com.manuonda.library.shared.AssertUtil;

public record MemberId(String id) {

    public MemberId{
        AssertUtil.requireNotNull(id, "ID cannot be null");
        AssertUtil.requireNotBlank(id, "ID cannot be blank");

    }
}
