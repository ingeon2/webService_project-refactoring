

package com.main.server.domain.comment.service;

import com.main.server.domain.comment.entity.Comment;
import com.main.server.domain.comment.mapper.CommentMapper;
import com.main.server.domain.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CommentService {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {//생성자 만들기
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public Comment createComment(Comment comment) { //댓글을 생성된걸 받는거
        if (comment.getParent() == null) {

        }
        return commentRepository.save(comment); //컨트롤러로객체반환 받은걸 다시 프론트에 반환

    }

    // 댓글 수정
    //

    //Optional<Comment> comment = commentRepository.findById(reqComment.getCommentId());
    //findById 를 통해 디비에서 댓글을 가져옴
    //Optional 은 만약 디비에 해당 댓글이 없을 경우 orElse() 를 사용해서 null을 넣어줌.
    //comment.orElse(null);

    public Comment updateComment(Comment comment) {
        Optional<Comment> originComment = commentRepository.findById(comment.getCommentId());

        Optional.ofNullable(comment.getContent())
                .ifPresent(contnet -> originComment.get().setContent(contnet));
        return commentRepository.save(originComment.get());
    }


    // 내용 바꿔주기


    //디비에서 에러가 났을때 다시 롤백해주는 기능
    @Transactional
    public void deleteComment(long commentId) {
        //  Optional<Comment> comment = commentRepository.findById(commentId);
        //  commentRepository.delete(comment.get());
        commentRepository.deleteById(commentId);
        //}
    }


}

