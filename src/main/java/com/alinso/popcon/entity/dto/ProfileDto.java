package com.alinso.popcon.entity.dto;

import com.alinso.popcon.entity.City;
import com.alinso.popcon.entity.enums.Gender;

import java.util.Date;

public class ProfileDto {

    private String name;

    private String surname;

    private String username;

    private String bio;

    private String profilePicName;

    private City city;

    private Gender gender;

    private Integer age;

    private Date getBirthDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getGetBirthDate() {
        return getBirthDate;
    }

    public void setGetBirthDate(Date getBirthDate) {
        this.getBirthDate = getBirthDate;
    }


    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
