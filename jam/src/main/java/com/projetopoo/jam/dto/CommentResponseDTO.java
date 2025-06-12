package com.projetopoo.jam.dto;

import com.projetopoo.jam.model.User;

import java.time.LocalDateTime;

public class CommentResponseDTO {
    private long commentId;
    private String commentText;
    private LocalDateTime commentDate;
    private UserResponseDTO commentUser;

    public CommentResponseDTO() {

    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
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

    public UserResponseDTO getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(UserResponseDTO commentUser) {
        this.commentUser = commentUser;
    }
}
