package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Comment;
import com.alinso.popcon.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment  c where c.photo=:photo")
    List<Comment> getCommentsByPhoto(@Param("photo") Photo photo);

    @Query("select count(c) from Comment c where c.photo=:photo")
    Integer getCommentCountOfPhoto(@Param("photo") Photo photo);


}
