package com.manuonda.library.members.domain.model;


import com.manuonda.library.members.domain.vo.Email;
import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.members.domain.vo.MemberName;
import com.manuonda.library.shared.AggregateRoot;
import org.hibernate.sql.results.graph.embeddable.AggregateEmbeddableResultGraphNode;

/**
 * Class Member: Referente a un miembro de la biblioteca
 * @author dgarcia
 * @version 1.0
 */
public class Member extends AggregateRoot {

    private MemberId memberId;
    private Email email;
    private MemberName memberName;
    private String phoneNumber;

    public MemberId getMemberId() {
        return memberId;
    }

    public MemberName getMemberName() {
        return memberName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Email getEmail() {
        return email;
    }


    private void created(Member)


}
