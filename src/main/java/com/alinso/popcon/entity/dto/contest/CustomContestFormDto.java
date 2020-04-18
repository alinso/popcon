package com.alinso.popcon.entity.dto.contest;

public class CustomContestFormDto {


    private Long photo1Id;

    private Long photo2Id;

    private String title;

    private Boolean isActive;

    private  Integer maxVote;

    public Long getPhoto1Id() {
        return photo1Id;
    }

    public void setPhoto1Id(Long photo1Id) {
        this.photo1Id = photo1Id;
    }

    public Long getPhoto2Id() {
        return photo2Id;
    }

    public void setPhoto2Id(Long photo2Id) {
        this.photo2Id = photo2Id;
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
}
