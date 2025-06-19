package com.projetopoo.jam.dto;

import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class JamRequestDTO {
    private Long jamId;
    private String jamTitle;
    private String jamDescription;
    private LocalDateTime jamStartDate;
    private LocalDateTime jamEndDate;
    private String jamContent;
    private MultipartFile jamCover;
    private MultipartFile jamWallpaper;
    private MultipartFile jamBanner;

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
