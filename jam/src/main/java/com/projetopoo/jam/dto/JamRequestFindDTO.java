package com.projetopoo.jam.dto;

import com.projetopoo.jam.model.JamStatus;

import java.time.LocalDateTime;

public class JamRequestFindDTO {
    private Long jamId;

    public Long getJamId() {
        return jamId;
    }

    public void setJamId(Long jamId) {
        this.jamId = jamId;
    }
}
