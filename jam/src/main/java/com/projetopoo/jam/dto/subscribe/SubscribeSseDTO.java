package com.projetopoo.jam.dto.subscribe;

/**
 * Classe para retornar informações sobre as inscrições no SSE para o frontend
 */
public class SubscribeSseDTO {
    private Long subscribeJamId;
    private Long subscribeTotal;

    public Long getSubscribeJamId() {
        return subscribeJamId;
    }

    public void setSubscribeJamId(Long subscribeJamId) {
        this.subscribeJamId = subscribeJamId;
    }

    public Long getSubscribeTotal() {
        return subscribeTotal;
    }

    public void setSubscribeTotal(Long subscribeTotal) {
        this.subscribeTotal = subscribeTotal;
    }
}
