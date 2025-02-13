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
    private HashMap<List<String>, List<List<String>>> transitions_table;
    private HashMap<String, List<String>> Renamed_transitions;
    private List<List<String>> States;
    private HashMap<String, List<String>> Renamed_states;
    private List<Character> Symbols;
    private String initial_state;
    private List<String> acceptance_states;

    public Direct_AFD(List<node> tree) {
        tree_info = new ArrayList<node>();
        transitions_table = new HashMap<>();
        States = new ArrayList<>();
        Symbols = new ArrayList<>();
        Renamed_states = new HashMap<>();
        Renamed_transitions = new HashMap<>();
    }

    public AFD generate_directAfd(List<node> tree) {
        read_tree(tree);
        find_symbols();
        System.out.print("Symbols: ");
        for (Character blabla : Symbols) {
            System.out.print(blabla + ", ");
        }
        check_calculated_functions();
        for (node object : tree_info) {
            System.err.println(object.getName() + ": " + object.isNullable());
            System.err.println("firstpos: " + object.getFirstpos());
            System.err.println("lastpos: " + object.getLastpos());
            System.err.println("followpos: " + object.getfollowpos());
            System.err.println();
        }

        create_transitions();
        for (List<String> s : transitions_table.keySet()) {
            System.err.println(s + "\t" + transitions_table.get(s));
        }
        List<String> states = (List<String>) Renamed_states.keySet();
        return new AFD(Renamed_transitions, states, Symbols, initial_state, acceptance_states);
    }

    public static ArrayList<node> getTree_info() {
        return tree_info;
    }

    private void read_tree(List<node> tree) {
        tree_info = (ArrayList<node>) tree;
    }

    private void check_calculated_functions() {
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

    /**
     * Crea la transición inicial y empieza el loop para el resto de las
     * transiciones
     */
    private void create_transitions() {
        // Según la definición del árbol la raíz siempre es la última en añadirse al
        // listado de nodos
        int root = tree_info.size() - 2;
        // El estado inicial es el firstpos del nodo raíz
        List<String> initialState = tree_info.get(root).getFirstpos();

        // Solicita la transición basada en el estado inicial
        List<List<String>> transitions = save_transitions(initialState);

        // Guarda la primera transición en la tabla definida
        transitions_table.put(initialState, transitions); // TRANSITIONS: CAMBIAR TIPO

        state_transitions(transitions);
    }

    /*
     * Genera las transiciones del estado correspondiente
     */
    private List<List<String>> save_transitions(List<String> state) {
        // Cada estado es un list string, se transiciona a varios estados
        Map<Character, List<List<String>>> grupos = new HashMap<>();
        // Busca la transición de cada nodo según su valor
        for (String actualNode : state) {
            node n = tree_info.get(getTreeIndex(actualNode));
            // Si la transición para el símbolo no ha sido guardado se añade al mapa
            grupos.computeIfAbsent(n.getValue(), k -> new ArrayList<>())
                    .add(n.getfollowpos()); // añade la transición a un nuevo estado
        }
        // Lista de estados a los que se transiciona
        List<List<String>> result = new ArrayList<>();
        for (Character c : grupos.keySet()) {
            List<String> newState = new ArrayList<>();
            for (List<String> list : grupos.get(c)) {
                for (String nodeString : list) {
                    if (!newState.contains(nodeString)) {
                        newState.add(nodeString);
                    }
                }
            }
            result.add(newState);
        }
        return result;
    }

    /**
     * @param transitions
     * 
     *                    Crea las nuevas transiciones basado en las creadas con el
     *                    estado inicial
     */
    private void state_transitions(List<List<String>> transitions) {
        int counter = 0;
        for (List<String> state : transitions) {
            if (!States.contains(state)) {
                States.add(state);
                Renamed_states.put("S" + counter, state);
                List<List<String>> actual_transitions = save_transitions(state);

                transitions_table.put(state, actual_transitions);
                state_transitions(actual_transitions);
            }
        }
    }

    private void find_symbols() {
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

        postfixExample.forEach(token -> System.out.print(token.getValue() + " "));
        System.out.println('\n');

        List<node> result = calculator.convertPostfixToTree(postfixExample);

        Direct_AFD generator = new Direct_AFD(result);
        generator.generate_directAfd(result);

        System.out.print("Symbols: ");
        for (Character blabla : generator.Symbols) {
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

        for (List<String> s : generator.transitions_table.keySet()) {
            System.err.println(s + "\t" + generator.transitions_table.get(s));
        }
    }
}