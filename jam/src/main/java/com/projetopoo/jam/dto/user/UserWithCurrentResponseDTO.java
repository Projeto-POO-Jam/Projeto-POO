package com.projetopoo.jam.dto.user;

/**
 * Classe para retornar informações sobre os usuários para o frontend, extends UserResponseDTO para retornar se o usuário
 * é dono ou não do que está sendo requisitado.
 */
public class UserWithCurrentResponseDTO extends UserResponseDTO{
    private boolean userCurrent;

    public boolean isUserCurrent() {
        return userCurrent;
    }

    public void setUserCurrent(boolean userCurrent) {
        this.userCurrent = userCurrent;
    }
}
