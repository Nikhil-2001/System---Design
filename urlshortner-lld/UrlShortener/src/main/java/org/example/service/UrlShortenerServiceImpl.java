package org.example.service;

import org.example.model.UrlMapping;
import org.example.repository.UrlRepository;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.random.RandomGenerator;

public class UrlShortenerServiceImpl implements UrlShortenerService{
    private UrlRepository urlRepository;
    private AtomicLong uniqueIdGenerator;
    private RandomGenerator randomGenerator;
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public UrlShortenerServiceImpl(UrlRepository urlRepository, RandomGenerator randomGenerator) {
        this.urlRepository = urlRepository;
        this.uniqueIdGenerator = new AtomicLong();
        this.randomGenerator = randomGenerator;
    }

    @Override
    public String createShortUrl(String longUrl) {
        Long id = uniqueIdGenerator.getAndIncrement();
        String shortCode = getBase62Encode();
        UrlMapping mapping = new UrlMapping(id, shortCode, longUrl);
        urlRepository.save(mapping);
        return shortCode;
    }

    @Override
    public String getLongUrl(String shortCode) {
        UrlMapping mapping = urlRepository.fetchMapping(shortCode);
        if(mapping != null) {
            return mapping.longUrl();
        }
        return "Not Found";
    }

    private String getBase62Encode() {
        StringBuilder sb = new StringBuilder();

        while (sb.isEmpty() || urlRepository.fetchMapping(sb.toString()) != null) {
            sb.setLength(0);
            for(int i = 0; i < 6; i++) {
                sb.insert(i, BASE62.charAt(randomGenerator.nextInt(62)));
            }
        }

        return sb.toString();
    }
}
