package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Duel;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.enums.DuelStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DuelRepository extends JpaRepository<Duel,Long> {
    @Query("select c from Duel c where (c.writerPhoto=:photo1 and c.readerPhoto=:photo2) or (c.writerPhoto=:photo2 and c.readerPhoto=:photo1)")
    Duel findByPhotos(@Param("photo1") Photo photo1, @Param("photo2") Photo photo2);

    @Query("select d from Duel  d where d.acceptDate>:yesterday")
    List<Duel> findActiveDuels(@Param("yesterday")Date yesterday);

    @Query("select d from Duel d where d.reader=:loggedUser or d.writer=:loggedUser order by id DESC")
    List<Duel> findByReaderOrWriter(@Param("loggedUser") User loggedUser, Pageable pageable);

    @Query("select d from Duel d where d.acceptDate<:yesterday and d.status=:accepted")
    List<Duel> findExpiredDuels(@Param("yesterday")Date yesterday, @Param("accepted")DuelStatus accepted);

    @Query("select d from Duel  d where d.readerPhoto=:photo or d.writerPhoto=:photo")
    List<Duel> findAllDuelsOfPhoto(@Param("photo") Photo photo);

    @Query("select d from Duel d where d.status=:accepted order by d.acceptDate asc")
    List<Duel> getAllByPage( @Param("accepted")DuelStatus accepted, Pageable pageable);
}
