package com.projetopoo.jam.dto.subscribe;

/**
 * Classe para retornar informações sobre as inscrições para o frontend
 */
public class SubscribeResponseDTO {
    private Boolean subscribed;

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }
}
