package com.manuonda.library.members.domain.repository;


import com.manuonda.library.members.domain.dto.MemberSearchCriteria;
import com.manuonda.library.members.domain.model.Member;
import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.shared.PagedResult;

import java.util.Optional;

/**
 * Interface MemberRepository/port for managing Memeber Entities
 * @author: dgarcia
 * @version : 1.0
 * @since : 3/03/2026
 */
public interface MemberRepository {
   void create(Member member);
   void update(Member member);
   Optional<Member> findByEmail(String email);
   PagedResult<Member> searchMember(MemberSearchCriteria request);
   Optional<Member> findById(MemberId memberId);
}
