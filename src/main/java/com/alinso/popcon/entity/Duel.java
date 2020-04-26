package com.alinso.popcon.entity;

import com.alinso.popcon.entity.enums.DuelStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Duel extends BaseEntity {

    @ManyToOne
    private User writer;

    @Column
    Integer writerPhotoVoteCount;

    @Column
    private Date acceptDate;

    @Column
    Integer readerPhotoVoteCount;

    @ManyToOne
    private User reader;

    @ManyToOne
    private Photo writerPhoto;

    @ManyToOne
    private Photo readerPhoto;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private DuelStatus status;

    public Integer getWriterPhotoVoteCount() {
        return writerPhotoVoteCount;
    }

    public void setWriterPhotoVoteCount(Integer writerPhotoVoteCount) {
        this.writerPhotoVoteCount = writerPhotoVoteCount;
    }

    public Integer getReaderPhotoVoteCount() {
        return readerPhotoVoteCount;
    }

    public void setReaderPhotoVoteCount(Integer readerPhotoVoteCount) {
        this.readerPhotoVoteCount = readerPhotoVoteCount;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public User getReader() {
        return reader;
    }

    public void setReader(User reader) {
        this.reader = reader;
    }

    public Photo getWriterPhoto() {
        return writerPhoto;
    }

    public void setWriterPhoto(Photo writerPhoto) {
        this.writerPhoto = writerPhoto;
    }

    public Photo getReaderPhoto() {
        return readerPhoto;
    }

    public void setReaderPhoto(Photo readerPhoto) {
        this.readerPhoto = readerPhoto;
    }

    public DuelStatus getStatus() {
        return status;
    }

    public void setStatus(DuelStatus status) {
        this.status = status;
    }

    public Date getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }
}
