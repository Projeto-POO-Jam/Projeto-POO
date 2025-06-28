package com.projetopoo.jam.dto.user;

/**
 * Classe para receber requisições de criar usuário do frontend
 */
public class UserInsertRequestDTO extends UserRequestDTO {
    private String userPassword;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
