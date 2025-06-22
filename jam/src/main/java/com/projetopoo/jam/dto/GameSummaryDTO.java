package com.projetopoo.jam.dto;

import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.JamStatus;

import java.time.LocalDateTime;

public class GameSummaryDTO {
    private Long gameId;
    private String gameTitle;
    private String gameDescription;
    private String gamePhoto;
    private String gameFile;
    private Long gameVoteTotal;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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

    public Long getGameVoteTotal() {
        return gameVoteTotal;
    }

    public void setGameVoteTotal(Long gameVoteTotal) {
        this.gameVoteTotal = gameVoteTotal;
    }
}
