package com.projetopoo.jam.dto;

import com.projetopoo.jam.model.JamStatus;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

public class JamResponse {
    private Long jamId;
    private String jamTitle;
    private String jamDescription;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private String jamContent;
    private String jamCover;
    private String jamWallpaper;
    private String jamBanner;
    private JamStatus jamStatus;
    private long jamTotalSubscribers;
    private UserResponseDTO jamUser;

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

    public String getJamContent() {
        return jamContent;
    }

    public void setJamContent(String jamContent) {
        this.jamContent = jamContent;
    }

    public String getJamCover() {
        return jamCover;
    }

    public void setJamCover(String jamCover) {
        this.jamCover = jamCover;
    }

    public String getJamWallpaper() {
        return jamWallpaper;
    }

    public void setJamWallpaper(String jamWallpaper) {
        this.jamWallpaper = jamWallpaper;
    }

    public String getJamBanner() {
        return jamBanner;
    }

    public void setJamBanner(String jamBanner) {
        this.jamBanner = jamBanner;
    }

    public JamStatus getJamStatus() {
        return jamStatus;
    }

    public void setJamStatus(JamStatus jamStatus) {
        this.jamStatus = jamStatus;
    }

    public long getJamTotalSubscribers() {
        return jamTotalSubscribers;
    }

    public void setJamTotalSubscribers(long jamTotalSubscribers) {
        this.jamTotalSubscribers = jamTotalSubscribers;
    }

    public UserResponseDTO getJamUser() {
        return jamUser;
    }

    public void setJamUser(UserResponseDTO jamUser) {
        this.jamUser = jamUser;
    }
}
