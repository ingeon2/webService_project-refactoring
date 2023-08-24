package com.main.server.domain.member.service;

import com.main.server.domain.member.dto.MemberDto;
import com.main.server.domain.member.entity.Member;

public interface MemberService {
    public Member createMember(MemberDto.Post memberPostDto);
    public void deleteMember(long memberId);

    public Member findMember(long memberId);

}
