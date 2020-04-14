package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("select v from Vote v where v.otherPhoto=:otherPhoto and v.voter=:voter and v.selectedPhoto =:selectedPhoto")
    Vote findExactVote(@Param("otherPhoto") Photo otherPhoto, @Param("voter") User voter, @Param("selectedPhoto") Photo selectedPhoto);


    @Query("select count(v) from Vote v where v.selectedPhoto=:winner")
    Integer wonCount(@Param("winner") Photo winner);

    @Query("select count(v) from Vote v where v.otherPhoto=:loser")
    Integer lostCount(@Param("loser") Photo loser);

}
