package org.example.service;

public interface UrlShortenerService {
    public String createShortUrl(String longUrl);
    public String getLongUrl(String shortCode);
}
