package com.alinso.popcon.service;

import com.alinso.popcon.entity.Follow;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.repository.FollowRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowService {

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    public Boolean follow(Long leaderId){

        User follower  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader =userService.findEntityById(leaderId);
        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader,follower);

        Boolean isFollowing;
        if(follow==null){
            Follow newFollow = new Follow();
            newFollow.setFollower(follower);
            newFollow.setLeader(leader);
            followRepository.save(newFollow);
            isFollowing=true;
        }else{
            followRepository.delete(follow);
            isFollowing=false;
        }
        return isFollowing;
    }

    public Boolean isFollowing(Long leaderId) {

        User follower  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader =userService.findEntityById(leaderId);

        Follow follow = followRepository.findFollowingByLeaderAndFollower(leader,follower);
        if(follow==null)
            return false;
        else
            return true;
    }

    public List<ProfileDto> findMyFollowings() {
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<User> followingUsers = followRepository.findUsersFollowedByTheUser(loggedUser);

        List<ProfileDto> profileDtos  = new ArrayList<>();
        for(User user: followingUsers){
            profileDtos.add(userService.toDto(user));
        }

        return profileDtos;
    }

}