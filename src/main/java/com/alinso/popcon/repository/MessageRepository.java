package com.alinso.popcon.repository;

import com.alinso.popcon.entity.Message;
import com.alinso.popcon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("select message from Message message where" +
            " (message.reader =:user1 and writer=:user2)" +
            " or (message.reader =:user2 and writer=:user1)" +
            " order by message.id asc")
    List<Message> getByReaderWriter(@Param("user1") User user1, @Param("user2") User user2);




    @Query(value = "select o from Message o left join Message b " +
            "on (o.reader = b.reader and o.writer = b.writer and o.createdAt<b.createdAt) " +
            "where b.createdAt is null and (o.reader=:me or o.writer=:me) order by o.createdAt desc")
    List<Message> latestMessageFromEachConversation(@Param("me") User me, Pageable pageable);

    @Query("select count(m) from Message m where m.reader =:writer and m.writer=:reader")
    Integer readerResposeCount(@Param("writer") User writer, @Param("reader") User reader);

    @Query("select distinct m.reader from Message m where m.writer=:writer and m.createdAt>:yesterday")
    List<User> todayConversationCountByWrittenByUser(@Param("writer")User writer, @Param("yesterday")Date yesterday);


    //how may diffret people has written to our WRITER(so take writer as reader here)
    @Query("select distinct m.writer from Message m where m.reader=:reader and m.createdAt>:yesterday")
    List<User> todayConversationCountReadByUser(@Param("reader") User reader, @Param("yesterday") Date yesterday);
}
