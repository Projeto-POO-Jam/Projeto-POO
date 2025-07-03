package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.util.List;

/**
 * Classe model de jogo, responsável pelo modelo de entidades do banco de dados.
 */
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(length = 60, nullable = false)
    private String gameTitle;

    @Column(length = 1000, nullable = false)
    private String gameDescription;

    @Column
    private String gamePhoto;

    @Column(nullable = false)
    private String gameFile;

    @Column(length = Integer.MAX_VALUE)
    private String gameContent;

    @Column(length = 50)
    private String gameToken;

    @OneToOne(mappedBy = "subscribeGame")
    private Subscribe gameSubscribe;

    @OneToMany(mappedBy = "voteGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vote> gameVotes;

    @OneToMany(mappedBy = "commentGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> gameComment;

    public String getGameContent() {
        return gameContent;
    }

    public void setGameContent(String gameContent) {
        this.gameContent = gameContent;
    }

    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String description) {
        this.gameDescription = description;
    }

    public String getGamePhoto() {
        return gamePhoto;
    }

    public void setGamePhoto(String gamePhoto) {
        this.gamePhoto = gamePhoto;
    }

    public String getGameFile() {
        return gameFile;
    }

    public void setGameFile(String gameFile) {
        this.gameFile = gameFile;
    }

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

    public String getGameToken() {
        return gameToken;
    }

    public void setGameToken(String gameToken) {
        this.gameToken = gameToken;
    }
}
