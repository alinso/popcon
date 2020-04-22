package com.alinso.popcon.controller;

import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import com.alinso.popcon.service.PhotoService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import com.alinso.popcon.validator.PhotoUpdateValidator;
import com.alinso.popcon.validator.PhotoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("photo")
public class PhotoController {

    @Autowired
    PhotoService photoService;

    @Autowired
    PhotoValidator photoValidator;

    @Autowired
    PhotoUpdateValidator photoUpdateValidator;


    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @GetMapping("/album/{id}/{pageNum}")
    public ResponseEntity<?> album(@PathVariable("id") Long id, @PathVariable("pageNum") Integer pageNum) {
        List<PhotoDto> photoDtoList = photoService.getByUserId(id,pageNum);
        return new ResponseEntity<>(photoDtoList, HttpStatus.CREATED);
    }

    @GetMapping("like/{id}")
    public ResponseEntity<?> like(@PathVariable("id") Long id) {
        photoService.like(id);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(PhotoFormDto photoFormDto, BindingResult result) {

        photoValidator.validate(photoFormDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        Long photoId  =photoService.uploadPhoto(photoFormDto);
        return new ResponseEntity<Long>(photoId, HttpStatus.ACCEPTED);
    }
    @PostMapping("delete")
    public ResponseEntity<?> delete(@RequestBody Map<String,String> file) { //send via fileName param

        photoService.delete(file.get("fileName"));

        return new ResponseEntity<String>("deleted", HttpStatus.ACCEPTED);
    }

    @GetMapping("/getCategories")
    public ResponseEntity<?> getPhotoCategories() {
        List<PhotoCategory> photoCategoryList = photoService.getPhotoCategories();
        return new ResponseEntity<>(photoCategoryList, HttpStatus.CREATED);
    }


    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getPhotoById(@PathVariable("id") Long id) {
        PhotoDto p = photoService.getById(id);
        return new ResponseEntity<>(p, HttpStatus.CREATED);
    }

    @PostMapping("update")
    public ResponseEntity<?> update(PhotoFormDto photoUpdateDto, BindingResult result) {

        photoUpdateValidator.validate(photoUpdateDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        photoService.update(photoUpdateDto);
        return new ResponseEntity<String>("uploaded", HttpStatus.ACCEPTED);
    }





}
