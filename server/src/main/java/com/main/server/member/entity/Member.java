package com.main.server.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.main.server.audit.Auditable;
import com.main.server.board.entity.Board;
import com.main.server.bookmark.entity.Bookmark;
import com.main.server.comment.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Member extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column //주민등록번호
    private String RRN;

    @Column(name = "profile_image_url", nullable = true) //S3에 저장된 프로필 이미지의 URL
    private String profileImageUrl;


    @Enumerated(EnumType.STRING)
    private Question question;
    @Column
    private String answer;

    @Enumerated(EnumType.STRING)
    private Grade grade;
    @Column
    private Integer point;


    @ElementCollection(fetch = FetchType.EAGER) //db 왔다갔다하지말고 한번에 다 꺼내와라
    private List<String> roles = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE) //삭제할 때 같이 삭제
    private List<Board> boards = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }


}