package com.alinso.popcon.repository;

import com.alinso.popcon.entity.CustomContest;
import com.alinso.popcon.entity.CustomContestVote;
import com.alinso.popcon.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomContestVoteRepository extends JpaRepository<CustomContestVote,Long> {

    @Query("select count(v) from CustomContestVote  v where v.selectedPhoto=:photo1 and v.customContest=:customContest")
    Integer findByCustomContestAndPhoto1(@Param("customContest") CustomContest customContest, @Param("photo1") Photo photo1);

    @Query("select count(v) from CustomContestVote  v where v.otherPhoto=:photo2 and v.customContest=:customContest")
    Integer findByCustomContestAndPhoto2(@Param("customContest") CustomContest customContest, @Param("photo2") Photo photo2);

    List<CustomContestVote> findByCustomContest(CustomContest customContest);

}
