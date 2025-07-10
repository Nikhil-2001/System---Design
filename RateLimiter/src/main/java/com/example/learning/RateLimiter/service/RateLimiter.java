package com.example.learning.RateLimiter.service;

public interface RateLimiter {
    boolean isAllowed(String key);
}
