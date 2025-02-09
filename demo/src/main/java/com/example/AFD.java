package com.example;

import java.util.HashMap;
import java.util.List;

import com.example.models.RegexToken;
import com.example.models.node;

import java.util.ArrayList;

public class AFD {
    private ArrayList<node> tree_info;
    private ArrayList<node> functions_table;
    private HashMap<String, List<String>>[] transitions_table;

    public AFD(List<Object> tree) {
        tree_info = read_tree(tree);
        ;
        functions_table = new ArrayList<node>();
    }

    public static boolean isAlphanumeric(String str) {
        return str.matches("[a-zA-Z0-9]+");
    }

    private ArrayList<node> read_tree(List<Object> tree) {

        return null;

    }

    private void check_calculated_functions() {
        for (int i = 0; i < tree_info.size(); i++) {
            node actualNode = tree_info.get(i);
            if (actualNode.isAlphanumeric()) {
                actualNode.setNullable(false);

            }
        }

    }

    private void create_transitions() {

    }

}