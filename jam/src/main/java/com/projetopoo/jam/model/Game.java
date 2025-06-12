package com.projetopoo.jam.model;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @OneToMany(mappedBy = "voteGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vote> gameVotes;

    @OneToOne(mappedBy = "subscribeGame")
    private Subscribe gameSubscribe;

    @Column(nullable = false)
    private String gameTitle;

    @OneToMany(mappedBy = "commentGame", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> gameComment;

    public Game() {

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
}
