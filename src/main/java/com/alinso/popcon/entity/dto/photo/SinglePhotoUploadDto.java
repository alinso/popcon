package com.alinso.popcon.entity.dto.photo;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class SinglePhotoUploadDto {

    MultipartFile file;

    String caption;

    List<Long> categoryIds;

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}