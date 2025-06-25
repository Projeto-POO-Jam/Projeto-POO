package com.projetopoo.jam.dto.game;

import java.util.List;

public class GamePaginatedResponseDTO {
    private List<GameSummaryDTO> games;
    private long total;

    public GamePaginatedResponseDTO(List<GameSummaryDTO> games, long total) {
        this.games = games;
        this.total = total;
    }

    public List<GameSummaryDTO> getGames() {
        return games;
    }

    public void setGames(List<GameSummaryDTO> games) {
        this.games = games;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
