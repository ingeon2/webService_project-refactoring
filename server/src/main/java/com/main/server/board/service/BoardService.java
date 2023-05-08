package com.main.server.board.service;

import com.main.server.board.entity.Board;
import com.main.server.board.entity.BoardTag;
import com.main.server.board.repository.BoardRepository;
import com.main.server.board.repository.BoardTagRepository;
import com.main.server.tag.entity.Tag;
import com.main.server.tag.repository.TagRepository;
import com.main.server.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;

    private final TagService tagService;

    private final TagRepository tagRepository;




    public Board createBoard(Board board){
//        board.setLike(0L);
//        for(BoardTag x : board.getBoardTag()){
//
//            tagService.createTag(x.getTag());
//        }
        board = boardRepository.save(board);
//        createBoardTag(board);
        return board;
    }

//    private void createBoardTag(Board board){
//        for(BoardTag x : board.getBoardTag()){
//            x.setBoard(board);
//            x.setTag(tagRepository.findByTagName(x.getTag().getTagName()));
//            boardTagRepository.save(x);
//        }
//    }
}
