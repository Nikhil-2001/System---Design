package com.learning.KafkaConsumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    @KafkaListener(topics = "location-live", groupId = "user-group")
    public void liveLocation(String location) {
        System.out.println(location + "Curr Time -" + System.currentTimeMillis());
    }
}
