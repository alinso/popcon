package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.contest.CustomContestDto;
import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.service.CustomContestService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import com.alinso.popcon.validator.CustomContestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customContest")
public class CustomContestController {

    @Autowired
    CustomContestService customContestService;

    @Autowired
    CustomContestValidator customContestValidator;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteCustomContest(@PathVariable("id") Long id) {

        customContestService.deleteCustomContest(id);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/stopStart/{id}")
    public ResponseEntity<?> stopStartCustomContest(@PathVariable("id") Long id) {

        customContestService.startStopCustomContest(id);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }
    @GetMapping("/results/{pageNum}")
    public ResponseEntity<?> customContestResults(@PathVariable("pageNum")Integer pagenum) {

        List<CustomContestDto> customContestDtoList  = customContestService.getCustomContestResults(pagenum);
        return new ResponseEntity<>(customContestDtoList, HttpStatus.CREATED);
    }

    @GetMapping("/get/")
    public ResponseEntity<?> getCustomContest() {

        List<PhotoDto> dtoList  = customContestService.getCustomContest();
        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }




    @GetMapping("/vote/{selectedId}/{otherId}")
    public ResponseEntity<?> voteCustomContest(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        customContestService.voteCustomContest(selectedId,otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

    @PostMapping("/save")
    public ResponseEntity<?> createCustomContest(CustomContestFormDto customContestFormDto, BindingResult result) {
        customContestValidator.validate(customContestFormDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        customContestService.createCustomContest(customContestFormDto);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }
}
