package com.example.Modules.Error;

public class Lex_errors extends Exception {
    private String errorMessage;
    private int line;
    private int char_num;

    public Lex_errors(String errorMessage, int line, int char_num) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.line = line;
        this.char_num = char_num;
    }

    @Override
    public String toString() {
        return "Error léxico en la línea " + line + ", (caracter " + char_num + ")\n:" + errorMessage;
    }
}
