package com.manuonda.library.members.domain.model;


import com.manuonda.library.members.domain.event.MemberRegistered;
import com.manuonda.library.members.domain.vo.Email;
import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.members.domain.vo.MemberName;
import com.manuonda.library.shared.AggregateRoot;

/**
 * Aggregate Root representing a library member.
 * Encapsulates business rules related to membership and borrowing privileges.
 * @author dgarcia
 * @version 1.0
 * @since 5/03/2026
 */
public class Member extends AggregateRoot {

    private MemberId memberId;
    private Email email;
    private MemberName memberName;
    private String phoneNumber;
    private boolean blocked;

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

    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Private constructor - only factory methods should be used to create a Member
     * @param memberId    unique identifier generated via TSID
     * @param email       validated email value object
     * @param memberName  validated name value object
     * @param phoneNumber contact phone number
     */
    private Member(MemberId memberId, Email email,
                   MemberName memberName,
                   String phoneNumber) {
        this.memberId = memberId;
        this.email = email;
        this.memberName = memberName;
        this.phoneNumber = phoneNumber;
        this.blocked = false;
    }

    /**
     * Factory method to create a new Member.
     * Generates a TSID and registers a MemberRegistered domain event.
     * @param email       validated email of the member
     * @param memberName  validated name of the member
     * @param phoneNumber contact phone number
     * @return new Member instance with MemberRegistered event registered
     */
    public static Member create(Email email, MemberName memberName, String phoneNumber) {
        Member member  = new Member(MemberId.generate(), email, memberName, phoneNumber);
        member.register(new MemberRegistered(
                 member.memberId.value().toString(),
                 email.email(),
                 memberName.name(),
                 phoneNumber
        ));
        return member;
    }

    /**
     * Factory method to reconstitute a Member from persistence.
     * Does NOT register domain events.
     * @param memberId    existing member ID from database
     * @param email       member email
     * @param memberName  member name
     * @param phoneNumber member phone number
     * @param blocked     current blocked status
     * @return reconstituted Member instance
     */
    public static Member reconstitute(MemberId memberId, Email email,
                                      MemberName memberName,
                                      String phoneNumber,
                                      boolean blocked) {
        Member member = new Member(memberId, email, memberName, phoneNumber);
        member.blocked = blocked;
        return member;
    }

    /**
     * Business rule: a member can borrow a book only if not blocked.
     * A member is blocked when they have overdue loans.
     * @return true if member is allowed to borrow, false otherwise
     */
    public boolean canBorrowBook(){
        return !this.blocked;
    }

    /**
     * Business rule: block a member due to overdue loans.
     * A blocked member cannot borrow new books.
     */
    public void block(){
        this.blocked = true;
    }

    /**
     * Business rule: unblock a member once overdue loans are resolved.
     * Restores borrowing privileges.
     */
    public void unblock(){
        this.blocked = false;
    }
}
