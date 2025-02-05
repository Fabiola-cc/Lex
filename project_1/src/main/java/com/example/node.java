package com.example;

public class node {
    private boolean isNullable;
    private boolean isAlphanumeric;
    private String value;
    private Integer[] nodes; // Contiene los índices de los 'hijos' (para operadores)
    private Integer[] firstpos;
    private Integer[] lastpos;
    private Integer[] followtpos;

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public boolean isAlphanumeric() {
        return isAlphanumeric;
    }

    public void setAlphanumeric(boolean isAlphanumeric) {
        this.isAlphanumeric = isAlphanumeric;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer[] getNodes() {
        return nodes;
    }

    public void setNodes(Integer[] nodes) {
        this.nodes = nodes;
    }

    public Integer[] getFirstpos() {
        return firstpos;
    }

    public void setFirstpos(Integer[] firstpos) {
        this.firstpos = firstpos;
    }

    public Integer[] getLastpos() {
        return lastpos;
    }

    public void setLastpos(Integer[] lastpos) {
        this.lastpos = lastpos;
    }

    public Integer[] getFollowtpos() {
        return followtpos;
    }

    public void setFollowtpos(Integer[] followtpos) {
        this.followtpos = followtpos;
    }
}
