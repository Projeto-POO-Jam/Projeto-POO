package com.projetopoo.jam.repository;

import com.projetopoo.jam.model.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface repository para classe Comment, responsável pelas funções relacionadas ao banco de dados
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Função que busca os comentários com base em um gameId, ele traz simultaneamente o User,
     * para evitar que uma segunda consulta seja feita para isso.
     * @param gameId Id do jogo que está sendo usado na consulta
     * @return Lista de comentários que possuem o gameId correspondente
     */
    @EntityGraph(value = "Comment.withUser")
    List<Comment> findByCommentGame_GameId(Long gameId);
}
