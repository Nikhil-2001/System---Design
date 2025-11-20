package org.example.model;

public class Ladder extends BoardEntity{
    public Ladder(int start, int end) {
        super(start, end);
        if (start >= end) {
            throw new IllegalArgumentException("Ladder tail must be at a lower position than its head.");
        }
    }
}