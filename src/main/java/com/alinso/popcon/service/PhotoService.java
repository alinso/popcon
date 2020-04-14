package com.alinso.popcon.service;

import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.photo.PhotoUpdateDto;
import com.alinso.popcon.entity.dto.photo.SinglePhotoUploadDto;
import com.alinso.popcon.repository.PhotoCategoryRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.util.FileStorageUtil;
import com.alinso.popcon.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    FileStorageUtil fileStorageUtil;

    @Autowired
    PhotoCategoryRepository photoCategoryRepository;

    public List<PhotoDto> getByUserId(Long id){
        User u  =userService.findEntityById(id);
        List<Photo> photos=  photoRepository.getByUser(u);
        return toDtoList(photos);
    }

    public List<PhotoCategory> getPhotoCategories(){
        List<PhotoCategory> photoCategoryList =  photoCategoryRepository.findAll();
        return photoCategoryList;
    }


    public String uploadPhoto(SinglePhotoUploadDto singlePhotoUploadDto) {

        String extension = FilenameUtils.getExtension(singlePhotoUploadDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;
        fileStorageUtil.storeFile(singlePhotoUploadDto.getFile(), newName, true);

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo p = new Photo();
        p.setUser(loggedUser);
        p.setFileName(newName);
        p.setCaption(singlePhotoUploadDto.getCaption());

        p.setCategories(setCategories(singlePhotoUploadDto.getCategoryIds()));

        photoRepository.save(p);
        return newName;
    }


    public List<PhotoCategory> setCategories(List<Long> ids){
        List<PhotoCategory> photoCategoryList =  new ArrayList<>();
        for(Long catId:ids){
            PhotoCategory photoCategory = photoCategoryRepository.findById(catId).get();
            photoCategoryList.add(photoCategory);
        }
        return photoCategoryList;
    }

    public List<PhotoDto> toDtoList(List<Photo> photoList){
        List<PhotoDto> dtoList = new ArrayList<>();
        for(Photo p:photoList){
            dtoList.add(toDto(p));
        }
        return dtoList;
    }

    public PhotoDto toDto(Photo p){
            PhotoDto dto = modelMapper.map(p, PhotoDto.class);
            dto.setUser(userService.toDto(p.getUser()));
            return dto;
    }


    public void delete(String photoName){
        Photo photo = photoRepository.findByFileName(photoName);
        UserUtil.checkUserOwner(photo.getUser().getId());

        if(photo!=null){
            fileStorageUtil.deleteFile(photoName);
            photoRepository.delete(photo);
        }
    }

    public PhotoDto getById(Long id) {
        Photo p = photoRepository.getById(id);
        return toDto(p);
    }

    public void update(PhotoUpdateDto photoUpdateDto) {

        Photo p  =photoRepository.getById(photoUpdateDto.getId());
        UserUtil.checkUserOwner(p.getUser().getId());
        p.setCategories(setCategories(photoUpdateDto.getCategoryIds()));
        p.setCaption(photoUpdateDto.getCaption());

        photoRepository.save(p);
    }
}









