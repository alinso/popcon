package com.alinso.popcon.repository;

import com.alinso.popcon.entity.CustomContest;
import com.alinso.popcon.entity.CustomContestVote;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomContestVoteRepository extends JpaRepository<CustomContestVote,Long> {

    @Query("select count(v) from CustomContestVote  v where v.selectedPhoto=:photo and v.customContest=:customContest")
    Integer findByCustomContestAndPhoto(@Param("customContest") CustomContest customContest, @Param("photo") Photo photo);


    List<CustomContestVote> findByCustomContest(CustomContest customContest);

    List<CustomContestVote> findByVoter(User loggedUser);
}
