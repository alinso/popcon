package com.alinso.popcon.service;

import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.Vote;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.repository.PhotoCategoryRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ContestService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    PhotoCategoryRepository photoCategoryRepository;

    @Autowired
    VoteRepository voteRepository;

    public List<PhotoDto> getDuelByCategory(Long catId){

        PhotoCategory photoCategory = photoCategoryRepository.findById(catId).get();
        List<Photo> allPhotos = photoRepository.findByCategories(photoCategory);


        Random random  = new Random();
        Integer index1 =  random.nextInt(allPhotos.size());
        Integer index2  =random.nextInt(allPhotos.size());

        List<Photo> duel = new ArrayList<>();
        duel.add(allPhotos.get(index1));
        duel.add(allPhotos.get(index2));

        return photoService.toDtoList(duel);
    }



    // as a security issue you can check if user voted any of these photos recently(last 10 votes maybe?)
    public void vote(Long selectedId, Long otherId) {

        User loggedUser  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo selected  = photoRepository.getOne(selectedId);
        Photo other  = photoRepository.getOne(otherId);
        Vote vInDb = voteRepository.findExactVote(other,loggedUser,selected);


        if(vInDb!=null){
            return ;
        }

        Vote vote = new Vote();


        vote.setVoter(loggedUser);
        vote.setSelectedPhoto(selected);
        vote.setOtherPhoto(other);

        voteRepository.save(vote);
        setPercent(other);
        setPercent(selected);
    }


    public void setPercent(Photo p){
        Integer won  =voteRepository.wonCount(p);
        Integer lost =voteRepository.lostCount(p);

        Double wonLong = Double.valueOf(won);
        Double lostLong = Double.valueOf(lost);
        Double percent = wonLong/(wonLong+lostLong) *100;

        Integer percentInt = percent.intValue();

        p.setPercent(percentInt);
        photoRepository.save(p);
    }
}
