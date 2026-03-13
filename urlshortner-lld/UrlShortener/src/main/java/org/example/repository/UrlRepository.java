package org.example.repository;

import org.example.model.UrlMapping;

public interface UrlRepository {
    public void save(UrlMapping urlMapping);
    public UrlMapping fetchMapping(String shortenedCode);
}
