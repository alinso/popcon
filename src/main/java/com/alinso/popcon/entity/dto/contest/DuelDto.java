package com.alinso.popcon.entity.dto.contest;


import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.enums.DuelStatus;

public class DuelDto {


    Long id;

    Integer readerPhotoVoteCount;
    Integer writerPhotoVoteCount;
    ProfileDto reader;
    ProfileDto writer;
    PhotoDto readerPhoto;
    PhotoDto writerPhoto;
    DuelStatus status;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    Boolean active;


    public Integer getReaderPhotoVoteCount() {
        return readerPhotoVoteCount;
    }

    public void setReaderPhotoVoteCount(Integer readerPhotoVoteCount) {
        this.readerPhotoVoteCount = readerPhotoVoteCount;
    }

    public Integer getWriterPhotoVoteCount() {
        return writerPhotoVoteCount;
    }

    public void setWriterPhotoVoteCount(Integer writerPhotoVoteCount) {
        this.writerPhotoVoteCount = writerPhotoVoteCount;
    }

    public PhotoDto getReaderPhoto() {
        return readerPhoto;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReaderPhoto(PhotoDto readerPhoto) {
        this.readerPhoto = readerPhoto;
    }

    public PhotoDto getWriterPhoto() {
        return writerPhoto;
    }

    public void setWriterPhoto(PhotoDto writerPhoto) {
        this.writerPhoto = writerPhoto;
    }

    public ProfileDto getReader() {
        return reader;
    }

    public void setReader(ProfileDto reader) {
        this.reader = reader;
    }

    public ProfileDto getWriter() {
        return writer;
    }

    public void setWriter(ProfileDto writer) {
        this.writer = writer;
    }


    public DuelStatus getStatus() {
        return status;
    }

    public void setStatus(DuelStatus status) {
        this.status = status;
    }
}
