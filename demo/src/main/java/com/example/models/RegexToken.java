package com.example.models;

public class RegexToken {
    private final Character value;
    private final boolean isOperator;

    public RegexToken(Character value, boolean isOperator) {
        this.value = value;
        this.isOperator = isOperator;
    }

    public Character getValue() {
        return value;
    }

    public boolean getIsOperator() {
        return isOperator;
    }

}
