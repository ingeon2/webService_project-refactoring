package com.main.server.domain.member.service;

import com.main.server.global.mail.MailService;
import com.main.server.global.auth.utils.CustomAuthorityUtils;
import com.main.server.global.awsS3.StorageService;
import com.main.server.global.exception.BusinessLogicException;
import com.main.server.global.exception.ExceptionCode;
import com.main.server.domain.member.dto.MemberDto;
import com.main.server.domain.member.entity.Grade;
import com.main.server.domain.member.entity.Member;
import com.main.server.domain.member.mapper.MemberMapper;
import com.main.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.main.server.domain.member.entity.Grade.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl1 implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;
    private final MemberMapper memberMapper;
    private final StorageService storageService;
    private final MailService mailService;



    
    public Member createMember(Member member) {
        Member savedMember = memberRepository.save(member);
        return savedMember;
    }

    @Override
    public Member createMember(MemberDto.Post memberPostDto) { //mem 001

        //기존에 있는 회원인지 확인, 비밀번호와 비밀번호 확인이 일치하는지 확인, 닉네임 길이 괜찮은지, 닉네임 존재하는지 확인
        verifyExistEmail(memberPostDto.getEmail());
        verifyPassword(memberPostDto.getPassword(), memberPostDto.getPasswordConfirm());
        verifyExistNickName(memberPostDto.getNickname());
        verifyNickName(memberPostDto.getNickname());

        
        //패스워드 암호화 저장, PostDto에서 Member로 객체 변환
        String encryptedPassword = passwordEncoder.encode(memberPostDto.getPassword());

        Member member = memberMapper.memberPostDtoToMember(memberPostDto);
        member.setPassword(encryptedPassword);
        
        //member가 가지고 있는 이메일로 role 확인 후 저장
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        mailService.sendEmail(member.getEmail(), "반갑습니다!", "정말 반갑습니다!");
        log.info("이메일 전송 완료!");

        Member savedMember = memberRepository.save(member);

        //publisher.publishEvent(new MemberRegistraionApplicationEvent(this, savedMember));
        return savedMember;
    }


    public Member updateNickname(Member member) { //mem 005
        
        //받아온 멤버아이디 유효한지, 받아온 닉네임 유효성에 맞는지(존재안하면서 길이까지)
        Member findMember = findVerifiedMember(member.getMemberId());
        String newNickname = member.getNickname();
        verifyExistNickName(newNickname);
        verifyNickName(newNickname);
        
        //통과했으면 이제 멤버 닉네임 바꿔주기
        findMember.setNickname(newNickname);

        return memberRepository.save(findMember);
    }

    public Member updatePassword(MemberDto.PatchPassword memberPatchPasswordDto) { //mem 006
        //받아온 비밀번호와 비밀번호 확인 일치하는지, 기존의 비밀번호는 입력된 비밀번호와 일치하는지
        Member findMember = findVerifiedMember(memberPatchPasswordDto.getMemberId());
        
        //verifyPassword2(findVerifiedPassword(findMember.getMemberId()), memberPatchPasswordDto.getNowPassword(), passwordEncoder);
        verifyPassword(memberPatchPasswordDto.getNewPassword(), memberPatchPasswordDto.getPasswordConfirm());
        
        //통과햇스면 멤버 비밀번호 암호화 후 바꿔주기
        String encodePassword = passwordEncoder.encode(memberPatchPasswordDto.getNewPassword());
        findMember.setPassword(encodePassword);

        return memberRepository.save(findMember);
    }


    public Member updateProfileImage(long memberId, MultipartFile file) throws IOException { //mem 007
        Member findMember = findMember(memberId);

        String imageUrl = storageService.uploadFile(file, memberId);

        findMember.setProfileImageUrl(imageUrl);

        return memberRepository.save(findMember);
    }

    public Member deleteProfileImage(long memberId) {
        Member findMember = findMember(memberId);
        findMember.setProfileImageUrl(null);

        return memberRepository.save(findMember);
    }


    @Override
    public void deleteMember(long memberId) { //mem 009
        Member findMember = findVerifiedMember(memberId);
        memberRepository.delete(findMember);
    }

    public String findMemberEmail(String RRNConfirm) { //mem012
        //핸들러매서드에서 반환형이 Email String, singleResponseDto 이런걸로 내보내면 될듯
        Member findMember = findVerifiedMemberByRRN(RRNConfirm);

        return findMember.getEmail();
    }



    public String findMemberPassword(String email, String question, String answer) { //mem013
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member findMember = optionalMember.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        verifyQuestion(question, findMember.getQuestion().getValue());
        verifyAnswer(answer, findMember.getAnswer());

        String randomPassword = generateRandomPassword(findVerifiedPassword(findMember.getMemberId()));
        String encodePassword = passwordEncoder.encode(randomPassword);
        findMember.setPassword(encodePassword);

        memberRepository.save(findMember);

        return randomPassword;

    }

    @Override
    public Member findMember(long memberId) { //get 요청시 멤버찾고, 그 멤버에 맞는 Responsedto를 보내줄 예정
        return findVerifiedMember(memberId);
    }

    public void updateGrade(Member member) { //member의 grade 업데이트해주는 로직, 리팩터링
        member.setPoint(member.getBoards().size() * 10 + member.getComments().size() * 5);
        int point = member.getPoint();

        member.setGrade(findGrade(point));
    }






    
    

    //여기 아래 둘은 여기 클래스에서만 사용할 매서드

    private String generateRandomPassword(String beforePassword) { //새로운 비밀번호 생성매서드
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < 10 ; i++) {
            int randomIndex = random.nextInt(beforePassword.length());
            sb.append(beforePassword.charAt(randomIndex));
        }
        return sb.toString();
    }

    private void verifyQuestion(String question1, String question2) {
        if(!question1.equals(question2)) throw new BusinessLogicException(ExceptionCode.QUESTION_NOT_SAME);
    }

    private void verifyAnswer(String answer1, String answer2) { //답변 두개 일치하는지
        if(!answer1.equals(answer2)) throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_SAME);
    }



    private String findVerifiedPassword(long memberId) { //지금 패스워드 불러오기
        Member findMember = findVerifiedMember(memberId);
        return findMember.getPassword();
    }
    
    private void verifyPassword2(String password1, String password2, PasswordEncoder passwordEncoder) { //현재 비밀번호와 내가 작성한 비밀번호가 일치하는지
        if(!passwordEncoder.matches(password1, password2)) throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_SAME);
    }
    private void verifyPassword(String password1, String password2) { //비밀번호와 비밀번호 확인 둘이 일치하는지
        if(!password1.equals(password2)) throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_SAME);
    }

    private void verifyExistEmail(String email) { //존재하는 이메일인지
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()) throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    private void verifyNickName(String nickname) { //닉네임 길이 10 이하인지
        if(nickname.length() >= 10) throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_LONG);
    }
    private void verifyExistNickName(String nickname) { //존재하는 닉네임인지
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        if(optionalMember.isPresent()) throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_EXISTS);
    }
        private Member findVerifiedMember(long memberId) { //존재하는 회원인지
            Optional<Member> optionalMember = memberRepository.findById(memberId);
            Member findMember = optionalMember.orElseThrow(() ->
                    new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

            return findMember;
        }

    private Member findVerifiedMemberByRRN(String RRN) { //mem 012
        Optional<Member> optionalMember = memberRepository.findByRRN(RRN);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findMember;
    }

    private static Grade findGrade(int point) { //점수따라 등급제
        if(point > 50) return GRADE5;
        if(point > 40) return GRADE4;
        if(point > 30) return GRADE3;
        if(point > 20) return GRADE2;
        else return GRADE1;

    }
    

}