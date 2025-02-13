package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

public class Direct_AFD {
    public static ArrayList<node> tree_info;
    private static HashMap<String, List<String>> transitions_table;
    private static List<String> States;
    private static List<Character> Symbols;
    public String initial_state;
    public List<String> acceptance_states;

    public Direct_AFD(List<node> tree) {
        tree_info = new ArrayList<node>();
        transitions_table = new HashMap<String, List<String>>();
        States = new ArrayList<>();
        Symbols = new ArrayList<>();
    }

    public AFD generate_directAfd(List<node> tree) {
        read_tree(tree);
        check_calculated_functions();
        find_symbols();
        create_transitions();

        return new AFD(transitions_table, States, Symbols, initial_state, acceptance_states);
    }

    public static ArrayList<node> getTree_info() {
        return tree_info;
    }

    private void read_tree(List<node> tree) {
        tree_info = (ArrayList<node>) tree;
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

    private static void create_transitions() {
        int root = tree_info.size() - 1;
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

    public static void main(String[] args) {
        // Ejemplo de uso
        Calculate_tree calculator = new Calculate_tree();
        List<RegexToken> postfixExample = new ArrayList<>();

        // Ejemplo: a b | * a ‧ b ‧ (representa (a|b)* ‧ a ‧ b)
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("#", false));
        postfixExample.add(new RegexToken("‧", true));

        postfixExample.forEach(token -> System.out.print(token.getValue() + " "));
        System.out.println('\n');

        List<node> result = calculator.convertPostfixToTree(postfixExample);

        Direct_AFD generator = new Direct_AFD(result);
        generator.generate_directAfd(result);

        System.out.print("Symbols: ");
        for (Character blabla : Symbols) {
            System.out.print(blabla + ", ");
        }
        System.out.println();

        for (node object : tree_info) {
            System.err.println(object.getName() + ": " + object.isNullable());
            System.err.println("firstpos: " + object.getFirstpos());
            System.err.println("lastpos: " + object.getLastpos());
            System.err.println("followpos: " + object.getfollowpos());
            System.err.println();
        }

        for (String s : transitions_table.keySet()) {
            System.err.println(s + "\t" + transitions_table.get(s));
        }
    }
}