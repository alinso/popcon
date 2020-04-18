package com.alinso.popcon.entity.dto.photo;

import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.enums.Gender;

import java.util.List;

public class PhotoDto {

    private String fileName;

    private String caption;

    private Long id;

    private Integer percent;

    private Gender gender;

    private ProfileDto user;

    private List<PhotoCategory> categories;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean didILikeIt;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUser(ProfileDto user) {
        this.user = user;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public ProfileDto getUser() {
        return user;
    }

    public List<PhotoCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PhotoCategory> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getDidILikeIt() {
        return didILikeIt;
    }

    public void setDidILikeIt(Boolean didILikeIt) {
        this.didILikeIt = didILikeIt;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
