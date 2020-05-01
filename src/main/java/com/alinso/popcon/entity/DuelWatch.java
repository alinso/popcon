package com.alinso.popcon.entity;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DuelWatch extends  BaseEntity {


    @ManyToOne
    private Duel duel;

    @ManyToOne
    private User watcher;


    public Duel getDuel() {
        return duel;
    }

    public void setDuel(Duel duel) {
        this.duel = duel;
    }

    public User getWatcher() {
        return watcher;
    }

    public void setWatcher(User watcher) {
        this.watcher = watcher;
    }
}
