package com.alinso.popcon.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ToBeVoted extends BaseEntity {

    @ManyToOne
    private User voter;

    @ManyToOne
    private Photo photo1;

    @ManyToOne
    private Photo photo2;


    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Photo getPhoto1() {
        return photo1;
    }

    public void setPhoto1(Photo photo1) {
        this.photo1 = photo1;
    }

    public Photo getPhoto2() {
        return photo2;
    }

    public void setPhoto2(Photo photo2) {
        this.photo2 = photo2;
    }
}
