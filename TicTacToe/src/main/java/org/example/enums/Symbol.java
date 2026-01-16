package org.example.enums;

public enum Symbol {
    X('X'),
    O('O'),
    EMPTY('_');

    private final char symbol;

    Symbol(char c) {
        this.symbol = c;
    }

    public char getSymbol() {
        return symbol;
    }
}
