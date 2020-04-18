package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.ToBeVoted;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ToBeVotedRepository  extends JpaRepository<ToBeVoted, Long> {

    @Query("select v from ToBeVoted v where  v.voter=:voter and (v.photo1 =:photo1 and v.photo2=:photo2) or (v.photo1=:photo2 and v.photo2=:photo1)")
    ToBeVoted findExactToBeVote(@Param("photo1") Photo photo1, @Param("voter") User voter, @Param("photo2") Photo photo2);

}
