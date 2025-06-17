package com.projetopoo.jam.dto;

import org.springframework.web.multipart.MultipartFile;

public class GameResquestDTO {

    private String gameTitle;
    private String description;
    private MultipartFile gamePhoto;
    private MultipartFile gameFile;

    public GameResquestDTO() {

    }

    public String getGameTitle() { return gameTitle; }

    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public MultipartFile getGamePhoto() { return gamePhoto; }

    public void setGamePhoto(MultipartFile gamePhoto) { this.gamePhoto = gamePhoto; }

    public MultipartFile getGameFile() { return gameFile; }

    public void setGameFile(MultipartFile gameFile) { this.gameFile = gameFile; }
}
