package com.manuonda.library.members.application.dto;


import com.manuonda.library.members.application.dto.command.AddMemberRequest;
import com.manuonda.library.members.application.dto.response.MemberResponse;
import com.manuonda.library.members.domain.exception.MemberNotFoundException;
import com.manuonda.library.members.domain.model.Member;
import com.manuonda.library.members.domain.repository.MemberRepository;
import com.manuonda.library.members.domain.vo.Email;
import com.manuonda.library.members.domain.vo.MemberId;
import com.manuonda.library.members.domain.vo.MemberName;
import com.manuonda.library.shared.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final DomainEventPublisher  domainEventPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    public MemberService(MemberRepository memberRepository, DomainEventPublisher  domainEventPublisher) {
        this.memberRepository = memberRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * Register e new member int he library
     * @param addMemberRequest
     * @return
     */
    public MemberResponse addMember(AddMemberRequest addMemberRequest) {
        LOGGER.debug("Adding member request: {}", addMemberRequest);

        Member member = Member.create(
                Email.parse(addMemberRequest.email()),
                MemberName.parse(addMemberRequest.memberName()),
                addMemberRequest.phoneNumber()
        );

        this.memberRepository.create(member);
        this.domainEventPublisher.publish(member.pullDomainEvents());
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long id) {
        LOGGER.debug("Finding member by id: {}", id);
        Member member = this.memberRepository.findById(MemberId.of(id))
                .orElseThrow(() -> new MemberNotFoundException())
    }
    private MemberResponse toResponse(Member member){
        MemberResponse response =
                new MemberResponse(
                   member.getMemberId().value().toString(),
                   member.getEmail().email(),
                   member.getMemberName().name(),
                   member.getPhoneNumber(),
                   member.isBlocked()
                );
        return response;
    }

}
