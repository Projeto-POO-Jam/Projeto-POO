package com.projetopoo.jam.dto.vote;

/**
 * Classe para receber requisições dos votos do frontend
 */
public class VoteRequestDTO {
    private Long voteGameId;

    public Long getVoteGameId() {
        return voteGameId;
    }

    public void setVoteGameId(Long voteGameId) {
        this.voteGameId = voteGameId;
    }
}
