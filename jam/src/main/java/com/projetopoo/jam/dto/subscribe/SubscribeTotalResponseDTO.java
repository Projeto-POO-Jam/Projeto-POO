package com.projetopoo.jam.dto.subscribe;

/**
 * Classe para retornar informações sobre o total de inscrições para o frontend
 */
public class SubscribeTotalResponseDTO {
    private Long subscribeTotal;

    public Long getSubscribeTotal() {
        return subscribeTotal;
    }

    public void setSubscribeTotal(Long subscribeTotal) {
        this.subscribeTotal = subscribeTotal;
    }
}
