package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "Comment.withUser")
    List<Comment> findByCommentGame_GameId(Long gameId);
}
