package com.alinso.popcon.service;

import com.alinso.popcon.entity.Comment;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.comment.CommentDto;
import com.alinso.popcon.entity.dto.comment.CommentFormDto;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.CommentRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.util.UserUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {


    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PhotoService photoService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserService userService;

    @Autowired
    BlockService blockService;

    public void save(CommentFormDto commentFormDto) {

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Photo photo = photoRepository.getById(commentFormDto.getPhotoId());

        if (blockService.isThereABlock(photo.getUser().getId()))
            throw new UserWarningException("Erişim Yok");


        Comment comment = new Comment();
        comment.setComment(commentFormDto.getComment());
        comment.setWriter(loggedUser);
        comment.setPhoto(photo);

        commentRepository.save(comment);
    }


    public List<CommentDto> getCommentsByPhotoId(Long id,Integer pageNum){

        PageRequest pageable  = PageRequest.of(pageNum,20);

        Photo photo = photoRepository.getById(id);
        List<Comment> commentList = commentRepository.getCommentsByPhoto(photo,pageable);
        return toDtoList(commentList);
    }

    public List<CommentDto> toDtoList(List<Comment> commentList) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : commentList) {
            commentDtos.add(toDto(c));
        }
        return commentDtos;
    }


    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setPhotoDto(photoService.toDto(comment.getPhoto()));
        commentDto.setProfileDto(userService.toDto(comment.getWriter()));
        commentDto.setId(comment.getId());
        commentDto.setComment(comment.getComment());

        return commentDto;
    }

    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).get();


        Photo photoWithOwner  = photoRepository.getWithOwner(comment.getPhoto().getId());
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(comment.getWriter().getId()!= loggedUser.getId() && photoWithOwner.getUser().getId()!=loggedUser.getId()){
            throw new UserWarningException("Yetkin yok");
        }

        commentRepository.delete(comment);
    }
}




