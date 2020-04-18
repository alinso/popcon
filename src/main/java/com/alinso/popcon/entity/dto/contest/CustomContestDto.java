package com.alinso.popcon.entity.dto.contest;

import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;

public class CustomContestDto {

    private Long id;

    private ProfileDto creator;

    private PhotoDto photo1;

    private PhotoDto photo2;

    private String title;

    private Boolean isActive;

    private  Integer maxVote;

    private Integer photo1VoteCount;

    private Integer photo2VoteCount;

    private Integer photo1Percet;

    public Integer getPhoto1VoteCount() {
        return photo1VoteCount;
    }

    public void setPhoto1VoteCount(Integer photo1VoteCount) {
        this.photo1VoteCount = photo1VoteCount;
    }

    public Integer getPhoto2VoteCount() {
        return photo2VoteCount;
    }

    public void setPhoto2VoteCount(Integer photo2VoteCount) {
        this.photo2VoteCount = photo2VoteCount;
    }

    public Integer getPhoto1Percet() {
        return photo1Percet;
    }

    public void setPhoto1Percet(Integer photo1Percet) {
        this.photo1Percet = photo1Percet;
    }

    public Integer getPhoto2Percent() {
        return photo2Percent;
    }

    public void setPhoto2Percent(Integer photo2Percent) {
        this.photo2Percent = photo2Percent;
    }

    private Integer photo2Percent;

    public ProfileDto getCreator() {
        return creator;
    }

    public void setCreator(ProfileDto creator) {
        this.creator = creator;
    }

    public PhotoDto getPhoto1() {
        return photo1;
    }

    public void setPhoto1(PhotoDto photo1) {
        this.photo1 = photo1;
    }

    public PhotoDto getPhoto2() {
        return photo2;
    }

    public void setPhoto2(PhotoDto photo2) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
