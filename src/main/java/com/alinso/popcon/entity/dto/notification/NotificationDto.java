package com.alinso.popcon.entity.dto.notification;


import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.enums.NotificationType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class NotificationDto {

    private Long id;

    private ProfileDto trigger;

    private ProfileDto target;

    private String createdAtString;

    @Enumerated(EnumType.ORDINAL)
    private NotificationType notificationType;

    private Boolean isRead;

    private Long itemId;


    public ProfileDto getTrigger() {
        return trigger;
    }

    public void setTrigger(ProfileDto trigger) {
        this.trigger = trigger;
    }

    public ProfileDto getTarget() {
        return target;
    }

    public void setTarget(ProfileDto target) {
        this.target = target;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }


    public String getCreatedAtString() {
        return createdAtString;
    }

    public void setCreatedAtString(String createdAtString) {
        this.createdAtString = createdAtString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
