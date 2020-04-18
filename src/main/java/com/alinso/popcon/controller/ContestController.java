package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.contest.CustomContestDto;
import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.service.ContestService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import com.alinso.popcon.validator.CustomContestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("contest")
public class ContestController {

    @Autowired
    ContestService contestService;

    @Autowired
    CustomContestValidator customContestValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;


    @GetMapping("/deleteCustomContest/{id}")
    public ResponseEntity<?> deleteCustomContest(@PathVariable("id") Long id) {

        contestService.deleteCustomContest(id);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/stopStartCustomContest/{id}")
    public ResponseEntity<?> stopStartCustomContest(@PathVariable("id") Long id) {

        contestService.startStopCustomContest(id);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/bestOfDay")
    public ResponseEntity<?> bestOfDay() {

        List<PhotoDto> photoDtos  =contestService.popconBestDaily();
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }

    @GetMapping("/best")
    public ResponseEntity<?> best() {

        List<PhotoDto> photoDtos  =contestService.popconBest();
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }

    @GetMapping("/getCustomContestResults")
    public ResponseEntity<?> customContestResults() {

       List<CustomContestDto> customContestDtoList  =contestService.getCustomContestResults();
        return new ResponseEntity<>(customContestDtoList, HttpStatus.CREATED);
    }


    @GetMapping("/voteCustomContest/{selectedId}/{otherId}")
    public ResponseEntity<?> voteCustomContest(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        contestService.voteCustomContest(selectedId,otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

    @PostMapping("/createCustomContest")
    public ResponseEntity<?> createCustomContest(CustomContestFormDto customContestFormDto, BindingResult result) {
        customContestValidator.validate(customContestFormDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        contestService.createCustomContest(customContestFormDto);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/duel/{catId}")
    public ResponseEntity<?> myProfileInfoForUpdate(@PathVariable("catId") Long catId) {

        List<PhotoDto> dtoList  = contestService.getDuelByCategory(catId);
        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }

    @GetMapping("/getCustomContest/")
    public ResponseEntity<?> getCustomContest() {

        List<PhotoDto> dtoList  = contestService.getCustomContest();
        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }


    @GetMapping("/vote/{selectedId}/{otherId}")
    public ResponseEntity<?> vote(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        contestService.vote(selectedId,otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

}
