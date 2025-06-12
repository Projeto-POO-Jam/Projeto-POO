package com.projetopoo.jam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "game_id"})
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User voteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game voteGame;

    public Vote() {

    }

    public long getVoteId() {
        return voteId;
    }

    public void setVoteId(long voteId) {
        this.voteId = voteId;
    }

    public User getUser() {
        return voteUser;
    }

    public void setUser(User user) {
        this.voteUser = user;
    }

    public Game getGame() {
        return voteGame;
    }

    public void setGame(Game game) {
        this.voteGame = game;
    }
}
