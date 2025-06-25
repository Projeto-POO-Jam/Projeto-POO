package com.projetopoo.jam.dto.jam;

import com.projetopoo.jam.model.JamStatus;

import java.time.LocalDateTime;

public class JamSummaryDTO {
    private Long jamId;
    private String jamTitle;
    private String jamBanner;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private JamStatus jamStatus;
    private Long jamTotalSubscribers;

    public Long getJamId() {
        return jamId;
    }

    public void setJamId(Long jamId) {
        this.jamId = jamId;
    }

    public String getJamTitle() {
        return jamTitle;
    }

    public void setJamTitle(String jamTitle) {
        this.jamTitle = jamTitle;
    }

    public String getJamBanner() {
        return jamBanner;
    }

    public void setJamBanner(String jamBanner) {
        this.jamBanner = jamBanner;
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

    public Long getJamTotalSubscribers() {
        return jamTotalSubscribers;
    }

    public void setJamTotalSubscribers(Long jamTotalSubscribers) {
        this.jamTotalSubscribers = jamTotalSubscribers;
    }

    public JamStatus getJamStatus() {
        return jamStatus;
    }

    public void setJamStatus(JamStatus jamStatus) {
        this.jamStatus = jamStatus;
    }
}
