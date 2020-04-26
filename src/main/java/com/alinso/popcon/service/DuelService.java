package com.alinso.popcon.service;

import com.alinso.popcon.entity.Duel;
import com.alinso.popcon.entity.DuelVote;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.contest.DuelDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.enums.DuelStatus;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.DuelRepository;
import com.alinso.popcon.repository.DuelVoteRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.repository.UserRepository;
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

        notificationService.newPhotoWar(reader, duel);

    }

    public DuelDto findById(Long id) {
        Duel duel = duelRepository.findById(id).get();
        return toDto(duel);
    }


    @Scheduled(cron = "0 0 * * * *")
    public void updateStatus() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date yesterday = calendar.getTime();

        List<Duel> expiredDuels = duelRepository.findExpiredDuels(yesterday, DuelStatus.ACCEPTED);
        for (Duel d : expiredDuels) {
            d.setStatus(DuelStatus.FINISHED);
        }
        duelRepository.saveAll(expiredDuels);

    }


    public DuelDto toDto(Duel duel) {

        DuelDto photoDto = modelMapper.map(duel, DuelDto.class);
        photoDto.setReader(userService.toDto(duel.getReader()));
        photoDto.setWriter(userService.toDto(duel.getWriter()));
        if (duel.getReaderPhoto() != null)
            photoDto.setReaderPhoto(photoService.toDto(duel.getReaderPhoto()));


        photoDto.setWriterPhoto(photoService.toDto(duel.getWriterPhoto()));
        return photoDto;
    }

    public void decline(Long id) {
        Duel duel = duelRepository.findById(id).get();
        UserUtil.checkUserOwner(duel.getReader().getId());

        duel.setStatus(DuelStatus.DECLINED);
        duelRepository.save(duel);
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


        List<Photo> duel = new ArrayList<>();
        duel.add(activeDuelList.get(index).getReaderPhoto());
        duel.add(activeDuelList.get(index).getWriterPhoto());

        return photoService.toDtoList(duel);
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


}
