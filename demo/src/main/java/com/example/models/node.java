package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class node {
    private boolean isNullable;
    private boolean isAlphanumeric;
    private Character value;
    private String name;
    private ArrayList<Integer> nodes; // Contiene los índices de los 'hijos' (para operadores)
    private List<String> firstpos;
    private List<String> lastpos;
    private List<String> followpos;

    public node(Character value, boolean isAlphanumeric) {
        this.value = value;
        this.isAlphanumeric = isAlphanumeric;
        this.nodes = new ArrayList<Integer>();
        this.firstpos = new ArrayList<String>();
        this.followpos = new ArrayList<String>();
        this.lastpos = new ArrayList<String>();
    }

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

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Integer> nodes) {
        this.nodes = nodes;
    }

    public List<String> getFirstpos() {
        return firstpos;
    }

    public void setFirstpos(List<String> firstpos) {
        this.firstpos = firstpos;
    }

    public List<String> getLastpos() {
        return lastpos;
    }

    public void setLastpos(List<String> lastpos) {
        this.lastpos = lastpos;
    }

    public List<String> getfollowpos() {
        return followpos;
    }

    public void setfollowpos(List<String> followpos) {
        this.followpos = followpos;
    }
}
