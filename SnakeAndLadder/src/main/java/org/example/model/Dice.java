package org.example.model;

import lombok.Data;

import java.util.Random;

@Data
public class Dice {
    private int min = 1;
    private int max = 6;
    public int roll() {
        Random random = new Random();
        return random.nextInt(max)+min;
    }
}
