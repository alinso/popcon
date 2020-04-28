package com.alinso.popcon.service;

import com.alinso.popcon.entity.*;
import com.alinso.popcon.entity.dto.contest.CustomContestDto;
import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.enums.Gender;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.*;
import com.alinso.popcon.util.Constants;
import com.alinso.popcon.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomContestService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    UserService userService;

    @Autowired
    PhotoCategoryRepository photoCategoryRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    ToBeVotedRepository toBeVotedRepository;

    @Autowired
    CustomContestVoteRepository customContestVoteRepository;

    @Autowired
    CustomContestRepository customContestRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserRepository userRepository;


    private List<Photo> getAllRandomContest() {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Random randomCategory = new Random();
        Integer catId = randomCategory.nextInt(8)+1;


        Boolean genderImportant = false;
        if (loggedUser.getPreferredGender() != Gender.UNSELECTED) {
            genderImportant = true;
        }
        PhotoCategory photoCategory = photoCategoryRepository.findById(Long.valueOf(catId)).get();


        List<Photo> allPhotos = new ArrayList<>();


        //select duel type
        Random randomDuelType = new Random();
        Integer randomTypeInt = randomDuelType.nextInt(10);
        Pageable pageable = PageRequest.of(0, 1000);


        if (randomTypeInt <= 3) {
            allPhotos = photoRepository.findByCategoriesOrderByIdDesc(photoCategory, pageable);
        } else if (randomTypeInt > 3 && randomTypeInt <= 5) {
            allPhotos = photoRepository.findByCategoriesOrderByPercentDesc(photoCategory, pageable);
        } else if (randomTypeInt > 5 && randomTypeInt <= 7) {
            List<User> followings = followRepository.findAllUsersFollowedByTheUser(loggedUser);
            for (User following : followings) {
                allPhotos.addAll(photoRepository.getByUser(following, pageable));
            }
        } else {
            allPhotos = photoRepository.findByCategories(photoCategory);
        }

        if (genderImportant) {
            Iterator<Photo> i = allPhotos.iterator();
            while (i.hasNext()) {
                Photo p = i.next();
                if (p.getGender() != loggedUser.getPreferredGender()) {
                    i.remove();
                }
            }
        }

        return allPhotos;

    }


    public List<PhotoDto> getRandomContest() {
        List<Photo> allPhotos = new ArrayList<>();
        while (true) {
            allPhotos = getAllRandomContest();
            if (allPhotos.size() >= 2)
                break;
        }

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Random random = new Random();
        Integer index1 = random.nextInt(allPhotos.size());
        Integer index2 = random.nextInt(allPhotos.size());

        List<Photo> duel = new ArrayList<>();
        duel.add(allPhotos.get(index1));
        duel.add(allPhotos.get(index2));

        ToBeVoted toBeVoted = toBeVotedRepository.findExactToBeVote(allPhotos.get(index1), loggedUser, allPhotos.get(index2));
        if (toBeVoted == null) {
            toBeVoted = new ToBeVoted();
            toBeVoted.setPhoto1(allPhotos.get(index1));
            toBeVoted.setPhoto2(allPhotos.get(index2));
            toBeVoted.setVoter(loggedUser);

            toBeVotedRepository.save(toBeVoted);
        }


        return photoService.toDtoList(duel);
    }

    public List<PhotoDto> popconBestDaily(Integer pageNum) {


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date yesterday = calendar.getTime();

        Pageable pageable = PageRequest.of(pageNum, 20);


        List<Photo> bestPhotosOfDay = photoRepository.getBestPhotosOfDay(yesterday, pageable);

        return photoService.toDtoList(bestPhotosOfDay);
    }


    private Boolean canIVote(Photo photo1, Photo photo2) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ToBeVoted toBeVotedCombination1 = toBeVotedRepository.findExactToBeVote(photo1, loggedUser, photo2);
        ToBeVoted toBeVotedCombination2 = toBeVotedRepository.findExactToBeVote(photo2, loggedUser, photo1);
        if (toBeVotedCombination1 == null && toBeVotedCombination2 == null) {
            return false; //if there is no ask for these photos the user cannot vote
        }
        if (toBeVotedCombination1 != null)
            toBeVotedRepository.delete(toBeVotedCombination1);
        if (toBeVotedCombination2 != null)
            toBeVotedRepository.delete(toBeVotedCombination2);

        return true;
    }

    // as a security issue you can check if user voted any of these photos recently(last 10 votes maybe?)
    public void vote(Long selectedId, Long otherId) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo selected = photoRepository.getOne(selectedId);
        Photo other = photoRepository.getOne(otherId);


        if (!loggedUser.getPhoneVerified())
            return;

        if (!canIVote(other, selected))
            return;

        //if user voted as same dont calculate it as second vote
        Vote vInDb = voteRepository.findExactVote(other, loggedUser, selected);
        if (vInDb != null) {
            return;
        }

        Vote vote = new Vote();


        vote.setVoter(loggedUser);
        vote.setSelectedPhoto(selected);
        vote.setOtherPhoto(other);

        voteRepository.save(vote);
        setPercent(other);
        setPercent(selected);


        if(selected.getPercent()>other.getPercent() && selected.getPercent()!=0 && other.getPercent()!=0){
            loggedUser.setCorrectGuessCount(loggedUser.getCorrectGuessCount()+1);
        }
        if(selected.getPercent()<other.getPercent() && selected.getPercent()!=0 && other.getPercent()!=0){
            loggedUser.setWrongGuessCount(loggedUser.getWrongGuessCount()+1);
        }
        userRepository.save(loggedUser);
    }


    public void setPercent(Photo p) {
        Integer won = voteRepository.wonCount(p);
        Integer lost = voteRepository.lostCount(p);
        Integer likeCount = likeRepository.getLikesOfPhoto(p);

        Integer totalVoteCount = won + lost + likeCount;
        if (totalVoteCount < Constants.minVoteCountToShowPercent)
            return;

        if (totalVoteCount == Constants.minVoteCountToShowPercent)
            notificationService.showPercent(p.getUser(), p.getId());


        won = won + likeCount;

        Double wonLong = Double.valueOf(won);
        Double lostLong = Double.valueOf(lost);
        Double percent = wonLong / (wonLong + lostLong) * 1000;

        Integer percentInt = percent.intValue();

        p.setPercent(percentInt);
        photoRepository.save(p);
    }

    public List<PhotoDto> popconBest(Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 20);
        List<Photo> bestPhotosOfDay = photoRepository.getBest(pageable);
        return photoService.toDtoList(bestPhotosOfDay);
    }

}
