package com.alinso.popcon.entity.dto.comment;

import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;

public class CommentDto {

    private PhotoDto photoDto;
    private ProfileDto profileDto;
    private String comment;
    private Long id;

    public PhotoDto getPhotoDto() {
        return photoDto;
    }

    public void setPhotoDto(PhotoDto photoDto) {
        this.photoDto = photoDto;
    }

    public ProfileDto getProfileDto() {
        return profileDto;
    }

    public void setProfileDto(ProfileDto profileDto) {
        this.profileDto = profileDto;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
