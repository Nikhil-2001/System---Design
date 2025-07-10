package com.example.learning.RateLimiter;


import com.example.learning.RateLimiter.service.RateLimiter;
import com.example.learning.RateLimiter.service.SlidingWindowLogRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {
    @Autowired
    @Qualifier("tokenBucket")
    private RateLimiter rateLimiter;

    @GetMapping("/hello")
    public ResponseEntity<String> hello(HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        if (!rateLimiter.isAllowed(ip)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Retry-After", "60");
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .headers(httpHeaders)
                    .body("Rate limit exceeded. Try again later.");
        }

        return ResponseEntity.ok("Hello, world!");
    }
}