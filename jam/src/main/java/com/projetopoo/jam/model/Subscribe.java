package com.projetopoo.jam.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscribes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "jam_id"})
})
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subscribeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User subscribeUser;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", unique = true)
    private Game subscribeGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam subscribeJam;

    public Subscribe() {

    }

    public long getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(long subscribeId) {
        this.subscribeId = subscribeId;
    }

    public User getSubscribeUser() {
        return subscribeUser;
    }

    public void setSubscribeUser(User subscribeUser) {
        this.subscribeUser = subscribeUser;
    }

    public Game getSubscribeGame() {
        return subscribeGame;
    }

    public void setSubscribeGame(Game subscribeGame) {
        this.subscribeGame = subscribeGame;
    }

    public Jam getSubscribeJam() {
        return subscribeJam;
    }

    public void setSubscribeJam(Jam subscribeJam) {
        this.subscribeJam = subscribeJam;
    }
}
