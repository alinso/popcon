package com.alinso.popcon.service;

import com.alinso.popcon.entity.*;
import com.alinso.popcon.entity.dto.contest.DuelDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.enums.DuelStatus;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.*;
import com.alinso.popcon.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DuelService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DuelRepository duelRepository;

    @Autowired
    DuelVoteRepository duelVoteRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    PhotoService photoService;

    @Autowired
    DuelWatchRepository duelWatchRepository;

    public void save(Long photoId, Long readerId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User reader = userRepository.getOne(readerId);

        Photo p = photoRepository.getById(photoId);
        UserUtil.checkUserOwner(p.getUser().getId());

        Duel duel = new Duel();

        duel.setWriter(loggedUser);
        duel.setReader(reader);
        duel.setWriterPhoto(p);
        duel.setWriterPhotoVoteCount(0);
        duel.setReaderPhotoVoteCount(0);
        duel.setStatus(DuelStatus.WAITING);
        duelRepository.save(duel);

        notificationService.newDuelRequest(reader, duel);

    }

    public DuelDto findById(Long id) {
        Duel duel = duelRepository.findById(id).get();
        return toDto(duel);
    }


    @Scheduled(cron = "0 0/10 * * * *")
    public void updateStatus() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -24);
        Date yesterday = calendar.getTime();

        List<Duel> expiredDuels = duelRepository.findExpiredDuels(yesterday, DuelStatus.ACCEPTED);
        for (Duel d : expiredDuels) {
            d.setStatus(DuelStatus.FINISHED);
        }
        duelRepository.saveAll(expiredDuels);


        //send Notification to watchers
        for (Duel d : expiredDuels) {
            List<DuelWatch> duelWatches  =duelWatchRepository.findByDuel(d);
            for(DuelWatch duelWatch : duelWatches){
                notificationService.duelFinished(d.getId(), duelWatch.getWatcher());
            }
            notificationService.duelFinished(d.getId(), d.getReader());
            notificationService.duelFinished(d.getId(), d.getWriter());
        }

    }


    public DuelDto toDto(Duel duel) {

        DuelDto photoDto = modelMapper.map(duel, DuelDto.class);
        photoDto.setReader(userService.toDto(duel.getReader()));
        photoDto.setWriter(userService.toDto(duel.getWriter()));
        photoDto.setWatching(isWatching(duel));
        photoDto.setWatcherCount(duelWatchRepository.getWatcherCount(duel));
        if(duel.getAcceptDate()!=null)
        photoDto.setAcceptedTime(duel.getAcceptDate().getTime());

        if (duel.getReaderPhoto() != null)
            photoDto.setReaderPhoto(photoService.toDto(duel.getReaderPhoto()));


        photoDto.setWriterPhoto(photoService.toDto(duel.getWriterPhoto()));
        return photoDto;
    }



    private Boolean isWatching(Duel duel) {
        User user  =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DuelWatch duelWatch  =duelWatchRepository.findByDuelAndUser(duel,user);
        if(duelWatch==null)
            return false;
        return true;

    }

    public void decline(Long id) {
        Duel duel = duelRepository.findById(id).get();
        UserUtil.checkUserOwner(duel.getReader().getId());

        duel.setStatus(DuelStatus.DECLINED);
        duelRepository.save(duel);

        notificationService.newDuelDecline(duel.getWriter(),duel);
    }

    public void accept(Long photoWarId, Long readerPhotoId) {
        Photo photo = photoRepository.getById(readerPhotoId);
        Duel duel = duelRepository.getOne(photoWarId);

        if (duel == null || photo == null) {
            throw new UserWarningException("Hata oluştu, durumu bize bildirebilirsin");
        }

        UserUtil.checkUserOwner(photo.getUser().getId());
        UserUtil.checkUserOwner(duel.getReader().getId());

        Duel oldDuel = duelRepository.findByPhotos(photo, duel.getWriterPhoto());
        if (oldDuel != null)
            throw new UserWarningException("Aynı fotoğraflar ile bir düello zaten yapıldı.");


        duel.setReaderPhoto(photo);
        duel.setAcceptDate(new Date());
        duel.setStatus(DuelStatus.ACCEPTED);

        duelRepository.save(duel);

        notificationService.newDuelAccept(duel.getWriter(),duel);
    }

    public void vote(Long selectedId, Long otherId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Photo selectedPhoto = photoRepository.findById(selectedId).get();
        Photo otherPhoto = photoRepository.findById(otherId).get();


        Duel duel = duelRepository.findByPhotos(selectedPhoto, otherPhoto);
        if (selectedId == duel.getWriterPhoto().getId())
            duel.setWriterPhotoVoteCount(duel.getWriterPhotoVoteCount() + 1);
        if (selectedId == duel.getReaderPhoto().getId())
            duel.setReaderPhotoVoteCount(duel.getReaderPhotoVoteCount() + 1);
        duelRepository.save(duel);

        DuelVote duelVote = new DuelVote();
        duelVote.setDuel(duel);
        duelVote.setSelectedPhoto(selectedPhoto);
        duelVote.setOtherPhoto(otherPhoto);
        duelVote.setVoter(loggedUser);

        duelVoteRepository.save(duelVote);

    }

    public List<PhotoDto> getDuelForVoting() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date yesterday = calendar.getTime();

        List<Duel> activeDuelList = duelRepository.findActiveDuels(yesterday);
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        List<DuelVote> myVotes = duelVoteRepository.findByVoter(loggedUser);
        if (activeDuelList.size() == 0)
            throw new UserWarningException("Yeni düello yok");

        Iterator<Duel> i = activeDuelList.iterator();
        while (i.hasNext()) {
            Duel d = i.next();
            for (DuelVote v : myVotes) {
                if (v.getDuel().getId() == d.getId()) {
                    i.remove();
                }
            }
        }

        if (activeDuelList.size() == 0)
            throw new UserWarningException("Yeni düello yok");

        Random random = new Random();
        Integer index = random.nextInt(activeDuelList.size());

        return setPercentOfDuelPhotoDtos(activeDuelList.get(index));

    }

    List<PhotoDto> setPercentOfDuelPhotoDtos(Duel duel){

        PhotoDto dto1=photoService.toDto(duel.getWriterPhoto());
        PhotoDto dto2=photoService.toDto(duel.getReaderPhoto());


        Integer writerVoteCount  = duel.getWriterPhotoVoteCount();
        Integer readerVoteCount  = duel.getReaderPhotoVoteCount();

        dto1.setPercent(0);
        dto2.setPercent(0);

        if((readerVoteCount+writerVoteCount)>0){
            dto1.setPercent((writerVoteCount*1000) / (writerVoteCount+readerVoteCount)  );
            dto2.setPercent((readerVoteCount*1000) / (writerVoteCount+readerVoteCount)  );
        }

        List<PhotoDto> photoDtos = new ArrayList<>();
        photoDtos.add(dto1);
        photoDtos.add(dto2);
        return photoDtos;
    }


    public List<DuelDto> getResults(Integer pagenum) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Pageable pageable = PageRequest.of(pagenum, 5);
        List<Duel> duelList = duelRepository.findByReaderOrWriter(loggedUser, pageable);
        List<DuelDto> duelDtos = new ArrayList<>();
        for (Duel d : duelList) {
            duelDtos.add(toDto(d));
        }

        return duelDtos;
    }
    public List<DuelDto> getAllResults(Integer pagenum) {

        Pageable pageable = PageRequest.of(pagenum, 5);
        List<Duel> duelList = duelRepository.getAllByPage( DuelStatus.ACCEPTED,pageable);
        List<DuelDto> duelDtos = new ArrayList<>();
        for (Duel d : duelList) {
            duelDtos.add(toDto(d));
        }

        return duelDtos;
    }


    public void delete(Long  id) {

        User user  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Duel d=duelRepository.findById(id).get();

        if(d.getWriter().getId()==user.getId() || d.getReader().getId()==user.getId()) {
            List<DuelVote> duelVoteList = duelVoteRepository.findByDuel(d);
            duelVoteRepository.deleteAll(duelVoteList);
            duelRepository.delete(d);
        }
    }
    public void toggleWatch(Long id){
        Duel duel = duelRepository.getOne(id);
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        DuelWatch duelWatch =  duelWatchRepository.findByDuelAndUser(duel,user);
        if(duelWatch==null){
            duelWatch  = new DuelWatch();
            duelWatch.setDuel(duel);
            duelWatch.setWatcher(user);
            duelWatchRepository.save(duelWatch);
        }else{
            duelWatchRepository.delete(duelWatch);
        }
    }

}
























