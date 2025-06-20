package com.projetopoo.jam.dto;

public class GameResponseDTO {

    private Long jamId;
    private String gameTitle;
    private String gameDescription;
    private String gameContent;
    private String gamePhoto;
    private String gameFile;
    private UserResponseDTO userResponseDTO;

    public GameResponseDTO() {

    }

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public UserResponseDTO getUserResponseDTO() {
        return userResponseDTO;
    }

    public void setUserResponseDTO(UserResponseDTO userResponseDTO) {
        this.userResponseDTO = userResponseDTO;
    }

    public Long getJamId() { return jamId; }

    public void setJamId(Long jamId) { this.jamId = jamId; }

    public String getGameTitle() { return gameTitle; }

    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public String getGameDescription() { return gameDescription; }

    public void setGameDescription(String gameDescription) { this.gameDescription = gameDescription; }

    public String getGamePhoto() { return gamePhoto; }

    public void setGamePhoto(String gamePhoto) { this.gamePhoto = gamePhoto; }

    public String getGameFile() { return gameFile; }

    public void setGameFile(String gameFile) { this.gameFile = gameFile; }

}
