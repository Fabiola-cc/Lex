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
    private HashMap<List<String>, String> Renamed_states;
    private List<Character> Symbols;
    private String initial_state;
    private List<String> acceptance_states;
    private List<String> initialState;

    public Direct_AFD() {
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
            System.out.println(object.getName() + ": " + object.isNullable());
            System.out.println("firstpos: " + object.getFirstpos());
            System.out.println("lastpos: " + object.getLastpos());
            System.out.println("followpos: " + object.getfollowpos());
            System.out.println();
        }

        create_transitions();
        for (List<String> s : transitions_table.keySet()) {
            System.out.println(s + "\t" + transitions_table.get(s));
        }

        rename_transitions();
        List<String> states = new ArrayList<>();
        System.out.println("\n ESTADOS RENOMBRADOS");
        for (List<String> state : Renamed_states.keySet()) {
            System.out.println(state + "\t" + Renamed_states.get(state));
        }
        System.out.println("\n TRANSICIONES RENOMBRADAS");
        for (String ns : Renamed_transitions.keySet()) {
            System.out.println(ns + "\t" + Renamed_transitions.get(ns));
        }

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
        System.out.println(tree_info.get(root).getValue());
        System.out.println(tree_info.get(root).isAlphanumeric());
        initialState = tree_info.get(root).getFirstpos();

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
        for (List<String> state : transitions) {
            if (!States.contains(state)) {
                States.add(state);
                List<List<String>> actual_transitions = save_transitions(state);

                transitions_table.put(state, actual_transitions);
                state_transitions(actual_transitions);
            }
        }
    }

    /**
     * Guarda las transiciones en un formato más amigable
     */
    private void rename_transitions() {
        for (List<String> state : States) {
            int stateName = States.size() - States.indexOf(state);
            Renamed_states.put(state, "S" + stateName);
        }

        initial_state = Renamed_states.get(initialState);
        for (List<String> state : transitions_table.keySet()) {
            String renamed_state = Renamed_states.get(state);
            List<String> renamed_transitions = new ArrayList<>();
            for (List<String> transition : transitions_table.get(state)) {
                renamed_transitions.add(Renamed_states.get(transition));
            }
            Renamed_transitions.put(renamed_state, renamed_transitions);
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
        System.out.println("ERROR: no se encontró el nodo dado");
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

        Direct_AFD generator = new Direct_AFD();
        AFD afd = generator.generate_directAfd(result);

        System.out.println("AFN generado con el método directo");
    }
}