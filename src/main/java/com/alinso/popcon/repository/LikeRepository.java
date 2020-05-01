package com.alinso.popcon.repository;


import com.alinso.popcon.entity.PhotoLike;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<PhotoLike, Long> {

    @Query("select l from PhotoLike  l where l.liker=:liker and l.photo=:photo")
    PhotoLike findByLikerAndPhoto(@Param("liker") User liker, @Param("photo") Photo photo);

    @Query("select count(l)  from PhotoLike l where l.photo=:photo")
    Integer getLikeCountOfPhoto(@Param("photo") Photo photo);

    @Query("select l from PhotoLike l where l.photo=:photo")
    List<PhotoLike> getLikesByPhoto(@Param("photo") Photo photo);


}
