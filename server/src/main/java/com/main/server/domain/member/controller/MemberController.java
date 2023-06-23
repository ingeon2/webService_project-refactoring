package com.main.server.domain.member.controller;

import com.main.server.domain.like.service.LikeService;
import com.main.server.domain.board.dto.BoardDto;
import com.main.server.domain.board.mapper.BoardMapper;
import com.main.server.domain.board.service.BoardService;
import com.main.server.domain.bookmark.service.BookmarkService;
import com.main.server.domain.comment.dto.CommentDto;
import com.main.server.domain.comment.mapper.CommentMapper;
import com.main.server.domain.member.service.MemberService;
import com.main.server.global.dto.SingleResponseDto;
import com.main.server.domain.member.dto.MemberDto;
import com.main.server.domain.member.entity.Member;
import com.main.server.domain.member.mapper.MemberMapper;
import com.main.server.global.utils.UriCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Validated
@Transactional
@RequestMapping("/members")
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final BoardService boardService;
    private final MemberMapper memberMapper;
    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;

    private final LikeService likeService;
    private final BookmarkService bookmarkService;

    public MemberController(MemberService memberService,
                            MemberMapper memberMapper,
                            BoardMapper boardMapper,
                            CommentMapper commentMapper,
                            LikeService likeService,
                            BookmarkService bookmarkService,
                            BoardService boardService) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.boardMapper = boardMapper;
        this.commentMapper = commentMapper;
        this.likeService = likeService;
        this.bookmarkService = bookmarkService;
        this.boardService = boardService;
    }

    //mem001
    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post memberPostDto) {
        Member createMember = memberService.createMember(memberPostDto); 
        //createMember 매서드 안의 private 매서드(유효성 검증) 때문에
        //Service   가 두번 호출되어 트랜잭션이 두번 될 수 있는거 아닌가?

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createMember.getMemberId());

        return ResponseEntity.created(location).build();
    }

    //mem005
    @PatchMapping("/nickname/{member-id}")
    public ResponseEntity patchMemberNickname(@PathVariable("member-id") @Positive long memberId,
                                               @RequestBody MemberDto.PatchNickname memberNicknamePatchDto) {
        memberNicknamePatchDto.setMemberId(memberId);
        Member patchMember = memberService.updateNickname(memberMapper.memberNicknamePatchDtoToMember(memberNicknamePatchDto));

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, patchMember.getMemberId());
        return ResponseEntity.created(location).build();
    }

    //mem006
    @PatchMapping("/password/{member-id}")
    public ResponseEntity patchMemberPassword(@PathVariable("member-id") @Positive long memberId,
                                              @RequestBody MemberDto.PatchPassword memberPasswordPatchDto) {
        memberPasswordPatchDto.setMemberId(memberId);
        Member patchMember = memberService.updatePassword(memberPasswordPatchDto);

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, patchMember.getMemberId());
        return ResponseEntity.created(location).build();
    }

    //mem007
    @PatchMapping("/image/{member-id}")
    public ResponseEntity patchMemberImage(@PathVariable("member-id") @Positive long memberId,
                                           @RequestParam(value = "file") MultipartFile file) throws IOException {
        Member patchMember = memberService.updateProfileImage(memberId, file);

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, patchMember.getMemberId());
        return ResponseEntity.created(location).build();
    }

    //프로필사진 삭제
    @PatchMapping("/deleteImage/{member-id}")
    public ResponseEntity patchMemberImageDelete(@PathVariable("member-id") long memberId) {
        Member patchMember = memberService.deleteProfileImage(memberId);

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, patchMember.getMemberId());
        return ResponseEntity.created(location).build();
    }


    //mem009
    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive long memberId) {
        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    //mem012 새 패스워드 보여주기
    @GetMapping("/password")
    public ResponseEntity getMemberPassword(@RequestParam("email") String email,
                                            @RequestParam("question") String question,
                                            @RequestParam("answer") String answer) {
        String memberPassword = memberService.findMemberPassword(email, question, answer);
        SingleResponseDto<String> response = new SingleResponseDto<>(memberPassword);

        return ResponseEntity.ok(response);
    }
    
    //mem013 아이디 보여드릴게
    @GetMapping("/id")
    public ResponseEntity getMemberId(@RequestParam("RRNConfirm") String RRNConfirm) {
        String memberEmail = memberService.findMemberEmail(RRNConfirm);

        SingleResponseDto<String> response = new SingleResponseDto<>(memberEmail);

        return ResponseEntity.ok(response);
    }

    //마이페이지 get요청
    @GetMapping("/mypage/{member-id}")
    public ResponseEntity getMemberMyPage(@Positive @PathVariable("member-id") long memberId) {
        MemberDto.GetMyPage myPageDto = memberMapper.memberToMyPageDto(memberService.findMember(memberId));
        Member findMember = memberService.findMember(memberId);


        List<BoardDto.Response> boardResponse = findMember.getBoards().stream()
                        .map(board -> boardMapper.boardToBoardResponse(board, likeService, bookmarkService, memberId))
                                .collect(Collectors.toList());
        myPageDto.setBoards(boardResponse);



        List<CommentDto.MyPageResponse> commentResponse = findMember.getComments().stream()
                        .map(comment -> commentMapper.commentToCommentMyPageDto(comment))
                                .collect(Collectors.toList());
        myPageDto.setComments(commentResponse);



        List<Long> boardIds = findMember.getBookmarks().stream()
                .map(bookmark -> bookmark.getBoardId())
                    .collect(Collectors.toList());

        myPageDto.setBookmarkBoardIds(boardIds);

        return new ResponseEntity<>(myPageDto, HttpStatus.OK);
    }

    //리팩터링
    @PatchMapping("/grade/{member-id}")
    public ResponseEntity patchMemberImage(@PathVariable("member-id") @Positive long memberId) throws IOException {
        Member patchMember = memberService.findMember(memberId);
        memberService.updateGrade(patchMember);

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, patchMember.getMemberId());
        return ResponseEntity.created(location).build();
    }

}
