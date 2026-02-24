package com.manuonda.library.members.domain.model;


import com.manuonda.library.members.domain.vo.Email;
import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.members.domain.vo.MemberName;
import com.manuonda.library.shared.AggregateRoot;
import org.hibernate.sql.results.graph.embeddable.AggregateEmbeddableResultGraphNode;

/**
 * Class Member:
 * @author dgarcia
 * @version 1.0
 */
public class Member extends AggregateRoot {

    private MemberId memberId;
    private Email email;
    private MemberName memberName;
    private String phoneNumber;



    public void setMemberId(MemberId memberId) {
        this.memberId = memberId;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public void setMemberName(MemberName memberName) {
        this.memberName = memberName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
