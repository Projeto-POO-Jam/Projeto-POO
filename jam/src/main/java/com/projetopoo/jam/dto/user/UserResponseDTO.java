package com.projetopoo.jam.dto.user;

/**
 * Classe para retornar informações sobre os usuários para o frontend
 */
public class UserResponseDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userGitHub;
    private String userLinkedIn;
    private String userFacebook;
    private String userInstagram;
    private String userPhoto;
    private String userBanner;

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
}
