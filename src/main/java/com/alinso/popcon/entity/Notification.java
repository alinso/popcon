package com.alinso.popcon.entity;


import com.alinso.popcon.entity.enums.NotificationType;

import javax.persistence.*;

@Entity
public class Notification extends BaseEntity {

    @ManyToOne
    private User trigger;

    @ManyToOne
    private User target;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private NotificationType notificationType;

    @Column
    private Boolean isRead;

    @Column
    private Long itemId;


    public User getTrigger() {
        return trigger;
    }

    public void setTrigger(User trigger) {
        this.trigger = trigger;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
