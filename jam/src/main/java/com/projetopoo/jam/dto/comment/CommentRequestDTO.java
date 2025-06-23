package com.projetopoo.jam.dto.comment;

public class CommentRequestDTO {
    private String commentText;
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
