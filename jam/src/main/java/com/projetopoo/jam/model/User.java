package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.util.List;

/**
 * Classe model de inscrições, responsável pelo modelo de entidades do banco de dados.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 60)
    private String userName;

    @Column(unique = true, nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @Column
    private String userGitHub;

    @Column
    private String userLinkedIn;

    @Column
    private String userFacebook;

    @Column
    private String userInstagram;

    @Column
    private String userPhoto;

    @Column
    private String userBanner;

    @OneToMany(mappedBy = "voteUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vote> userVotes;

    @OneToMany(mappedBy = "commentUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> userComments;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserGitHub() {
        return userGitHub;
    }

    public void setUserGitHub(String userGitHub) {
        this.userGitHub = userGitHub;
    }

    public String getUserLinkedIn() {
        return userLinkedIn;
    }

    public void setUserLinkedIn(String userLinkedIn) {
        this.userLinkedIn = userLinkedIn;
    }

    public String getUserFacebook() {
        return userFacebook;
    }

    public void setUserFacebook(String userFacebook) {
        this.userFacebook = userFacebook;
    }

    public String getUserInstagram() {
        return userInstagram;
    }

    public void setUserInstagram(String userInstagram) {
        this.userInstagram = userInstagram;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserBanner() {
        return userBanner;
    }

    public void setUserBanner(String userBanner) {
        this.userBanner = userBanner;
    }

    public List<Vote> getUserVotes() {
        return userVotes;
    }

    public void setUserVotes(List<Vote> userVotes) {
        this.userVotes = userVotes;
    }

    public List<Comment> getUserComments() {
        return userComments;
    }

    public void setUserComments(List<Comment> userComments) {
        this.userComments = userComments;
    }
}
