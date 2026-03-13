package org.example.controller;

import org.example.service.UrlShortenerService;

public class UrlShortenerController {
    private UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    public String shortenUrl(String longUrl) {
        return urlShortenerService.createShortUrl(longUrl);
    }

    public String fetchLongUrl(String shortCode) {
        return urlShortenerService.getLongUrl(shortCode);
    }
}
