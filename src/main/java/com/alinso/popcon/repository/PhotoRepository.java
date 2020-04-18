package com.alinso.popcon.repository;


import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.enums.Gender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    @Query("select p from Photo p where p.user=:user")
    List<Photo> getByUser(@Param("user") User user);

    @Query("select p from Photo p where p.fileName=:fileName")
    Photo findByFileName(@Param("fileName") String fileName);

    List<Photo> findByCategories(PhotoCategory photoCategory);

    Photo getById(Long id);

    List<Photo> findByCategoriesOrderByPercentDesc(PhotoCategory category, Pageable pageable);

    List<Photo> findByCategoriesOrderByIdDesc(PhotoCategory category, Pageable pageable);

    @Query("select p  from Photo p where p.createdAt>:yesterday order by p.percent desc ")
    List<Photo> getBestPhotosOfDay(@Param("yesterday") Date yesterday,Pageable pageable);

    @Query("select p  from Photo p  order by p.percent desc ")
    List<Photo> getBest(Pageable pageable);

}
