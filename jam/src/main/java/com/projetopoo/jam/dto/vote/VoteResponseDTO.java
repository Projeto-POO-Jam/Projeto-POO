package com.projetopoo.jam.dto.vote;

/**
 * Classe para retornar informações sobre os votos para o frontend
 */
public class VoteResponseDTO {
    private Boolean voted;

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }
}
