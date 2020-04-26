package com.alinso.popcon.repository;

import com.alinso.popcon.entity.DuelVote;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuelVoteRepository extends JpaRepository<DuelVote,Long> {
    List<DuelVote> findByVoter(User loggedUser);
}
