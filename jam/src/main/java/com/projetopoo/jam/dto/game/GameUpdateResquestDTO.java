package com.projetopoo.jam.dto.game;

import org.springframework.web.multipart.MultipartFile;

public class GameUpdateResquestDTO {

    private Long gameId;
    private String gameTitle;
    private String gameDescription;
    private String gameContent;
    private MultipartFile gamePhoto;
    private MultipartFile gameFile;


    public GameUpdateResquestDTO() {

    }

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() { return gameTitle; }

    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public String getGameDescription() { return gameDescription; }

    public void setGameDescription(String gameDescription) { this.gameDescription = gameDescription; }

    public MultipartFile getGamePhoto() { return gamePhoto; }

    public void setGamePhoto(MultipartFile gamePhoto) { this.gamePhoto = gamePhoto; }

    public MultipartFile getGameFile() { return gameFile; }

    public void setGameFile(MultipartFile gameFile) { this.gameFile = gameFile; }
}
