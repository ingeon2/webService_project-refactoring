package com.main.server.domain.board.entity;

import com.main.server.domain.comment.entity.Comment;
import com.main.server.domain.member.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    @Lob //일반적인 데이터베이스에서 저장하는 길이인 255개 이상의 문자를 저장하고 싶을 때 지정
    private String content;

    @Column()
    private String address;

    @Column()
    private String category;

    @Column(nullable = false)
    private LocalDateTime now = LocalDateTime.now();


    @Column()
    private int likeCheck;
    @Column()
    private Long likeCount;

    @ManyToOne
    @JoinColumn(name = "member_id")

    private Member member;


    @Column()
    private int bookmark;

    @Column()
    private int pin;

    @Column()
    private int pick;



    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<BoardTag> boardTag = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();



}
