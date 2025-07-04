package com.projetopoo.jam.model;

import jakarta.persistence.*;

/**
 * Classe model de inscrições, responsável pelo modelo de entidades do banco de dados.
 */
@Entity
@Table(name = "subscribes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "jam_id"})
})
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscribeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User subscribeUser;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", unique = true)
    private Game subscribeGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jam_id", nullable = false)
    private Jam subscribeJam;

    public Long getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(Long subscribeId) {
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
