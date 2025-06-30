package com.projetopoo.jam.dto.comment;

import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;

import java.time.LocalDateTime;

/**
 * Classe para retornar informações sobre os comentários para o frontend
 */
public class CommentResponseDTO {
    private Long commentId;
    private String commentText;
    private LocalDateTime commentDate;
    private UserWithCurrentResponseDTO commentUser;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(LocalDateTime commentDate) {
        this.commentDate = commentDate;
    }

    public UserWithCurrentResponseDTO getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(UserWithCurrentResponseDTO commentUser) {
        this.commentUser = commentUser;
    }
}
