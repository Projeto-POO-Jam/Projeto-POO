package com.projetopoo.jam.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserResquestDTO {
    private String userName;
    private String userEmail;
    private String userPassword;
    private MultipartFile userPhoto;

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
}
