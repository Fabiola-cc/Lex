package com.example.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AFD {
    private HashMap<String, List<String>> transitions_table;
    private List<String> states;
    private List<Character> alphabet;
    private String initial_state;
    private List<String> acceptance_states;

    public AFD(HashMap<String, List<String>> transitions_table, List<String> states, List<Character> alphabet,
            String initial_state, List<String> acceptance_states) {
        this.transitions_table = transitions_table;
        this.states = states;
        this.alphabet = alphabet;
        this.initial_state = initial_state;
        this.acceptance_states = acceptance_states;
    }

    public HashMap<String, List<String>> getTransitions_table() {
        return transitions_table;
    }

    public void setTransitions_table(HashMap<String, List<String>> transitions_table) {
        this.transitions_table = transitions_table;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<Character> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(List<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public String getInitial_state() {
        return initial_state;
    }

    public void setInitial_state(String initial_state) {
        this.initial_state = initial_state;
    }

    public List<String> getAcceptance_states() {
        return acceptance_states;
    }

    public void setAcceptance_states(List<String> acceptance_states) {
        this.acceptance_states = acceptance_states;
    }

    /*
     * q --> state
     * a --> alphabet element
     * d --> list of transitions
     * 
     * @return transition's value
     */
    private String transition(String q, String symbol) {
        String transited_state = "";
        List<String> transitions = transitions_table.get(q);
        for (int i = 0; i < alphabet.size(); i++) {
            if (symbol.equals(String.valueOf(alphabet.get(i)))) {
                transited_state = transitions.get(i);
            }
        }
        return transited_state;
    }

    /*
     * q --> state
     * F --> Acceptance state
     */
    public Boolean accepted(String q, List<String> F) {
        return F.contains(q);
    }

    /*
     * q --> state
     * w --> string
     * 
     * @return secuence of states
     */
    public ArrayList<ArrayList<String>> derivation(String q, String w) {
        String state = q;
        ArrayList<ArrayList<String>> transitions = new ArrayList<>();

        for (int i = 0; i < w.length(); i++) {
            ArrayList<String> actualT = new ArrayList<>();
            actualT.add(state);
            actualT.add(String.valueOf(w.charAt(i)));

            if (String.valueOf(w.charAt(i)).equals("#") && i == w.length() - 1) {
                // Detenerse al llegar al centinela
                transitions.add(actualT);
                break;
            }

            state = transition(state, w.charAt(i) + "");
            actualT.add(state);

            transitions.add(actualT);

        }

        return transitions;
    }

    /**
     * Used for minimization function
     * Based on code from:
     * https://github.com/Fabiola-cc/Automatas-Finitos/tree/main.
     * 
     * @param Q
     * @param F
     * @return
     */
    private Set<List<String>> differenceList(Set<String[]> Q, Set<String[]> F) {

        Set<List<String>> q = new HashSet<>();
        Set<List<String>> f = new HashSet<>();

        for (String[] arr : Q) {
            q.add(Arrays.asList(arr));
        }
        for (String[] arr : F) {
            f.add(Arrays.asList(arr));
        }

        Set<List<String>> normalizedQ = new HashSet<>();
        Set<List<String>> normalizedF = new HashSet<>();

        // Normaliza las listas de ambos sets
        for (List<String> qList : q) {
            List<String> sortedList = new ArrayList<>(qList);
            Collections.sort(sortedList);
            normalizedQ.add(sortedList);
        }

        for (List<String> fList : f) {
            List<String> sortedList = new ArrayList<>(fList);
            Collections.sort(sortedList);
            normalizedF.add(sortedList);
        }

        // Realiza la diferencia con las listas normalizadas
        normalizedQ.removeAll(normalizedF);

        return normalizedQ;
    }

    /**
     * Uses Glushkov algorithm to reduce AFD
     * Based on code from:
     * https://github.com/Fabiola-cc/Automatas-Finitos/tree/main.
     * 
     * @return
     */
    public AFD minimize() {
        // Convert transitions into HashMap<String, HashMap<String, String>>
        HashMap<String, HashMap<String, String>> transitionMap = new HashMap<>();

        // Aquí, stateFrom es la clave principal y cada entrada de transitionMap
        // contiene un HashMap<symbol, stateTo>, lo que facilita la consulta posterior
        for (String stateFrom : transitions_table.keySet()) {
            List<String> transition = transitions_table.get(stateFrom);
            transitionMap.putIfAbsent(stateFrom, new HashMap<>());
            for (int i = 0; i < transition.size(); i++) {
                String symbol = alphabet.get(i) + "";
                String stateTo = transition.get(i) != null ? transition.get(i) : "";

                transitionMap.get(stateFrom).put(symbol, stateTo);
            }
        }

        // Convert states and acceptance states into sets
        Set<String> F = new HashSet<>(acceptance_states);
        Set<String> Q = new HashSet<>(states);
        Set<String> P = new HashSet<>(Q);
        P.removeAll(F);

        Set<String[]> combinations = new HashSet<>();

        // Creating pairs of states for minimization
        for (String i : P) {
            for (String j : F) {
                String[] pair = { i, j };
                Arrays.sort(pair); // Orden para evitar duplicados con diferente orden
                combinations.add(pair);

            }
        }

        Set<String[]> newCombinations = new HashSet<>();
        Set<String[]> oldCombinations;
        do {
            oldCombinations = new HashSet<>(combinations);
            for (String[] combination : combinations) {
                for (String alphabet : transitionMap.keySet()) {
                    HashMap<String, String> alphabetTransition = transitionMap.get(alphabet);
                    List<String> firstValues = new ArrayList<>();
                    List<String> secondValues = new ArrayList<>();

                    if (alphabetTransition.containsValue(combination[0])
                            && alphabetTransition.containsValue(combination[1])) {
                        for (String key : alphabetTransition.keySet()) {
                            if (alphabetTransition.get(key).equals(combination[0])
                                    && !acceptance_states.contains(key)) {
                                firstValues.add(key);
                            }
                            if (alphabetTransition.get(key).equals(combination[1])
                                    && !acceptance_states.contains(key)) {
                                secondValues.add(key);
                            }
                        }
                    }

                    for (String i : firstValues) {
                        for (String j : secondValues) {
                            List<String> newCombinationList = Arrays.asList(i, j);
                            boolean exists = combinations.stream().map(Arrays::asList)
                                    .anyMatch(c -> c.equals(newCombinationList));
                            if (!exists) {
                                newCombinations.add(new String[] { i, j });
                            }
                        }
                    }
                }
            }
            combinations.addAll(newCombinations);
            newCombinations.clear();

        } while (!differenceList(combinations, oldCombinations).isEmpty());

        Set<String[]> matrix_combinations = new HashSet<>();
        for (String q1 : states) {
            for (String q2 : states) {
                if (!q1.equals(q2)) {
                    matrix_combinations.add(new String[] { q1, q2 });
                }
            }
        }

        Set<List<String>> states_to_combine = differenceList(matrix_combinations, combinations);
        Set<String> combinedStates = new HashSet<>();

        for (List<String> q : states_to_combine) {
            combinedStates.add(String.join("", q));
        }

        // Define new states
        List<String> newStates = new ArrayList<>(states);
        for (String combined : combinedStates) {
            newStates.removeIf(state -> combined.contains(state));
            newStates.add(combined);
        }

        // Define new initial state
        String newInitialState = newStates.stream().filter(s -> s.contains(initial_state)).findFirst()
                .orElse(initial_state);

        // Define new acceptance states
        List<String> newAcceptanceStates = new ArrayList<>();
        for (String x : newStates) {
            if (acceptance_states.stream().anyMatch(x::contains)) {
                newAcceptanceStates.add(x);
            }
        }

        // Define la nueva tabla de transiciones
        HashMap<String, List<String>> newTransitionsTable = new HashMap<>();

        for (String newState : newStates) {
            List<String> transitionsList = new ArrayList<>(Collections.nCopies(alphabet.size(), "")); // Iniciar con
                                                                                                      // tamaño fijo

            // Iterar sobre los estados originales que forman parte del estado combinado
            for (String originalState : states) {
                if (newState.contains(originalState) && transitions_table.containsKey(originalState)) {
                    List<String> transition = transitions_table.get(originalState);

                    for (int i = 0; i < transition.size(); i++) {
                        String destination = transition.get(i) != null ? transition.get(i) : "";

                        // Verificar si el estado de destino pertenece a un estado combinado
                        for (String combined : combinedStates) {
                            if (combined.contains(destination)) {
                                destination = combined;
                                break;
                            }
                        }

                        // Agregar la transición a la nueva tabla de transiciones
                        transitionsList.set(i, destination);
                    }
                }
            }

            // Agregar el nuevo estado y sus transiciones a la nueva tabla
            newTransitionsTable.put(newState, transitionsList);
        }

        return new AFD(newTransitionsTable, newStates, alphabet, newInitialState, newAcceptanceStates);
    }

    public void printAFD() {
        System.out.println("\n Alfabeto: " + alphabet.toString());

        System.out.println("\n Estado inicial: " + initial_state);

        System.out.println("\n Estados de aceptación: " + acceptance_states.toString());

        System.out.println("\n Todos los estados: " + states.toString());

        System.out.println("\n TRANSICIONES");
        for (String ns : transitions_table.keySet()) {
            System.out.println(ns + "\t" + transitions_table.get(ns));
        }

    }

    public static void main(String[] args) {

        // Definir estados
        List<String> states = Arrays.asList("q0", "q1", "q2", "q3");

        // Definir alfabeto
        List<Character> alphabet = Arrays.asList('0', '1');

        // Definir estado inicial
        String initial_state = "q0";

        // Definir estados de aceptación
        List<String> acceptance_states = Arrays.asList("q1", "q3");

        // Definir transiciones
        HashMap<String, List<String>> transitions = new HashMap<>();
        transitions.put("q0", Arrays.asList("q1", "q2"));
        transitions.put("q1", Arrays.asList("q0", "q3"));
        transitions.put("q2", Arrays.asList("q3", "q0"));
        transitions.put("q3", Arrays.asList("q2", "q1"));

        AFD afd = new AFD(transitions, states, alphabet, initial_state, acceptance_states);
        afd.printAFD();

        AFD mini = afd.minimize();
        mini.printAFD();
    }
}
