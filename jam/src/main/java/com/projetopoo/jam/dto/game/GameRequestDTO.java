package com.projetopoo.jam.dto.game;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Classe para receber requisições dos jogos do frontend
 */
public class GameRequestDTO {
    @NotNull()
    private Long jamId;

    @NotNull()
    private String gameTitle;

    private String gameDescription;
    private String gameContent;
    private MultipartFile gamePhoto;
    private MultipartFile gameFile;

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public Long getJamId() {
        return jamId;
    }

    public void setJamId(Long jamId) {
        this.jamId = jamId;
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

    public MultipartFile getGamePhoto() {
        return gamePhoto;
    }

    public void setGamePhoto(MultipartFile gamePhoto) {
        this.gamePhoto = gamePhoto;
    }

    public MultipartFile getGameFile() {
        return gameFile;
    }

    public void setGameFile(MultipartFile gameFile) {
        this.gameFile = gameFile;
    }
}
