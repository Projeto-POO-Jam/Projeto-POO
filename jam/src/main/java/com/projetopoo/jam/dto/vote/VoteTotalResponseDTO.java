package com.projetopoo.jam.dto.vote;

/**
 * Classe para retornar informações sobre o total de votos para o frontend
 */
public class VoteTotalResponseDTO {
    private Long voteTotal;

    public Long getVoteTotal() {
        return voteTotal;
    }

    public void setVoteTotal(Long voteTotal) {
        this.voteTotal = voteTotal;
    }
}
