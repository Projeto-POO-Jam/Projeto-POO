package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jams")
public class Jam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jamId;

    @Column(length = 60, nullable = false)
    private String jamTitle;

    @Column(length = 1000)
    private String jamDescription;

    @Column(length = 100)
    private String jamUrl;

    @Column
    private LocalDateTime jamStartDate;

    @Column
    private LocalDateTime jamEndDate;

    @Column
    private LocalDateTime jamVotingEndDate;

    @Lob
    @Column
    private String jamContent;

    @OneToMany(mappedBy = "subscribeJam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscribe> jamSubscribes ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User jamUser;

    public Jam() {

    }

    public int getJamId() {
        return jamId;
    }

    public void setJamId(int jamId) {
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

    public String getJamUrl() {
        return jamUrl;
    }

    public void setJamUrl(String jamUrl) {
        this.jamUrl = jamUrl;
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

    public LocalDateTime getjamVotingEndDate() {
        return jamVotingEndDate;
    }

    public void setjamVotingEndDate(LocalDateTime jamVotingEndDate) {
        this.jamVotingEndDate = jamVotingEndDate;
    }

    public String getJamContent() {
        return jamContent;
    }

    public void setJamContent(String jamContent) {
        this.jamContent = jamContent;
    }

    public List<Subscribe> getJamSubscribes() {
        return jamSubscribes;
    }

    public void setJamSubscribes(List<Subscribe> jamSubscribes) {
        this.jamSubscribes = jamSubscribes;
    }

    public User getJamUser() {
        return jamUser;
    }

    public void setJamUser(User jamUser) {
        this.jamUser = jamUser;
    }
}
