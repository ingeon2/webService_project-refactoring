package com.main.server.board.mapper;

import com.main.server.board.dto.BoardDto.Response;
import com.main.server.board.entity.Board;
import com.main.server.comment.entity.Comment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-07T17:43:24+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.18 (Azul Systems, Inc.)"
)
@Component
public class BoardMapperImpl implements BoardMapper {

    @Override
    public List<Response> pickListToResponse(List<Board> boards) {
        if ( boards == null ) {
            return null;
        }

        List<Response> list = new ArrayList<Response>( boards.size() );
        for ( Board board : boards ) {
            list.add( boardToResponse( board ) );
        }

        return list;
    }

    protected com.main.server.comment.dto.CommentDto.Response commentToResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        com.main.server.comment.dto.CommentDto.Response response = new com.main.server.comment.dto.CommentDto.Response();

        response.setContent( comment.getContent() );
        response.setCreatedAt( comment.getCreatedAt() );
        response.setCommentId( comment.getCommentId() );

        return response;
    }

    protected List<com.main.server.comment.dto.CommentDto.Response> commentListToResponseList(List<Comment> list) {
        if ( list == null ) {
            return null;
        }

        List<com.main.server.comment.dto.CommentDto.Response> list1 = new ArrayList<com.main.server.comment.dto.CommentDto.Response>( list.size() );
        for ( Comment comment : list ) {
            list1.add( commentToResponse( comment ) );
        }

        return list1;
    }

    protected Response boardToResponse(Board board) {
        if ( board == null ) {
            return null;
        }

        List<com.main.server.comment.dto.CommentDto.Response> comments = null;
        Long boardId = null;
        String title = null;
        String content = null;
        String address = null;
        LocalDateTime now = null;
        int bookmark = 0;
        String category = null;
        int pin = 0;
        int likeCheck = 0;
        long likeCount = 0L;
        int pick = 0;

        comments = commentListToResponseList( board.getComments() );
        boardId = board.getBoardId();
        title = board.getTitle();
        content = board.getContent();
        address = board.getAddress();
        now = board.getNow();
        bookmark = board.getBookmark();
        category = board.getCategory();
        pin = board.getPin();
        likeCheck = board.getLikeCheck();
        if ( board.getLikeCount() != null ) {
            likeCount = board.getLikeCount();
        }
        pick = board.getPick();

        Long memberId = null;
        String photo = null;
        String nickName = null;
        String userPhoto = null;
        List<com.main.server.board.dto.BoardTagDto.Response> tags = null;

        Response response = new Response( boardId, memberId, title, content, address, now, photo, bookmark, nickName, userPhoto, category, pin, likeCheck, likeCount, pick, tags, comments );

        return response;
    }
}
