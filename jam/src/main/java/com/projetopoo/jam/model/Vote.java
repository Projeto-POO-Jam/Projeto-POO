package com.projetopoo.jam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "game_id"})
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User voteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game voteGame;

    public Vote() {

    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public User getVoteUser() {
        return voteUser;
    }

    public void setVoteUser(User user) {
        this.voteUser = user;
    }

    public Game getVoteGame() {
        return voteGame;
    }

    public void setVoteGame(Game game) {
        this.voteGame = game;
    }
}
