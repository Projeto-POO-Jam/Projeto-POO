package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true, nullable = false, length = 60)
    private String userNameLogin;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false, length = 60)
    private String userName;

    @Column
    private String userPhoto;

    @Column
    private String userEmail;

    @OneToMany(mappedBy = "voteUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vote> userVotes;

    @OneToMany(mappedBy = "commentUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> userComments;

    public User() {

    }

    public User(String userNameLogin, String userPassword, String userName, String userPhoto, String userEmail) {
        this.userNameLogin = userNameLogin;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userEmail = userEmail;
    }

    public User(int userId, String userNameLogin, String userPassword, String userName, String userPhoto, String userEmail) {
        this.userId = userId;
        this.userNameLogin = userNameLogin;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserNameLogin() {
        return userNameLogin;
    }

    public void setUserNameLogin(String userNameLogin) {
        this.userNameLogin = userNameLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
