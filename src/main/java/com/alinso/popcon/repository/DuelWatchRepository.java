package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Duel;
import com.alinso.popcon.entity.DuelWatch;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DuelWatchRepository extends JpaRepository<DuelWatch, Long> {

    @Query("select d from DuelWatch d where d.duel=:duel and d.watcher=:user")
    DuelWatch findByDuelAndUser(@Param("duel") Duel duel, @Param("user") User user);

    List<DuelWatch> findByDuel(Duel d);

    @Query("select count(d) from DuelWatch d where d.duel=:duel")
    Integer getWatcherCount(@Param("duel") Duel duel);
}
