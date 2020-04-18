package com.alinso.popcon.repository;


import com.alinso.popcon.entity.PhotoLike;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<PhotoLike, Long> {

    @Query("select l from PhotoLike  l where l.liker=:liker and l.photo=:photo")
    PhotoLike findByLikerAndPhoto(@Param("liker") User liker, @Param("photo") Photo photo);

    @Query("select count(l)  from PhotoLike l where l.photo=:photo")
    Integer getLikesOfPhoto(@Param("photo") Photo photo);

}
