package com.alinso.popcon.repository;

import com.alinso.popcon.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {

    @Query("select city from City city order by city.name")
    List<City> findAllOrderByName();
}
