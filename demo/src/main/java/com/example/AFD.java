package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.models.RegexToken;
import com.example.models.node;

import java.util.ArrayList;

public class AFD {
    private static ArrayList<node> tree_info;
    private static HashMap<String, List<String>> transitions_table;
    private static List<String> States;
    private static List<Character> Symbols;

    public AFD(List<Object> tree) {
        tree_info = new ArrayList<node>();
        read_tree(tree);
        check_calculated_functions();
        find_symbols();

        transitions_table = new HashMap<String, List<String>>();
        States = new ArrayList<>();
        Symbols = new ArrayList<>();
    }

    public static ArrayList<node> getTree_info() {
        return tree_info;
    }

    private void read_tree(List<Object> tree) {

    }

    private static void check_calculated_functions() {
        for (node object : tree_info) {
            object.setNullable(Calculated_functions.isNullable(object));
        }
        for (node object : tree_info) {
            object.setFirstpos(Calculated_functions.getFirstPos(object));
        }
        for (node object : tree_info) {
            object.setLastpos(Calculated_functions.getLastPos(object));
        }
        for (node object : tree_info) {
            Calculated_functions.getFollowPos(object);
        }

    }

    private static void create_transitions(int root) {
        String initialState = tree_info.get(root).getFirstpos().toString().replaceAll("[\\[\\], ]", "");

        List<String> transitions = save_transitions(initialState.toCharArray());
        transitions_table.put(initialState, transitions);

        state_transitions(transitions);
    }

    private static List<String> save_transitions(char[] state) {
        Map<Character, List<String>> grupos = new HashMap<>();
        for (Character actualNode : state) {
            node n = tree_info.get(getTreeIndex(actualNode.toString()));
            grupos.computeIfAbsent(n.getValue(), k -> new ArrayList<>())
                    .add(n.getfollowpos().toString().replaceAll("[\\[\\], ]", ""));
        }
        List<String> result = new ArrayList<>();
        for (Character c : grupos.keySet()) {
            result.add(grupos.get(c).toString().replaceAll("[\\[\\], ]", ""));
        }
        return result;
    }

    private static void state_transitions(List<String> transitions) {
        for (String state : transitions) {
            if (!States.contains(state)) {
                States.add(state);
                List<String> actual_transitions = save_transitions(state.toCharArray());

                transitions_table.put(state, actual_transitions);
                state_transitions(actual_transitions);
            }
        }
    }

    private static void find_symbols() {
        for (node object : tree_info) {
            if (object.isAlphanumeric() && !Symbols.contains(object.getValue())) {
                Symbols.add(object.getValue());
            }
        }
    }

    public static int getTreeIndex(String elementName) {
        for (node node : getTree_info()) {
            if (node.getName().equals(elementName)) {
                return getTree_info().indexOf(node);
            }
        }
        System.err.println("ERROR: no se encontró el nodo dado");
        return -1;
    }
}