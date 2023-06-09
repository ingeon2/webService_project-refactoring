package com.main.server.auth.kakaooauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.server.auth.mail.MailService;
import com.main.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    private MailService mailService;
    private MemberRepository memberRepository;


    public void saveUser(ResponseEntity<String> kakaoUserInfo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        KakaoMember kakaoMember = objectMapper.readValue(kakaoUserInfo.getBody(), KakaoMember.class);


    }
}
