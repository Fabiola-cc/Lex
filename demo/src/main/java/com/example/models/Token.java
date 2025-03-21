package com.example.models;

public class Token {
    private String lexema;
    private String tipo;
    
    public Token(String lexema, String tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }
    
    // Getter for lexema
    public String getLexema() {
        return lexema;
    }
    
    // Setter for lexema
    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
    
    // Getter for tipo
    public String getTipo() {
        return tipo;
    }
    
    // Setter for tipo
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return "<" + lexema + ", " + tipo + ">";
    }
}