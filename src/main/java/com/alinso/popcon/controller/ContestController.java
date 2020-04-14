package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("contest")
public class ContestController {

    @Autowired
    ContestService contestService;


    @GetMapping("/duel/{catId}")
    public ResponseEntity<?> myProfileInfoForUpdate(@PathVariable("catId") Long catId) {

        List<PhotoDto> dtoList  = contestService.getDuelByCategory(catId);
        return new ResponseEntity<>(dtoList, HttpStatus.CREATED);
    }
    @GetMapping("/vote/{selectedId}/{otherId}")
    public ResponseEntity<?> vote(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        contestService.vote(selectedId,otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

}
