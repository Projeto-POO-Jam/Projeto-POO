package com.projetopoo.jam.dto.game;

import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;

/**
 * Classe para retornar informações sobre os jogos para o frontend
 */
public class GameResponseDTO {
    private String gameTitle;
    private String gameDescription;
    private String gameContent;
    private String gamePhoto;
    private String gameFile;
    private UserWithCurrentResponseDTO userResponseDTO;

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public UserWithCurrentResponseDTO getUserResponseDTO() {
        return userResponseDTO;
    }

    public void setUserResponseDTO(UserWithCurrentResponseDTO userResponseDTO) {
        this.userResponseDTO = userResponseDTO;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }

    public String getGamePhoto() {
        return gamePhoto;
    }

    public void setGamePhoto(String gamePhoto) {
        this.gamePhoto = gamePhoto;
    }

    public String getGameFile() {
        return gameFile;
    }

    public void setGameFile(String gameFile) {
        this.gameFile = gameFile;
    }

}
