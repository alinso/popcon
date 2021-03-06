package com.alinso.popcon.repository;


import com.alinso.popcon.entity.DeletedConversation;
import com.alinso.popcon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedConversationRepository extends JpaRepository<DeletedConversation,Long> {

    @Query("select d from DeletedConversation d where d.eraserUser=:eraser and d.otherUser=:other")
    DeletedConversation findByUserIds(@Param("eraser") User eraser, @Param("other") User other);
}
