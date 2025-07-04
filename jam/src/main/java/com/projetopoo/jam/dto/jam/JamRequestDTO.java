package com.projetopoo.jam.dto.jam;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Classe para receber requisições das jams do frontend
 */
public class JamRequestDTO {
    private String jamTitle;
    private String jamDescription;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private String jamContent;
    private String jamBackgroundColor;
    private String jamBackgroundCardColor;
    private String jamTextColor;
    private String jamLinkColor;
    private MultipartFile jamCover;
    private MultipartFile jamWallpaper;
    private MultipartFile jamBanner;

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

    public MultipartFile getJamCover() {
        return jamCover;
    }

    public void setJamCover(MultipartFile jamCover) {
        this.jamCover = jamCover;
    }

    public MultipartFile getJamWallpaper() {
        return jamWallpaper;
    }

    public void setJamWallpaper(MultipartFile jamWallpaper) {
        this.jamWallpaper = jamWallpaper;
    }

    public MultipartFile getJamBanner() {
        return jamBanner;
    }

    public void setJamBanner(MultipartFile jamBanner) {
        this.jamBanner = jamBanner;
    }
}
