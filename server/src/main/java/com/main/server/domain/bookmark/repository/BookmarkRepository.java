package com.main.server.domain.bookmark.repository;

import com.main.server.domain.bookmark.entity.Bookmark;
import com.main.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByMemberAndBoardId (Member member, long boardId);
}
