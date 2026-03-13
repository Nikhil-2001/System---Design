package org.example.repository;

import org.example.model.UrlMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlRepositoryImpl implements UrlRepository{
    Map<String,UrlMapping> shortUrlLongUrl;

    public UrlRepositoryImpl() {
        shortUrlLongUrl = new ConcurrentHashMap<>();
    }

    @Override
    public void save(UrlMapping urlMapping) {
        shortUrlLongUrl.put(urlMapping.shortCode(), urlMapping);
    }

    @Override
    public UrlMapping fetchMapping(String shortCode) {
        return shortUrlLongUrl.get(shortCode);
    }
}
