package com.alinso.popcon.repository;

import com.alinso.popcon.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query("select u from User u where u.username=:username")
    User findByUsername(@Param("username") String username);

    @Query("select u from User u where u.phone=:phone")
    User findByPhone(@Param("phone") String phone);

    @Query("select user from User user where  user.username like CONCAT('%',:search,'%') ")
    List<User> searchUser(@Param("search") String search, Pageable pageable);

    @Query("select u from User u where u.password=:password")
    User findByPassword(@Param("password") String pasword);
}
