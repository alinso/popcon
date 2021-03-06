package com.alinso.popcon.controller;


import com.alinso.popcon.entity.dto.notification.NotificationDto;
import com.alinso.popcon.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("newNotifications")
    public ResponseEntity<?> newNotifications(){
        List<NotificationDto> notificationDtoList = notificationService.findLoggedUserNotReadedNotifications();
        return  new ResponseEntity<>(notificationDtoList, HttpStatus.OK);
    }

    @GetMapping("allNotifications")
    public ResponseEntity<?> allNotifications(){
        List<NotificationDto> notificationDtoList = notificationService.findLoggedUserAllNotifications();
        return  new ResponseEntity<>(notificationDtoList, HttpStatus.OK);
    }

    @GetMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        notificationService.deleteById(id);
        return  new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("read/{id}")
    public void read(@PathVariable("id") Long id){
        notificationService.read(id);
    }

    @GetMapping("readAll")
    public void readExceptMessages(){
        notificationService.readAll();
    }

}
