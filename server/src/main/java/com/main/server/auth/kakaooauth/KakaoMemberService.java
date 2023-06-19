package com.main.server.auth.kakaooauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.server.auth.mail.MailService;
import com.main.server.member.entity.Member;
import com.main.server.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ComponentScan
public class KakaoMemberService {

    private final MailService mailService;
    private final MemberRepository memberRepository;

    public void saveUser(String kakaoUserInfo) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 데이터를 파싱하여 JsonNode 객체로 변환
            JsonNode jsonNode = objectMapper.readTree(kakaoUserInfo);
            System.out.println(jsonNode);
            // "nickname" 필드 추출
            String nickname = jsonNode.get("properties").get("nickname").asText();

            // "email" 필드 추출
            String email = jsonNode.get("kakao_account").get("email").asText();

            // 추출한 값을 출력 또는 원하는 대로 처리하는것.
            Member saveMember = new Member(email, nickname);
            memberRepository.save(saveMember);
            mailService.sendEmail(email, "ㅎㅇ", "ㅎㅇ");
            log.info("카카오 멤버 저장 완료");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
