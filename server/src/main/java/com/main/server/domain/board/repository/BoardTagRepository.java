package com.main.server.domain.board.repository;

import com.main.server.domain.board.entity.Board;
import com.main.server.domain.board.entity.BoardTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface
BoardTagRepository extends JpaRepository<BoardTag, Long> {
    List<BoardTag> findByBoard(Board board);
}
