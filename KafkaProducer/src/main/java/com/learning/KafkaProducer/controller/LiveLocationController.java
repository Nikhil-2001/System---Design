package com.learning.KafkaProducer.controller;

import com.learning.KafkaProducer.service.LiveLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/location")
public class LiveLocationController {
    @Autowired
    LiveLocationService liveLocationService;

    @GetMapping
    public ResponseEntity updateLocation() throws InterruptedException {
        int range = 100;
        while(range > 0) {
            liveLocationService.updateLocation(Math.random() + " - " + Math.random() + "Time - "+ System.currentTimeMillis());
            Thread.sleep(1000);
            range--;
        }
        return new ResponseEntity<>(Map.of("Message", "Location Updated"), HttpStatus.OK);
    }
}
