package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Duel;
import com.alinso.popcon.entity.DuelVote;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuelVoteRepository extends JpaRepository<DuelVote,Long> {
    List<DuelVote> findByVoter(User loggedUser);

    @Query("select count(d) from DuelVote  d where d.selectedPhoto=:photo and d.duel=:duel")
    Integer findByDuelAndPhoto(@Param("duel") Duel duel, @Param("photo") User photo);
}
