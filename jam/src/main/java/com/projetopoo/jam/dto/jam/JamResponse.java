package com.projetopoo.jam.dto.jam;

import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
import com.projetopoo.jam.model.JamStatus;

import java.time.LocalDateTime;

/**
 * Classe para retornar informações sobre as jams para o frontend
 */
public class JamResponse {
    private Long jamId;
    private String jamTitle;
    private String jamDescription;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private String jamContent;
    private String jamBackgroundColor;
    private String jamBackgroundCardColor;
    private String jamTextColor;
    private String jamLinkColor;
    private String jamCover;
    private String jamWallpaper;
    private String jamBanner;
    private JamStatus jamStatus;
    private Long jamTotalSubscribers;
    private UserWithCurrentResponseDTO jamUser;

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

    public String getJamBackgroundColor() {
        return jamBackgroundColor;
    }

    public void setJamBackgroundColor(String jamBackgroundColor) {
        this.jamBackgroundColor = jamBackgroundColor;
    }

    public String getJamBackgroundCardColor() {
        return jamBackgroundCardColor;
    }

    public void setJamBackgroundCardColor(String jamBackgroundCardColor) {
        this.jamBackgroundCardColor = jamBackgroundCardColor;
    }

    public String getJamTextColor() {
        return jamTextColor;
    }

    public void setJamTextColor(String jamTextColor) {
        this.jamTextColor = jamTextColor;
    }

    public String getJamLinkColor() {
        return jamLinkColor;
    }

    public void setJamLinkColor(String jamLinkColor) {
        this.jamLinkColor = jamLinkColor;
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

    public Long getJamTotalSubscribers() {
        return jamTotalSubscribers;
    }

    public void setJamTotalSubscribers(Long jamTotalSubscribers) {
        this.jamTotalSubscribers = jamTotalSubscribers;
    }

    public UserWithCurrentResponseDTO getJamUser() {
        return jamUser;
    }

    public void setJamUser(UserWithCurrentResponseDTO jamUser) {
        this.jamUser = jamUser;
    }
}
