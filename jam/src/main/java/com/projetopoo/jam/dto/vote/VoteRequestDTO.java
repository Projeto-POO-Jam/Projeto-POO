package com.projetopoo.jam.dto.vote;

import jakarta.validation.constraints.NotNull;

/**
 * Classe para receber requisições dos votos do frontend
 */
public class VoteRequestDTO {
    @NotNull()
    private Long voteGameId;

    public Long getVoteGameId() {
        return voteGameId;
    }

    public void setVoteGameId(Long voteGameId) {
        this.voteGameId = voteGameId;
    }
}
