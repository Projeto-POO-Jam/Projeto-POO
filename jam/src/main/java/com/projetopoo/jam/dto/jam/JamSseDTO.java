package com.projetopoo.jam.dto.jam;

import com.projetopoo.jam.model.JamStatus;

import java.time.LocalDateTime;

/**
 * Classe para retornar informações sobre as jams no SSE para o frontend
 */
public class JamSseDTO {
    private Long jamId;
    private String jamTitle;
    private String jamDescription;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private JamStatus jamStatus;
    private Long jamTotalSubscribers;

    public String getJamTitle() {
        return jamTitle;
    }

    public void setJamTitle(String jamTitle) {
        this.jamTitle = jamTitle;
    }

    public String getJamDescription() {
        return jamDescription;
    }

    public void setJamDescription(String jamDescription) {
        this.jamDescription = jamDescription;
    }

    public LocalDateTime getJamStartDate() {
        return jamStartDate;
    }

    public void setJamStartDate(LocalDateTime jamStartDate) {
        this.jamStartDate = jamStartDate;
    }

    public LocalDateTime getJamEndDate() {
        return jamEndDate;
    }

    public void setJamEndDate(LocalDateTime jamEndDate) {
        this.jamEndDate = jamEndDate;
    }

    public Long getJamId() {
        return jamId;
    }

    public void setJamId(Long jamId) {
        this.jamId = jamId;
    }

    public JamStatus getJamStatus() {
        return jamStatus;
    }

    public void setJamStatus(JamStatus jamStatus) {
        this.jamStatus = jamStatus;
    }

    public Long getJamTotalSubscribers() {
        return jamTotalSubscribers;
    }

    public void setJamTotalSubscribers(Long jamTotalSubscribers) {
        this.jamTotalSubscribers = jamTotalSubscribers;
    }
}
