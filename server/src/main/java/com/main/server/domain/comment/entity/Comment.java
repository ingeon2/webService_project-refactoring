package com.main.server.domain.comment.entity;

import com.main.server.domain.board.entity.Board;
import com.main.server.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "comments")
public class Comment { //엔티티의 역할? 테이블 설계
    @Id //식별자
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "content")
    private String content;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @Column
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @ManyToOne(fetch = FetchType.LAZY) // 게시글과 댓글 - N:1 관계 설정
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Comment> children = new ArrayList<>();


}

//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "parent_id")
// private Comment parent;
//   this.parentCommentId = parentCommentId;
// @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
//  private List<Comment> children = new ArrayList<>();


// 생성자, 게터, 세터 생략


//보드와 커멘트의 관계는 1:N이다. 댓글이 기준이니깐
// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name = "board_id")
//private BoardEntity boardEntity;

