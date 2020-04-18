package com.alinso.popcon.entity.dto.comment;

import javax.validation.constraints.NotBlank;

public class CommentFormDto {

    private Long photoId;


    @NotBlank(message = "Yorum boş olamaz")
    private String comment;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
