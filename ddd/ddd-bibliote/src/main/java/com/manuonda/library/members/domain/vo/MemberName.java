package com.manuonda.library.members.domain.vo;

import com.manuonda.library.shared.AssertUtil;

public record MemberName(String name) {

    public MemberName {
        AssertUtil.requireNotBlank(name, "Member name cannot be empty");
    }

    public static MemberName parse(String name){
        return new MemberName(name);
    }
}
