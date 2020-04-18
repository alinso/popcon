package com.alinso.popcon.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PhotoLike extends BaseEntity {

    @ManyToOne
    private User liker;

    @ManyToOne
    private  Photo photo;

    public User getLiker() {
        return liker;
    }

    public void setLiker(User liker) {
        this.liker = liker;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
