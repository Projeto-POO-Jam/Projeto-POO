package com.projetopoo.jam.dto.jam;

import jakarta.validation.constraints.NotNull;

/**
 * Classe para receber requisições do frontend se for para atualizar os dados da jam
 */
public class JamUpdateRequestDTO extends JamRequestDTO{
    @NotNull()
    private Long jamId;

    public Long getJamId() {
        return jamId;
    }

    public void setJamId(Long jamId) {
        this.jamId = jamId;
    }
}
