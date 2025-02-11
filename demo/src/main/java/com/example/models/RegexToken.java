package com.example.models;

public class RegexToken {
    private final String value;
    private final boolean isOperator;

    public RegexToken(String value, boolean isOperator) {
        this.value = value;
        this.isOperator = isOperator;
    }

    public String getValue() {
        return value;
    }

    public boolean getIsOperator() {
        return isOperator;
    }

}
