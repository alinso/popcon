package com.alinso.popcon.entity;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends BaseEntity {

    @ManyToOne
    private Photo photo;

    @ManyToOne
    private User writer;

    @Column
    private String comment;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
