package com.projetopoo.jam.dto.user;

import jakarta.validation.constraints.NotNull;

/**
 * Classe para receber requisições de alterar senha do usuário do frontend
 */
public class UserPasswordRequestDTO {
    @NotNull
    private String userOldPassword;

    @NotNull
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
