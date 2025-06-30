package com.projetopoo.jam.dto.subscribe;

import jakarta.validation.constraints.NotNull;

/**
 * Classe para receber requisições das inscrições do frontend
 */
public class SubscribeRequestDTO {
    @NotNull()
    private Long subscribeJamId;

    public Long getSubscribeJamId() {
        return subscribeJamId;
    }

    public void setSubscribeJamId(Long subscribeJamId) {
        this.subscribeJamId = subscribeJamId;
    }
}
