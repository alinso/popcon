package com.alinso.popcon.repository;


import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.PhotoCategory;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {

    @Query("select p from Photo p where p.user=:user")
    List<Photo> getByUser(@Param("user") User user);

    @Query("select p from Photo p where p.fileName=:fileName")
    Photo findByFileName(@Param("fileName") String fileName);

    List<Photo> findByCategories(PhotoCategory photoCategory);

    Photo getById(Long id);
}
