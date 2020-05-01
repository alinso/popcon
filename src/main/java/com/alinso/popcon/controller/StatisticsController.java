package com.alinso.popcon.controller;

import com.alinso.popcon.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("statistics")
public class StatisticsController {


    @Autowired
    StatisticsService statisticsService;

    @GetMapping("get")
    public ResponseEntity<?> get(){
       Map<String,Integer> stat = statisticsService.statistics();
        return new ResponseEntity<>(stat, HttpStatus.ACCEPTED);
    }
}
