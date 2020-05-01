package com.alinso.popcon.service;

import com.alinso.popcon.entity.*;
import com.alinso.popcon.entity.dto.contest.CustomContestDto;
import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.CustomContestRepository;
import com.alinso.popcon.repository.CustomContestVoteRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.util.UserUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class CustomContestService {

    @Autowired
    CustomContestRepository customContestRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    CustomContestVoteRepository customContestVoteRepository;

    @Autowired
    UserService userService;

    @Autowired
    PhotoService photoService;

    public List<CustomContest> findAllContestsOfPhoto(Photo photo) {

        List<CustomContest> customContestList = customContestRepository.findAllContestsOfPhoto(photo);
        return customContestList;
    }


    public void createCustomContest(CustomContestFormDto customContestFormDto) {

        Photo photo1 = photoRepository.findById(customContestFormDto.getPhoto1Id()).get();
        Photo photo2 = photoRepository.findById(customContestFormDto.getPhoto2Id()).get();

        UserUtil.checkUserOwner(photo1.getUser().getId());
        UserUtil.checkUserOwner(photo2.getUser().getId());

        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        CustomContest customContest = customContestRepository.findByPhotos(photo1, photo2);
        if (customContest != null)
            throw new UserWarningException("Aynı fotoğraflar ile bir oylama zaten yaptın, tekrar yapmak istersen eski oylamayı silmelisin");


        customContest = new CustomContest();
        customContest.setCreator(creator);
        customContest.setPhoto1(photo1);
        customContest.setPhoto2(photo2);
        customContest.setPhoto2VoteCount(0);
        customContest.setPhoto1VoteCount(0);
        customContest.setTitle(customContestFormDto.getTitle());
        customContest.setActive(true);
        customContest.setMaxVote(customContestFormDto.getMaxVote());
        customContest.setVoteCount(0);
        customContestRepository.save(customContest);
    }

    public List<PhotoDto> getCustomContest() {
        List<CustomContest> customContestList = customContestRepository.findActiveContests();
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        List<CustomContestVote> myVotes = customContestVoteRepository.findByVoter(loggedUser);
        if (customContestList.size() == 0)
            throw new UserWarningException("Yeni oylama yok");

        Iterator<CustomContest> i = customContestList.iterator();

        while(i.hasNext()) {
            CustomContest c= i.next();
            for (CustomContestVote v : myVotes)
                if (v.getCustomContest().getId() == c.getId()) {
                    i.remove();
                }
        }

        if (customContestList.size() == 0)
            throw new UserWarningException("Yeni oylama yok");

        Random random = new Random();
        Integer index = random.nextInt(customContestList.size());


        return setPercentOfCustomContestPhotoDtos(customContestList.get(index));

    }


    public void voteCustomContest(Long selectedId, Long otherId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Photo selectedPhoto = photoRepository.findById(selectedId).get();
        Photo otherPhoto = photoRepository.findById(otherId).get();


        CustomContest customContest = customContestRepository.findByPhotos(selectedPhoto, otherPhoto);
        customContest.setVoteCount(customContest.getVoteCount() + 1);


        if (selectedId == customContest.getPhoto1().getId())
            customContest.setPhoto1VoteCount(customContest.getPhoto1VoteCount() + 1);
        if (selectedId == customContest.getPhoto2().getId())
            customContest.setPhoto2VoteCount(customContest.getPhoto2VoteCount() + 1);
        customContestRepository.save(customContest);


        CustomContestVote customContestVote = new CustomContestVote();
        customContestVote.setCustomContest(customContest);
        customContestVote.setSelectedPhoto(selectedPhoto);
        customContestVote.setOtherPhoto(otherPhoto);
        customContestVote.setVoter(loggedUser);

        customContestVoteRepository.save(customContestVote);

    }

    List<PhotoDto> setPercentOfCustomContestPhotoDtos(CustomContest customContest){

        if(customContest==null)
            return null;

        PhotoDto dto1=photoService.toDto(customContest.getPhoto1());
        PhotoDto dto2=photoService.toDto(customContest.getPhoto2());

        Integer photo1VoteCount  = customContest.getPhoto1VoteCount();
        Integer photo2VoteCount  = customContest.getPhoto2VoteCount();

        dto1.setPercent(0);
        dto2.setPercent(0);

        if((photo2VoteCount+photo1VoteCount)>0){
            dto1.setPercent((photo1VoteCount*1000) / (photo1VoteCount+photo2VoteCount)  );
            dto2.setPercent((photo2VoteCount*1000) / (photo1VoteCount+photo2VoteCount)  );
        }

        List<PhotoDto> photoDtos = new ArrayList<>();
        photoDtos.add(dto1);
        photoDtos.add(dto2);
        return photoDtos;
    }

    public void deleteCustomContest(Long id) {
        CustomContest customContest = customContestRepository.getOne(id);
        UserUtil.checkUserOwner(customContest.getCreator().getId());

        List<CustomContestVote> customContestVotes = customContestVoteRepository.findByCustomContest(customContest);
        customContestVoteRepository.deleteAll(customContestVotes);
        customContestRepository.delete(customContest);

    }

    public void startStopCustomContest(Long id) {
        CustomContest customContest = customContestRepository.getOne(id);
        UserUtil.checkUserOwner(customContest.getCreator().getId());

        customContest.setActive(!customContest.getActive());
        customContestRepository.save(customContest);
    }

    public List<CustomContestDto> getCustomContestResults(Integer pagenum) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(pagenum, 5);
        List<CustomContest> customContestList = customContestRepository.findByCreator(loggedUser, pageable);
        List<CustomContestDto> customContestDtoList = new ArrayList<>();
        for (CustomContest c : customContestList) {
            CustomContestDto customContestDto = new CustomContestDto();
            customContestDto.setCreator(userService.toDto(c.getCreator()));
            customContestDto.setActive(c.getActive());
            customContestDto.setMaxVote(c.getMaxVote());
            customContestDto.setId(c.getId());
            customContestDto.setPhoto1(photoService.toDto(c.getPhoto1()));
            customContestDto.setPhoto2(photoService.toDto(c.getPhoto2()));
            customContestDto.setTitle(c.getTitle());


            customContestDtoList.add(customContestDto);
        }

        return customContestDtoList;
    }

}
