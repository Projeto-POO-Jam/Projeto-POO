package com.projetopoo.jam.dto.user;

public class UserWithIdResponseDTO extends UserResponseDTO{
    private boolean userCurrent;

    public boolean isUserCurrent() {
        return userCurrent;
    }

    public void setUserCurrent(boolean userCurrent) {
        this.userCurrent = userCurrent;
    }
}
