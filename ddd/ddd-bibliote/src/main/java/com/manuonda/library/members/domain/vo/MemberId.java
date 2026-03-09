package com.manuonda.library.members.domain.vo;

import com.manuonda.library.members.domain.model.Member;
import com.manuonda.library.shared.AssertUtil;
import io.hypersistence.tsid.TSID;
import org.springframework.util.Assert;

/**
 * @author : dgarcia
 * @version : 1.0
 * @since: 7/03/2026
 * @param value
 */
public record MemberId(Long value) {

    public MemberId{
        AssertUtil.requireNotNull(value, "Member id cannot be null");
    }

    /**
     * Method static generate id
     * @return
     */
    public static MemberId generate(){
        return new MemberId(TSID.fast().toLong());
    }

    public static MemberId of(Long value) {
        return new MemberId(value);
    }
}
