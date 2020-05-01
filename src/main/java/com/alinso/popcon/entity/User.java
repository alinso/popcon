package com.alinso.popcon.entity;

import com.alinso.popcon.entity.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@NamedStoredProcedureQuery(
        name = "delete_user_sp",
        procedureName = "delete_user",
        parameters = {
                @StoredProcedureParameter(name = "userId", mode = ParameterMode.IN, type = Long.class),
        }
)
public class User extends BaseEntity implements UserDetails {

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String firebaseId;

    @Column
    private String username;

    @Column
    private String phone;

    @Column
    private Integer gain;

    @Column
    private Integer spent;

    @Column
    private String role;

    @Column
    private String profilePicName;

    @ManyToOne
    private City city;

    @Column
    private String phoneVerifyCode;

    @Column
    private Boolean isPhoneVerified;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    @Column
    private  String password;

    @Column
    private Integer correctGuessCount;

    @Column
    private Integer wrongGuessCount;

    @Column
    private String confirmPassword;

    @Column
    private boolean enabled;

    @Column
    private Date getBirthDate;

    @Column
    private String bio;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Gender preferredGender;

    public Integer getCorrectGuessCount() {
        return correctGuessCount;
    }

    public void setCorrectGuessCount(Integer correctGuessCount) {
        this.correctGuessCount = correctGuessCount;
    }

    public Integer getWrongGuessCount() {
        return wrongGuessCount;
    }

    public void setWrongGuessCount(Integer wrongGuessCount) {
        this.wrongGuessCount = wrongGuessCount;
    }

    public Date getGetBirthDate() {
        return getBirthDate;
    }

    public void setGetBirthDate(Date getBirthDate) {
        this.getBirthDate = getBirthDate;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public Date getBirthDate() {
        return getBirthDate;
    }

    public void setBirthDate(Date bdate) {
        this.getBirthDate = bdate;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getGain() {
        return gain;
    }


    public void setGain(Integer gain) {
        this.gain = gain;
    }

    public Integer getSpent() {
        return spent;
    }

    public void setSpent(Integer spent) {
        this.spent = spent;
    }


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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();

        SimpleGrantedAuthority user = new SimpleGrantedAuthority("ROLE_USER");
        updatedAuthorities.add(user);

        //if it is admin//
        if(this.getRole()!=null && this.getRole().equals("ROLE_ADMIN")){
            SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ROLE_ADMIN");
            updatedAuthorities.add(admin);
        }
        return updatedAuthorities;
    }

    @Override
    public String getPassword() {

        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return  enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneVerifyCode() {
        return phoneVerifyCode;
    }

    public void setPhoneVerifyCode(String phoneVerifyCode) {
        this.phoneVerifyCode = phoneVerifyCode;
    }

    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public Gender getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(Gender preferredGender) {
        this.preferredGender = preferredGender;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
