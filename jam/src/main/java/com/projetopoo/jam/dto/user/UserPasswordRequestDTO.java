package com.projetopoo.jam.dto.user;

/**
 * Classe para receber requisições de alterar senha do usuário do frontend
 */
public class UserPasswordRequestDTO {
    private String userOldPassword;
    private String userNewPassword;

    public String getUserOldPassword() {
        return userOldPassword;
    }

    public void setUserOldPassword(String userOldPassword) {
        this.userOldPassword = userOldPassword;
    }

    public String getUserNewPassword() {
        return userNewPassword;
    }

    public void setUserNewPassword(String userNewPassword) {
        this.userNewPassword = userNewPassword;
    }
}
