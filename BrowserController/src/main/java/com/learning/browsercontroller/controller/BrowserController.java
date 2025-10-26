package com.learning.browsercontroller.controller;

import com.learning.browsercontroller.service.BrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
public class BrowserController {

    @Autowired
    private BrowserService service;

    @GetMapping("/start")
    public String start(@RequestParam String browser, @RequestParam String url) throws IOException {
        return service.start(browser, url);
    }

    @GetMapping("/stop")
    public String stop(@RequestParam String browser) throws IOException {
        return service.stop(browser);
    }

    @GetMapping("/geturl")
    public String getUrl(@RequestParam String browser) throws IOException {
        return service.getUrl(browser);
    }
}
