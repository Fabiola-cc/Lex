package com.example;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.example.models.AFD;
import com.example.models.node;

public class Direct_AFD {
    public static ArrayList<node> tree_info;

    private HashMap<List<String>, List<List<String>>> transitions_table;
    private List<List<String>> States;
    private List<Character> Symbols;
    private List<List<String>> acceptanceStates;
    private List<String> acceptedNodes;
    private List<String> initialState;

    private HashMap<String, List<String>> Renamed_transitions;
    private HashMap<List<String>, String> Renamed_states;
    private String initial_state;
    private List<String> acceptance_states;

    /**
     * Clase que representa la construcción directa de un AFD a partir de un árbol
     * de análisis sintáctico
     * 
     * El constructor inicializa las estructuras de datos utilizadas en la
     * construcción del AFD
     */
    public Direct_AFD() {
        tree_info = new ArrayList<node>();

        transitions_table = new HashMap<>();
        States = new ArrayList<>();
        Symbols = new ArrayList<>();
        acceptanceStates = new ArrayList<>();
        acceptedNodes = new ArrayList<>();

        Renamed_states = new HashMap<>();
        Renamed_transitions = new HashMap<>();
        acceptance_states = new ArrayList<>();
    }

    /**
     * Método principal para generar el AFD directo a partir del árbol de sintaxis
     * 
     * @param tree
     * @return
     */
    public AFD generate_directAfd(List<node> tree) {
        read_tree(tree); // Carga la información del árbol en la lista tree_info
        find_symbols(); // Extrae los símbolos del alfabeto a partir del árbol

        check_calculated_functions(); // Calcula las funciones de nullable, firstpos, lastpos y followpos

        // Según la definición del árbol la raíz siempre es la última en añadirse al
        // listado de nodos
        int root = tree_info.size() - 1;

        get_AcceptedNodes(root); // Obtiene los nodos de aceptación desde el árbol
        create_transitions(root); // Genera las transiciones del AFD

        System.out.println("Tabla de transiciones");
        System.out.println("\t" + Symbols);
        for (List<String> s : transitions_table.keySet()) {
            System.out.println(s + "\t" + transitions_table.get(s));
        }

        rename_transitions(); // Renombra los estados y transiciones para hacerlas más comprensibles

        // Impresión de los nombres de los estados renombrados
        List<String> states = new ArrayList<>();
        System.out.println("\n Nombres de estados");
        for (List<String> state : Renamed_states.keySet()) {
            System.out.println(state + "\t" + Renamed_states.get(state));
            states.add(Renamed_states.get(state));
        }

        // Retorna el AFD construido
        return new AFD(Renamed_transitions, states, Symbols, initial_state, acceptance_states);
    }

    /**
     * Método para obtener la información del árbol de análisis sintáctico desde
     * otra clase
     * 
     * @return
     */
    public static ArrayList<node> getTree_info() {
        return tree_info;
    }

    /**
     * Lee el árbol y lo almacena en tree_info
     * 
     * @param tree
     */
    private void read_tree(List<node> tree) {
        tree_info = (ArrayList<node>) tree;
    }

    /**
     * Calcula las funciones nullable, firstpos, lastpos y followpos para cada nodo
     * del árbol
     */
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
            if (!object.isAlphanumeric()) { // solo aplica a ciertos nodos operadores
                Calculated_functions.getFollowPos(object);
            }
        }

    }

    /**
     * Guarda todos los nodos que pueden ser nodos de aceptación en la expresión
     * regular
     * 
     * @param root Índice de la raíz del árbol
     */
    private void get_AcceptedNodes(int root) {
        node rootNode = tree_info.get(root - 2);
        acceptedNodes = rootNode.getLastpos();
        acceptedNodes.add(tree_info.get(root - 1).getName());
    }

    /**
     * Crea la transición inicial y empieza el loop para el resto de las
     * transiciones
     */
    private void create_transitions(int root) {
        // El estado inicial es el firstpos del nodo raíz
        initialState = tree_info.get(root).getFirstpos();

        States.add(initialState);

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
        Map<Character, List<List<String>>> grupos = new LinkedHashMap<>();
        // Busca la transición de cada nodo según su valor
        for (String actualNode : state) {
            node n = tree_info.get(getTreeIndex(actualNode));

            // Crear tansiciones 'vacías' para cada símbolo
            for (Character simbolo : Symbols) {
                // Verifica si hay transiciones para este símbolo, si no, crea una vacía
                grupos.computeIfAbsent(simbolo, k -> new ArrayList<>());
            }

            if (n.getValue() == '#' && getTreeIndex(actualNode) == tree_info.size() - 2) {
                break;
            }

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
     * Crea las nuevas transiciones basado en las creadas con el
     * estado inicial
     * 
     * @param transitions transitions Lista de transiciones generadas para un estado
     * 
     */
    private void state_transitions(List<List<String>> transitions) {
        for (List<String> state : transitions) {
            if (state.size() != 0) {
                if (!States.contains(state)) {
                    States.add(state);
                    List<List<String>> actual_transitions = save_transitions(state);

                    // Guardar estados de aceptación, es decir, aquellos que contienen nodos finales
                    for (String value : acceptedNodes) {
                        if (state.contains(value)) {
                            acceptanceStates.add(state);
                        }
                    }

                    transitions_table.put(state, actual_transitions);
                    state_transitions(actual_transitions);
                }
            }
        }
    }

    /**
     * Guarda las transiciones en un formato más amigable
     */
    private void rename_transitions() {
        // Genera los nuevos nombres para los estados, basado en su posición
        for (List<String> state : States) {
            if (state.size() == 0) {
                Renamed_states.put(state, "");
            } else {
                int stateName = States.indexOf(state);
                Renamed_states.put(state, "S" + stateName);
            }
        }

        // Registra el estado inicial renombrado
        initial_state = Renamed_states.get(initialState);

        // Registra las nuevas transiciones con los nuevos nombres
        for (List<String> state : transitions_table.keySet()) {
            String renamed_state = Renamed_states.get(state);
            List<String> renamed_transitions = new ArrayList<>();
            for (List<String> transition : transitions_table.get(state)) {
                renamed_transitions.add(Renamed_states.get(transition));
            }
            Renamed_transitions.put(renamed_state, renamed_transitions);
        }

        // Registra los estados finales con los nuevos nombres
        for (List<String> accepted_state : acceptanceStates) {
            String renamed_Astate = Renamed_states.get(accepted_state);
            acceptance_states.add(renamed_Astate);
        }
    }

    /**
     * Obtiene los símbolos de la expresión regular desde el árbol de análisis
     */
    private void find_symbols() {
        for (node object : tree_info) {
            if (tree_info.indexOf(object) == tree_info.size() - 2) {
                // Omitir centinela
                break;
            }
            if (object.isAlphanumeric() && !Symbols.contains(object.getValue())) {
                Symbols.add(object.getValue());
            }
        }
    }

    /**
     * Obtiene el índice de un nodo en la lista según su nombre
     * 
     * @param elementName nombre del nodo
     * @return índice del nodo en el árbol
     */
    public static int getTreeIndex(String elementName) {
        // Recorre el árbol hasta encontrar el nodo que coincide
        for (node node : getTree_info()) {
            if (node.getName().equals(elementName)) {
                return getTree_info().indexOf(node);
            }
        }
        System.out.println("ERROR: no se encontró el nodo dado");
        return -1;
    }

}