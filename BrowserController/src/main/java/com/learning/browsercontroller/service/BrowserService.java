package com.learning.browsercontroller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class BrowserService {

    @Autowired
    private BrowserManager browserManager;

    public String start(String browser, String url) throws IOException {
        return browserManager.startBrowser(browser, url);
    }

    public String stop(String browser) throws IOException {
        return browserManager.stopBrowser(browser);
    }

    public String getUrl(String browser) throws IOException {
        return browserManager.getActiveUrl(browser);
    }
}
