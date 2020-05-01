package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.contest.DuelDto;
import com.alinso.popcon.entity.dto.photo.PhotoDto;
import com.alinso.popcon.service.DuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("duel")
public class DuelController {

    @Autowired
    DuelService duelService;


    @GetMapping("save/{readerId}/{photoId}")
    public ResponseEntity<?> save(@PathVariable("readerId") Long readerId, @PathVariable("photoId") Long photoId) {
        duelService.save(photoId, readerId);
        return new ResponseEntity<>("OK", HttpStatus.ACCEPTED);
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        DuelDto duelDto = duelService.findById(id);
        return new ResponseEntity<>(duelDto, HttpStatus.ACCEPTED);
    }

    @GetMapping("decline/{id}")
    public ResponseEntity<?> decline(@PathVariable("id") Long id) {
        duelService.decline(id);
        return new ResponseEntity<>("declined", HttpStatus.ACCEPTED);
    }

    @GetMapping("accept/{photoWarId}/{readerPhotoId}")
    public ResponseEntity<?> accept(@PathVariable("photoWarId") Long photoWarId, @PathVariable("readerPhotoId") Long readerPhotoId) {
        duelService.accept(photoWarId, readerPhotoId);
        return new ResponseEntity<>("declined", HttpStatus.ACCEPTED);
    }

    @GetMapping("/vote/{selectedId}/{otherId}")
    public ResponseEntity<?> voteDuel(@PathVariable("selectedId") Long selectedId, @PathVariable("otherId") Long otherId) {

        duelService.vote(selectedId, otherId);
        return new ResponseEntity<>("voted", HttpStatus.CREATED);
    }

    @GetMapping("/getDuelForVoting")
    public ResponseEntity<?> getDuelForVoting() {

        List<PhotoDto> photoDtos = duelService.getDuelForVoting();
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }

    @GetMapping("/results/{pageNum}")
    public ResponseEntity<?> results(@PathVariable("pageNum") Integer pageNum) {

        List<DuelDto> photoDtos = duelService.getResults(pageNum);
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }
    @GetMapping("/allResults/{pageNum}")
    public ResponseEntity<?> getAll(@PathVariable("pageNum") Integer pageNum) {

        List<DuelDto> photoDtos = duelService.getAllResults(pageNum);
        return new ResponseEntity<>(photoDtos, HttpStatus.CREATED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> results(@PathVariable("id") Long id) {
        duelService.delete(id);
        return new ResponseEntity<>("deleted", HttpStatus.CREATED);
    }

    @GetMapping("/toggleWatch/{id}")
    public ResponseEntity<?> toggleWatch(@PathVariable("id") Long id) {
        duelService.toggleWatch(id);
        return new ResponseEntity<>("deleted", HttpStatus.CREATED);
    }

}
