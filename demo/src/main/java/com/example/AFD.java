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
        // int savedNodes = tree_info.size();
        // int nodesPassed = 0;
        // int tree_elements = tree.size();
        // for (Object element : tree) {
        // if (element instanceof List) {
        // read_tree((List<Object>) element);
        // } else {
        // RegexToken token = (RegexToken) element;
        // node newNode = new node(token.getValue(), !token.getIsOperator());
        // tree_info.add(newNode);
        // if (savedNodes != 0 | nodesPassed != 0) {
        // if (nodesPassed != 0 && savedNodes == 0) {
        // tree_info.get(0).getNodes().add(savedNodes + 1);
        // }
        // tree_info.get(savedNodes - (tree_elements - 1)).getNodes().add(savedNodes +
        // 1);
        // }
        // }
        // nodesPassed++;
        // }
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

    public static void main(String[] args) {
        /* EJEMPLO DE NODOS YA ESTRUCTURADOS */
        tree_info = new ArrayList<node>();
        transitions_table = new HashMap<String, List<String>>();
        States = new ArrayList<>();
        Symbols = new ArrayList<>();

        node a = new node('|', false);
        a.setName("o1");
        node b = new node('a', true);
        b.setName("1");
        node c = new node('b', true);
        c.setName("2");
        node d = new node('*', false);
        d.setName("o2");
        node e = new node('‧', false);
        e.setName("o3");
        node f = new node('a', true);
        f.setName("3");
        node g = new node('‧', false);
        g.setName("o4");
        node h = new node('b', true);
        h.setName("4");

        tree_info.add(a);
        tree_info.add(b);
        tree_info.add(c);
        tree_info.add(d);
        tree_info.add(e);
        tree_info.add(f);
        tree_info.add(g);
        tree_info.add(h);

        a.getNodes().add(1); // índice de 'a' en tree_info
        a.getNodes().add(2); // índice de 'b' en tree_info
        d.getNodes().add(0); // índice de '|' en tree_info
        e.getNodes().add(3); // índice de '*' en tree_info
        e.getNodes().add(5); // índice de 'a' en tree_info
        g.getNodes().add(4); // índice de '.' en tree_info
        g.getNodes().add(7); // índice de 'b' en tree_info

        int root = 4;
        find_symbols();
        check_calculated_functions();
        create_transitions(root);

        for (node object : tree_info) {
            System.err.println(object.getName() + ": " + object.isNullable());
            System.err.println(object.getFirstpos());
            System.err.println(object.getLastpos());
            System.err.println(object.getfollowpos());
            System.err.println();
        }

        for (Character blabla : Symbols) {
            System.err.print(blabla + ", ");
        }
        System.err.println();

        for (String s : transitions_table.keySet()) {
            System.err.println(s + "\t" + transitions_table.get(s));
        }
    }
}