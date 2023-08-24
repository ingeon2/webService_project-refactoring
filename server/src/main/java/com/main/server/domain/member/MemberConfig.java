package com.main.server.domain.member;

import com.main.server.domain.member.mapper.MemberMapper;
import com.main.server.domain.member.repository.MemberRepository;
import com.main.server.domain.member.service.MemberService;
import com.main.server.domain.member.service.MemberServiceImpl1;
import com.main.server.global.auth.utils.CustomAuthorityUtils;
import com.main.server.global.awsS3.StorageService;
import com.main.server.global.mail.MailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MemberConfig {

    @Bean
    public MemberService memberService (MemberRepository memberRepository,
                                        MemberMapper memberMapper,
                                        PasswordEncoder passwordEncoder,
                                        JavaMailSender javaMailSender) {

        return new MemberServiceImpl1(memberRepository, passwordEncoder, authorityUtils(), memberMapper, storageService(), mailService(javaMailSender));
    }

    //위의 MemberServiceImpl1 생성을 위해 필요한 주입 요소들



    @Bean
    public CustomAuthorityUtils authorityUtils() {
        return new CustomAuthorityUtils();
    }

    @Bean
    public StorageService storageService() {
        return new StorageService();
    }

    @Bean
    public MailService mailService(JavaMailSender javaMailSender) {
        return new MailService(javaMailSender);
    }



}
