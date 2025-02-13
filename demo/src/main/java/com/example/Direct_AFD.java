package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.example.Drawings.Draw_AFD;
import com.example.models.AFD;
import com.example.models.RegexToken;
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

    public AFD generate_directAfd(List<node> tree) {
        read_tree(tree);
        find_symbols();

        check_calculated_functions();

        // Según la definición del árbol la raíz siempre es la última en añadirse al
        // listado de nodos
        int root = tree_info.size() - 1;

        get_AcceptedNodes(root);
        create_transitions(root);
        for (List<String> s : transitions_table.keySet()) {
            System.out.println(s + "\t" + transitions_table.get(s));
        }

        rename_transitions();
        List<String> states = new ArrayList<>();
        System.out.println("\n Nombres de estados");
        for (List<String> state : Renamed_states.keySet()) {
            System.out.println(state + "\t" + Renamed_states.get(state));
            states.add(Renamed_states.get(state));
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
     * @param root
     * 
     *             Saves all nodes that can be last in regex
     */
    private void get_AcceptedNodes(int root) {
        node rootNode = tree_info.get(root);
        acceptedNodes = rootNode.getLastpos();
    }

    /**
     * Crea la transición inicial y empieza el loop para el resto de las
     * transiciones
     */
    private void create_transitions(int root) {
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
            if (!newState.isEmpty()) {
                result.add(newState);
            }
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

    /**
     * Guarda las transiciones en un formato más amigable
     */
    private void rename_transitions() {
        // Genera los nuevos nombres para los estados, basado en su posición
        for (List<String> state : States) {
            int stateName = States.size() - States.indexOf(state);
            Renamed_states.put(state, "S" + stateName);
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
        afd.printAFD();

        Draw_AFD drawerAfd = new Draw_AFD(afd);
        drawerAfd.displayAutomaton();

        String inputString = "baabb#";

        ArrayList<ArrayList<String>> derivationProcess = afd.derivation(afd.getInitial_state(), inputString);
        Boolean resultString = afd.accepted(derivationProcess.get(derivationProcess.size() - 1).get(0),
                afd.getAcceptance_states());
        if (resultString) {
            System.out.println("La cadena es aceptada");
        } else {
            System.out.println("La cadena no es aceptada");
        }

        System.out.println("\nPRINT MINIMIZED");
        AFD miniAfd = afd.minimize();
        miniAfd.printAFD();

        ArrayList<ArrayList<String>> derivationProcess2 = miniAfd.derivation(miniAfd.getInitial_state(), inputString);
        Boolean resultString2 = miniAfd.accepted(derivationProcess2.get(derivationProcess2.size() - 1).get(0),
                miniAfd.getAcceptance_states());
        if (resultString2) {
            System.out.println("La cadena es aceptada");
        } else {
            System.out.println("La cadena no es aceptada");
        }
    }
}