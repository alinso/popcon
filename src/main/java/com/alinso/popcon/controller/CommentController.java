package com.alinso.popcon.controller;

import com.alinso.popcon.entity.dto.comment.CommentDto;
import com.alinso.popcon.entity.dto.comment.CommentFormDto;
import com.alinso.popcon.service.CommentService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("comment")
public class CommentController {


    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    CommentService commentService;


    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid CommentFormDto commentFormDto, BindingResult result){

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        CommentDto commentDto  = commentService.save(commentFormDto);

        return new ResponseEntity<>(commentDto, HttpStatus.ACCEPTED);
    }





    @GetMapping("/getCommentsByPhotoId/{id}/{pageNum}")
    public ResponseEntity<?> save(@PathVariable("id") Long id, @PathVariable("pageNum") Integer pageNumm){

        List<CommentDto> commentDtoList =commentService.getCommentsByPhotoId(id,pageNumm);

        return new ResponseEntity<>(commentDtoList, HttpStatus.ACCEPTED);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){

        commentService.delete(id);

        return new ResponseEntity<>("OK ", HttpStatus.ACCEPTED);
    }



}
