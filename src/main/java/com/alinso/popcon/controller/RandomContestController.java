package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.service.RandomContestService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import com.alinso.popcon.validator.CustomContestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("randomContest")
public class RandomContestController {

    @Autowired
    RandomContestService randomContestService;

    @Autowired
    CustomContestValidator customContestValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;



    @GetMapping("/bestOfDay/{pageNum}")
    public ResponseEntity<?> bestOfDay(@PathVariable("pageNum") Integer pageNum) {

        List<PhotoDto> photoDtos  = randomContestService.popconBestDaily(pageNum);
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }

    @GetMapping("/best/{pageNum}")
    public ResponseEntity<?> best(@PathVariable("pageNum") Integer pageNum) {

        List<PhotoDto> photoDtos  = randomContestService.popconBest(pageNum);
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }


    @GetMapping("/random/")
    public ResponseEntity<?> random() {

        List<PhotoDto> dtoList  = randomContestService.getRandomContest();
        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }

    @GetMapping("/vote/{selectedId}/{otherId}")
    public ResponseEntity<?> vote(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        randomContestService.vote(selectedId,otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

}
