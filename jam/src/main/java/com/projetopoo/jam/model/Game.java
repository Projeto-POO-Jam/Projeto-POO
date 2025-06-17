package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false)
    private String gameTitle;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String gamePhoto;

    @Column(nullable = false)
    private String gameFile;

    @OneToMany(mappedBy = "voteGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vote> gameVotes;

    @OneToOne(mappedBy = "subscribeGame")
    private Subscribe gameSubscribe;

    @OneToMany(mappedBy = "commentGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> gameComment;

    public Game() {

    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getGamePhoto() { return gamePhoto; }

    public void setGamePhoto(String gamePhoto) { this.gamePhoto = gamePhoto; }

    public String getGameFile() { return gameFile;}

    public void setGameFile(String gameFile) { this.gameFile = gameFile; }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<Vote> getGameVotes() {
        return gameVotes;
    }

    public void setGameVotes(List<Vote> gameVotes) {
        this.gameVotes = gameVotes;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Subscribe getGameSubscribe() {
        return gameSubscribe;
    }

    public void setGameSubscribe(Subscribe gameSubscribe) {
        this.gameSubscribe = gameSubscribe;
    }

    public List<Comment> getGameComment() {
        return gameComment;
    }

    public void setGameComment(List<Comment> gameComment) {
        this.gameComment = gameComment;
    }
}
