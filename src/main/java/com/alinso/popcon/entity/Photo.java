package com.alinso.popcon.entity;


import com.alinso.popcon.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Photo extends BaseEntity {

    @Column
    private String fileName;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private User user;


    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @NotNull(message = "En az bir kategori secmelisin")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<PhotoCategory> categories;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;


    @Column
    private Integer percent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<PhotoCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PhotoCategory> categories) {
        this.categories = categories;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
