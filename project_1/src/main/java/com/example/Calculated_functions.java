package com.example;

public class Calculated_functions {
    public Integer[] getFirstPos(String node, String[] tree_info) {

    }

    public Integer[] getLastPos(String node, String[] tree_info) {

    }

    public Integer[] getFollowPos(String node, String[] tree_info, Integer[] firstpos, Integer[] lastpos) {

    }

    public Boolean isNullable(String node, String[] tree_info, Boolean isAlphanumeric, int index) {
        if (node == "_") { // Epsilon leaf
            return true
        } else if(isAlphanumeric) {
            return false
        } 

        if (node == "|") {
            
        }
    }
}
