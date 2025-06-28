package com.projetopoo.jam.dto.subscribe;

/**
 * Classe para receber requisições das inscrições do frontend
 */
public class SubscribeRequestDTO {
    private Long subscribeJamId;

    public Long getSubscribeJamId() {
        return subscribeJamId;
    }

    public void setSubscribeJamId(Long subscribeJamId) {
        this.subscribeJamId = subscribeJamId;
    }
}
