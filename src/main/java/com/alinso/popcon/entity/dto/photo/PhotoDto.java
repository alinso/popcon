package com.alinso.popcon.entity.dto.photo;

import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.dto.user.ProfileDto;

import java.util.List;

public class PhotoDto {

    private String fileName;

    private String caption;

    private Long id;

    private Integer percent;

    private ProfileDto user;

    private List<PhotoCategory> categories;

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
}
