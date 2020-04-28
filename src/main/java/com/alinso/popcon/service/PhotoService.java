package com.alinso.popcon.service;

import com.alinso.popcon.entity.*;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.*;
import com.alinso.popcon.util.FileStorageUtil;
import com.alinso.popcon.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserService userService;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    RandomContestService randomContestService;

    @Autowired
    CustomContestService customContestService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    PhotoCategoryRepository photoCategoryRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BlockService blockService;

    public List<PhotoDto> getByUserId(Long id, Integer pageNum) {
        User u = userService.findEntityById(id);

        if (blockService.isThereABlock(u.getId()))
            throw new UserWarningException("Erişim Yok");


        Pageable pageable  = PageRequest.of(pageNum,12);

        List<Photo> photos = photoRepository.getByUserOrderByPercent(u,pageable);
        return toDtoList(photos);
    }

    public List<PhotoCategory> getPhotoCategories() {
        List<PhotoCategory> photoCategoryList = photoCategoryRepository.findAll();
        return photoCategoryList;
    }


    public Long uploadPhoto(PhotoFormDto photoFormDto) {

        String extension = FilenameUtils.getExtension(photoFormDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;
        fileStorageUtil.storeFile(photoFormDto.getFile(), newName, true);

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo p = new Photo();
        p.setUser(loggedUser);
        p.setGender(photoFormDto.getGender());
        p.setFileName(newName);
        p.setCaption(photoFormDto.getCaption());
        p.setPercent(0);

        p.setCategories(setCategories(photoFormDto.getCategoryIds()));

        photoRepository.save(p);
        return p.getId();
    }


    public List<PhotoCategory> setCategories(List<Long> ids) {
        List<PhotoCategory> photoCategoryList = new ArrayList<>();
        for (Long catId : ids) {
            PhotoCategory photoCategory = photoCategoryRepository.findById(catId).get();
            photoCategoryList.add(photoCategory);
        }
        return photoCategoryList;
    }

    public List<PhotoDto> toDtoList(List<Photo> photoList) {
        List<PhotoDto> dtoList = new ArrayList<>();
        for (Photo p : photoList) {
            dtoList.add(toDto(p));
        }
        return dtoList;
    }

    public PhotoDto toDto(Photo p) {
        User loggedUser  =(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PhotoDto dto = modelMapper.map(p, PhotoDto.class);
        dto.setUser(userService.toDto(p.getUser()));
        dto.setLikeCount(likeRepository.getLikesOfPhoto(p));
        dto.setCommentCount(commentRepository.getCommentCountOfPhoto(p));

        PhotoLike photoLike  =likeRepository.findByLikerAndPhoto(loggedUser,p);

        if(photoLike!=null)
            dto.setDidILikeIt(true);
        else
            dto.setDidILikeIt(false);

        return dto;
    }


    public void delete(String photoName) {
        Photo photo = photoRepository.findByFileName(photoName);
        UserUtil.checkUserOwner(photo.getUser().getId());

        if (photo != null) {
            fileStorageUtil.deleteFile(photoName);

            List<Vote> allVotesOfPhoto = voteRepository.findAllVotesOfPhoto(photo);
            voteRepository.deleteAll(allVotesOfPhoto);

            List<CustomContest> customContestList = customContestService.findAllContestsOfPhoto(photo);
            for (CustomContest c : customContestList) {
                customContestService.deleteCustomContest(c.getId());
            }
            photoRepository.delete(photo);
        }
    }

    public PhotoDto getById(Long id) {
        Photo p = photoRepository.getById(id);
        return toDto(p);
    }

    public void update(PhotoFormDto photoUpdateDto) {

        Photo p = photoRepository.getById(photoUpdateDto.getId());
        UserUtil.checkUserOwner(p.getUser().getId());
        p.setCategories(setCategories(photoUpdateDto.getCategoryIds()));
        p.setCaption(photoUpdateDto.getCaption());
        p.setGender(photoUpdateDto.getGender());

        photoRepository.save(p);
    }

    public void like(Long id) {

        User liker = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!liker.getPhoneVerified())
            return;


        Photo photo = photoRepository.getById(id);
        PhotoLike like = likeRepository.findByLikerAndPhoto(liker, photo);

        if (like != null) {
            likeRepository.delete(like);
            randomContestService.setPercent(photo);
        } else {
            like = new PhotoLike();
            like.setLiker(liker);
            like.setPhoto(photo);
            likeRepository.save(like);
            randomContestService.setPercent(photo);
          //  notificationService.newPhotoLike(photo.getUser(),photo.getId());

        }
    }
}









