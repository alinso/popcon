package com.alinso.popcon.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CustomContestVote extends BaseEntity {


    @ManyToOne
    private User voter;

    @ManyToOne
    private Photo selectedPhoto;

    @ManyToOne
    private Photo otherPhoto;

    @ManyToOne
    private CustomContest customContest;

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Photo getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setSelectedPhoto(Photo selectedPhoto) {
        this.selectedPhoto = selectedPhoto;
    }

    public Photo getOtherPhoto() {
        return otherPhoto;
    }

    public void setOtherPhoto(Photo otherPhoto) {
        this.otherPhoto = otherPhoto;
    }

    public CustomContest getCustomContest() {
        return customContest;
    }

    public void setCustomContest(CustomContest customContest) {
        this.customContest = customContest;
    }
}
