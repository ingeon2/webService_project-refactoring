package com.main.server.domain.like.repository;

import com.main.server.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface LikeRepository extends JpaRepository<Like, Long> {

        Optional<Like> findLikeByMemberIdAndBoardId(long memberId, Long boardId); //좋아요 누른사람+게시글
//        Optional<List<Like>> findLikesByMemberId(Long memberId); // 좋아요 누른 멤버 아이디찾기
}
