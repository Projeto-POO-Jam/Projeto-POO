package com.projetopoo.jam.dto.user;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;

public class UserResquestDTO {
    private String userName;
    private String userEmail;
    private String userPassword;
    private MultipartFile userPhoto;
    private MultipartFile userBanner;
    private String userGitHub;
    private String userLinkedIn;
    private String userFacebook;
    private String userInstagram;
    private String odlPassword;

    public UserResquestDTO() {

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

    public MultipartFile getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(MultipartFile userPhoto) {
        this.userPhoto = userPhoto;
    }

    public MultipartFile getUserBanner() {
        return userBanner;
    }

    public void setUserBanner(MultipartFile userBanner) {
        this.userBanner = userBanner;
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

    public String getOdlPassword() {
        return odlPassword;
    }

    public void setOdlPassword(String odlPassword) {
        this.odlPassword = odlPassword;
    }
}
