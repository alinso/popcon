package com.alinso.popcon.service;

import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.Vote;
import com.alinso.popcon.entity.enums.Gender;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.repository.UserRepository;
import com.alinso.popcon.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PhotoRepository  photoRepository;

    @Autowired
    VoteRepository voteRepository;

    public Map<String,Integer> statistics(){

        Map<String,Integer> statistics=  new HashMap<>();
        Integer womenCount  = userRepository.userCountByGender(Gender.FEMALE);
        Integer menCount = userRepository.userCountByGender(Gender.MALE);


        //photocount by gender
        Integer womenPhotoCount  = 0;
        Integer menPhotoCount = 0;
        List<Photo> photos = photoRepository.findAll();
        for(Photo p : photos){
            if(p.getUser().getGender()==Gender.MALE)
                menPhotoCount++;
            if(p.getUser().getGender()==Gender.FEMALE)
                womenPhotoCount++;
        }

        //vote by gender
        Integer womenVoteCount  = 0;
        Integer menVoteCount  = 0;
        List<Vote> voteList  = voteRepository.findAll();
        for(Vote v :voteList){
            if(v.getVoter().getGender()==Gender.MALE)
                menVoteCount++;
            if(v.getVoter().getGender()==Gender.FEMALE)
                womenVoteCount++;
        }

        statistics.put("womenCount",womenCount);
        statistics.put("menCount",menCount);
        statistics.put("womenPhotoCount",womenPhotoCount);
        statistics.put("menPhotoCount",menPhotoCount);
        statistics.put("womenVoteCount",womenVoteCount);
        statistics.put("menVoteCount",menVoteCount);

        return statistics;
    }


}


















