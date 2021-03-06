package com.alinso.popcon.service;


import com.alinso.popcon.entity.Notification;
import com.alinso.popcon.entity.Duel;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.notification.NotificationDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.enums.NotificationType;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.pushNotification.AndroidPushNotificationsService;
import com.alinso.popcon.repository.NotificationRepository;
import com.alinso.popcon.util.DateUtil;
import com.alinso.popcon.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    //Logged user do the thing and notification goes to other user,
    // so when creating notification object logged user is trigger - other user is target

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BlockService blockService;


    @Autowired
    UserService userService;


    public void deleteById(Long id){
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Notification n  = notificationRepository.findById(id).get();

        if(n.getTarget().getId()==loggedUser.getId()){
            notificationRepository.deleteById(id);
        }
    }


    private void createNotification(User target, User trigger, NotificationType notificationType,Long itemId){


        if(blockService.isThereABlock(target.getId()))
            throw new UserWarningException("Erişim Yok");


        Notification notification = new Notification();
        notification.setNotificationType(notificationType);
        notification.setTarget(target);
        notification.setItemId(itemId);
        notification.setTrigger(trigger);
        notification.setRead(false);
        notificationRepository.save(notification);
    }


    public void newMessage(User target){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.MESSAGE,null);
        androidPushNotificationsService.newMessage(trigger,target);

    }

    public void newDuelRequest(User target, Duel duel){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.DUEL_REQUEST, duel.getId());
        androidPushNotificationsService.newDuelRequest(trigger,target);

    }
    public void newDuelAccept(User target, Duel duel){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.DUEL_ACCEPT, duel.getId());
        androidPushNotificationsService.newDuelAccept(trigger,target);

    }
    public void newDuelDecline(User target, Duel duel){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.DUEL_DECLINE, duel.getId());
        androidPushNotificationsService.newDuelDecline(trigger,target);
    }
//    public void newPhotoLike(User target, Long itemId){
//        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        createNotification(target,trigger,NotificationType.PHOTO_LIKE, itemId);
//    }

    public void newFollow(User target){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.FOLLOW,null);
        androidPushNotificationsService.newFollow(trigger,target);

    }

    public void newCommment(User target,Long itemId){
        User trigger = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        createNotification(target,trigger,NotificationType.COMMENT,itemId);
        androidPushNotificationsService.newComment(trigger,target);
    }
    public void showPercent(User target,Long itemId){
        createNotification(target,null,NotificationType.SHOW_PERCENT,itemId);
    }

    public void duelFinished(long id, User watcher) {
        createNotification(watcher,null,NotificationType.FOLLOW,id);
        androidPushNotificationsService.newDuelFinish(watcher);
    }



    public void read(Long id){

        Notification notification = notificationRepository.findById(id).get();
        UserUtil.checkUserOwner(notification.getTarget().getId());


        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<NotificationDto> findLoggedUserNotReadedNotifications(){
        //now the target is logged user because we are reading notifications now, not creating
        User target  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Notification> notifications = notificationRepository.findTargetNotReadedNotifications(target);
        List<NotificationDto> notificationDtos = transformFromEntityToDtoList(notifications);
        return notificationDtos;
    }

    public List<NotificationDto> findLoggedUserAllNotifications(){
        //now the target is logged user because we are reading notifications now, not creating
        User target  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable  = PageRequest.of(0,20);
        List<Notification> notifications = notificationRepository.findByTargetOrderByCreatedAtDesc(target,pageable);
        List<NotificationDto> notificationDtos  =transformFromEntityToDtoList(notifications);

        return notificationDtos;
    }

    public void readAll() {
        User target = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Notification> notifications = notificationRepository.findByTarget(target);


        for(Notification notification : notifications){
                notification.setRead(true);
                notificationRepository.save(notification);
        }
    }


    public List<NotificationDto> transformFromEntityToDtoList(List<Notification> notifications){
        List<NotificationDto> notificationDtos  = new ArrayList<>();
        for(Notification notification  :notifications){
            NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);

            ProfileDto targetDto= userService.toDto(notification.getTarget());

            ProfileDto triggerDto = null;
            if(notification.getTrigger()!=null)
                triggerDto  =userService.toDto(notification.getTrigger());

            notificationDto.setTrigger(triggerDto);
            notificationDto.setTarget(targetDto);
            notificationDto.setCreatedAtString(DateUtil.dateToString(notification.getCreatedAt(),"dd/MM/YYYY HH:mm"));
            notificationDtos.add(notificationDto);
        }

        return notificationDtos;
    }


//    public void readMessages() {
//        User target = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<Notification> notifications = notificationRepository.findByTarget(target);
//
//
//        for(Notification notification : notifications){
//            if(notification.getNotificationType()==NotificationType.MESSAGE){
//                notification.setRead(true);
//                notificationRepository.save(notification);
//            }
//        }
//    }

    public void newGreetingMessage(User target) {
        User trigger = userService.findEntityById(Long.valueOf(59));

        createNotification(target,trigger,NotificationType.MESSAGE,null);

    }


}












