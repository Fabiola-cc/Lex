package com.example.models;

public class RegexToken {
    private final String value;
    private final boolean isOperator;
    private boolean isToken;

    public RegexToken(String value, boolean isOperator) {
        this.value = value;
        this.isOperator = isOperator;
        this.isToken = false;
    }

    public String getValue() {
        return value;
    }

    public boolean getIsOperator() {
        return isOperator;
    }

    public boolean getIsToken() { return isToken; }

    public void setIsToken(boolean isToken) { this.isToken = isToken; }

}
