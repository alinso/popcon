package com.alinso.popcon.repository;

import com.alinso.popcon.entity.CustomContest;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomContestRepository extends JpaRepository<CustomContest, Long> {

    @Query("select c from CustomContest  c where c.isActive=true ")
    List<CustomContest> findActiveContests();

    @Query("select c from CustomContest c where (c.photo1=:photo1 and c.photo2=:photo2) or (c.photo1=:photo2 and c.photo2=:photo1)")
    CustomContest findByPhotos(@Param("photo1") Photo photo1, @Param("photo2") Photo photo2);

    @Query("select c from CustomContest c where c.creator=:creator")
    List<CustomContest> findByCreator(@Param("creator")User creator);

    @Query("select c from CustomContest c where c.photo2=:photo or c.photo1=:photo")
    List<CustomContest> findAllContestsOfPhoto(@Param("photo")Photo photo);
}
