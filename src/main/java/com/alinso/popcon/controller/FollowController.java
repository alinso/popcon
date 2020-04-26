package com.alinso.popcon.controller;


import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    FollowService followService;

    @GetMapping("follow/{leaderId}")
    public ResponseEntity<?> follow(@PathVariable("leaderId") Long leaderId) {
        Boolean result = followService.follow(leaderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("isFollowing/{leaderId}")
    public ResponseEntity<?> isFollowing(@PathVariable("leaderId") Long leaderId) {
        Boolean result = followService.isFollowing(leaderId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("myFollowings")
    public ResponseEntity<?> myFollowings() {

        List<ProfileDto> profileDtoList = followService.findMyFollowings();
        return new ResponseEntity<>(profileDtoList, HttpStatus.OK);
    }

    @GetMapping("myFollowers")
    public ResponseEntity<?> myFollowers() {

        List<ProfileDto> profileDtoList = followService.myFollowers();
        return new ResponseEntity<>(profileDtoList, HttpStatus.OK);
    }



    @GetMapping("followerCount/{id}")
    public ResponseEntity<?> followerCount(@PathVariable("id") Long id) {

        Integer fc = followService.followerCount(id);
        return new ResponseEntity<>(fc, HttpStatus.OK);
    }


}
