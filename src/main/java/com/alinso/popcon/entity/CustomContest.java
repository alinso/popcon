package com.alinso.popcon.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CustomContest extends BaseEntity {

    @ManyToOne
    private User creator;

    @ManyToOne
    private Photo photo1;

    @ManyToOne
    private Photo photo2;

    @Column
    private Integer photo1VoteCount;

    @Column
    private Integer photo2VoteCount;

    @Column
    private String title;

    @Column
    private Boolean isActive;

    @Column
    private  Integer maxVote;

    @Column
    private Integer voteCount;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getMaxVote() {
        return maxVote;
    }

    public void setMaxVote(Integer maxVote) {
        this.maxVote = maxVote;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getPhoto2VoteCount() {
        return photo2VoteCount;
    }

    public void setPhoto2VoteCount(Integer photo2VoteCount) {
        this.photo2VoteCount = photo2VoteCount;
    }

    public Integer getPhoto1VoteCount() {
        return photo1VoteCount;
    }

    public void setPhoto1VoteCount(Integer photo1VoteCount) {
        this.photo1VoteCount = photo1VoteCount;
    }
}
