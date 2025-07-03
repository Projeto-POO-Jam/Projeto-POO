package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe model de jam, responsável pelo modelo de entidades do banco de dados.
 */
@Entity
@Table(name = "jams")
public class Jam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jamId;

    @Column(length = 60, nullable = false)
    private String jamTitle;

    @Column(length = 1000)
    private String jamDescription;

    @Column
    private LocalDateTime jamStartDate;

    @Column
    private LocalDateTime jamEndDate;

    @Column(length = Integer.MAX_VALUE)
    private String jamContent;

    @Column(length = 50)
    private String jamToken;

    @Column(length = 10)
    private String jamBackgroundColor;

    @Column(length = 10)
    private String jamBackgroundCardColor;

    @Column(length = 10)
    private String jamTextColor;

    @Column(length = 10)
    private String jamLinkColor;

    @Column
    private String jamCover;

    @Column
    private String jamWallpaper;

    @Column
    private String jamBanner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JamStatus jamStatus = JamStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User jamUser;

    @OneToMany(mappedBy = "subscribeJam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscribe> jamSubscribes ;

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

    public String getJamToken() {
        return jamToken;
    }

    public void setJamToken(String jamToken) {
        this.jamToken = jamToken;
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

    public User getJamUser() {
        return jamUser;
    }

    public void setJamUser(User jamUser) {
        this.jamUser = jamUser;
    }

    public List<Subscribe> getJamSubscribes() {
        return jamSubscribes;
    }

    public void setJamSubscribes(List<Subscribe> jamSubscribes) {
        this.jamSubscribes = jamSubscribes;
    }
}
