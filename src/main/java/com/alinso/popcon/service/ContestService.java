package com.alinso.popcon.service;

import com.alinso.popcon.entity.*;
import com.alinso.popcon.entity.dto.contest.CustomContestDto;
import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.enums.DuelType;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.*;
import com.alinso.popcon.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContestService {

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

    private List<Photo>getDuelPhotos(Long catId){

            User loggedUser  =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Boolean genderImportant=false;
        if(catId==1 || catId==2 || catId==3){
            genderImportant=true;
        }
        PhotoCategory photoCategory = photoCategoryRepository.findById(catId).get();


        List<Photo> allPhotos =new ArrayList<>();


        //select duel type
        Random randomDuelType = new Random();
        Integer duelTypeInt = randomDuelType.nextInt(10);
        Pageable pageable = PageRequest.of(0, 1000);




            if (duelTypeInt <= 3) {
                allPhotos = photoRepository.findByCategoriesOrderByIdDesc(photoCategory, pageable);
            } else if (duelTypeInt > 3 && duelTypeInt <= 5) {
                allPhotos = photoRepository.findByCategoriesOrderByPercentDesc(photoCategory, pageable);
            } else if (duelTypeInt > 5 && duelTypeInt <= 7 ) {
                List<User> followings = followRepository.findUsersFollowedByTheUser(loggedUser);
                for (User following : followings) {
                    allPhotos.addAll(photoRepository.getByUser(following));
                }
            } else {
                allPhotos = photoRepository.findByCategories(photoCategory);
            }

        if(genderImportant ) {
            Iterator<Photo> i = allPhotos.iterator();
            while(i.hasNext()){
                Photo p=i.next();
                if(p.getGender()!=loggedUser.getPreferredGender()){
                    i.remove();
                }
            }
        }

        return allPhotos;

    }


    public List<PhotoDto> getDuelByCategory(Long catId) {
        List<Photo> allPhotos= new ArrayList<>();
        while(true){
             allPhotos =getDuelPhotos(catId);
             if(allPhotos.size()>2)
                 break;
        }

        User  loggedUser= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Random random = new Random();
        Integer index1 = random.nextInt(allPhotos.size());
        Integer index2 = random.nextInt(allPhotos.size());

        List<Photo> duel = new ArrayList<>();
        duel.add(allPhotos.get(index1));
        duel.add(allPhotos.get(index2));

        ToBeVoted toBeVoted  = toBeVotedRepository.findExactToBeVote(allPhotos.get(index1),loggedUser,allPhotos.get(index2));
        if(toBeVoted==null){
             toBeVoted =  new ToBeVoted();
            toBeVoted.setPhoto1(allPhotos.get(index1));
            toBeVoted.setPhoto2(allPhotos.get(index2));
            toBeVoted.setVoter(loggedUser);

            toBeVotedRepository.save(toBeVoted);
        }


        return photoService.toDtoList(duel);
    }

    public List<PhotoDto> popconBestDaily(){


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date yesterday= calendar.getTime();
        Pageable pageable = PageRequest.of(0,20);

        List<Photo> bestPhotosOfDay = photoRepository.getBestPhotosOfDay(yesterday, pageable);

        return photoService.toDtoList(bestPhotosOfDay);
    }


    private Boolean canIVote(Photo photo1,Photo photo2){
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ToBeVoted toBeVotedCombination1  = toBeVotedRepository.findExactToBeVote(photo1,loggedUser,photo2);
        ToBeVoted toBeVotedCombination2  = toBeVotedRepository.findExactToBeVote(photo2,loggedUser,photo1);
        if(toBeVotedCombination1==null && toBeVotedCombination2==null){
            return false; //if there is no ask for these photos the user cannot vote
        }
        if(toBeVotedCombination1!=null)
            toBeVotedRepository.delete(toBeVotedCombination1);
        if(toBeVotedCombination2!=null)
            toBeVotedRepository.delete(toBeVotedCombination2);

        return true;
    }

    // as a security issue you can check if user voted any of these photos recently(last 10 votes maybe?)
    public void vote(Long selectedId, Long otherId) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo selected = photoRepository.getOne(selectedId);
        Photo other = photoRepository.getOne(otherId);


        if(!canIVote(other,selected))
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
    }


    public void setPercent(Photo p) {
        Integer won = voteRepository.wonCount(p);
        Integer lost = voteRepository.lostCount(p);
        Integer likeCount  = likeRepository.getLikesOfPhoto(p);

        Integer totalVoteCount= won + lost+likeCount;
        if(totalVoteCount<10)
            return;

        won=won+likeCount;

        Double wonLong = Double.valueOf(won);
        Double lostLong = Double.valueOf(lost);
        Double percent = wonLong / (wonLong + lostLong) * 1000;

        Integer percentInt = percent.intValue();

        p.setPercent(percentInt);
        photoRepository.save(p);
    }


    public void createCustomContest(CustomContestFormDto customContestFormDto) {

        Photo photo1 = photoRepository.findById(customContestFormDto.getPhoto1Id()).get();
        Photo photo2 = photoRepository.findById(customContestFormDto.getPhoto2Id()).get();

        UserUtil.checkUserOwner(photo1.getUser().getId());
        UserUtil.checkUserOwner(photo2.getUser().getId());

        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        CustomContest customContest = customContestRepository.findByPhotos(photo1, photo2);
        if (customContest != null)
            throw new UserWarningException("Aynı fotoğraflar ile bir düello zaten yaptın, tekrar başlatmak istersen eski düelloyu silmelisin");


        customContest = new CustomContest();
        customContest.setCreator(creator);
        customContest.setPhoto1(photo1);
        customContest.setPhoto2(photo2);
        customContest.setTitle(customContestFormDto.getTitle());
        customContest.setActive(true);
        customContest.setMaxVote(customContestFormDto.getMaxVote());
        customContest.setVoteCount(0);
        customContestRepository.save(customContest);
    }

    public List<PhotoDto> getCustomContest() {
        List<CustomContest> customContestList = customContestRepository.findActiveContests();
        if (customContestList.size() == 0)
            throw new UserWarningException("Şu an aktif düello yok");

        Random random = new Random();
        Integer index = random.nextInt(customContestList.size());


        List<Photo> duel = new ArrayList<>();
        duel.add(customContestList.get(index).getPhoto1());
        duel.add(customContestList.get(index).getPhoto2());

        return photoService.toDtoList(duel);
    }

    public List<CustomContestDto> getCustomContestResults(Integer pagenum) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(pagenum,5);
        List<CustomContest> customContestList = customContestRepository.findByCreator(loggedUser,pageable);
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

            Integer photo1VoteCount = customContestVoteRepository.findByCustomContestAndPhoto1(c, c.getPhoto1());
            Integer photo2VoteCount = customContestVoteRepository.findByCustomContestAndPhoto2(c, c.getPhoto2());

            customContestDto.setPhoto1VoteCount(photo1VoteCount);
            customContestDto.setPhoto2VoteCount(photo2VoteCount);

            customContestDtoList.add(customContestDto);
        }

        return customContestDtoList;
    }


    public void voteCustomContest(Long selectedId, Long otherId) {
        User loggedUser  = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Photo selectedPhoto = photoRepository.findById(selectedId).get();
        Photo otherPhoto = photoRepository.findById(otherId).get();

        UserUtil.checkUserOwner(selectedPhoto.getUser().getId());
        UserUtil.checkUserOwner(otherPhoto.getUser().getId());


        CustomContest customContest = customContestRepository.findByPhotos(selectedPhoto, otherPhoto);
        customContest.setVoteCount(customContest.getVoteCount() + 1);
        if (customContest.getVoteCount() >= customContest.getVoteCount())
            customContest.setActive(false);
        customContestRepository.save(customContest);

        CustomContestVote customContestVote = new CustomContestVote();
        customContestVote.setCustomContest(customContest);
        customContestVote.setSelectedPhoto(selectedPhoto);
        customContestVote.setOtherPhoto(otherPhoto);
        customContestVote.setVoter(loggedUser);

        customContestVoteRepository.save(customContestVote);

    }

    public void deleteCustomContest(Long id) {
        CustomContest customContest  =customContestRepository.getOne(id);
        UserUtil.checkUserOwner(customContest.getCreator().getId());

        List<CustomContestVote> customContestVotes  =customContestVoteRepository.findByCustomContest(customContest);
        customContestVoteRepository.deleteAll(customContestVotes);
        customContestRepository.delete(customContest);

    }

    public void startStopCustomContest(Long id) {
        CustomContest customContest  =customContestRepository.getOne(id);
        UserUtil.checkUserOwner(customContest.getCreator().getId());

        customContest.setActive(!customContest.getActive());
        customContestRepository.save(customContest);
    }

    public List<CustomContest> findAllContestsOfPhoto(Photo photo) {

        List<CustomContest>  customContestList = customContestRepository.findAllContestsOfPhoto(photo);
        return customContestList;
    }

    public List<PhotoDto> popconBest(Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum,20);
        List<Photo> bestPhotosOfDay = photoRepository.getBest(pageable);
        return photoService.toDtoList(bestPhotosOfDay);
    }
}
