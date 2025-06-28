package com.projetopoo.jam.dto.comment;

import jakarta.validation.constraints.NotNull;

/**
 * Classe para receber requisições dos comentários do frontend
 */
public class CommentRequestDTO {

    @NotNull()
    private String commentText;

    @NotNull()
    private Long gameId;

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
