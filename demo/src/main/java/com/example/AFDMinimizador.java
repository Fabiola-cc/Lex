package com.example;

import java.util.*;
import com.example.models.AFD;

public class AFDMinimizador {
    private HashMap<String, List<String>> transitions_table;
    private List<String> states;
    private List<String> alphabet;
    private String initial_state;
    private List<String> acceptance_states;

    public AFDMinimizador(AFD model) {
        this.transitions_table = model.getTransitions_table();
        this.states = model.getStates();
        this.alphabet = model.getAlphabet();
        this.initial_state = model.getInitial_state();
        this.acceptance_states = model.getAcceptance_states();
    }

    public AFD minimize() {
        Set<Set<String>> P = new HashSet<>();
        Queue<Set<String>> L = new LinkedList<>(); // Usamos una cola en lugar de un Set

        Set<String> F = new HashSet<>(acceptance_states);
        Set<String> QMinusF = new HashSet<>(states);
        QMinusF.removeAll(F);

        P.add(F);
        P.add(QMinusF);
        L.add(F.size() < QMinusF.size() ? F : QMinusF);

        while (!L.isEmpty()) {
            Set<String> S = L.poll(); // Extraemos sin modificar un Set
            for (String a : alphabet) {
                int symbolIndex = alphabet.indexOf(a);
                Set<Set<String>> newPartitions = new HashSet<>();

                // Creamos una copia de P antes de modificarlo
                List<Set<String>> partitionsCopy = new ArrayList<>(P);

                for (Set<String> B : partitionsCopy) {
                    Set<String> B1 = new HashSet<>();
                    Set<String> B2 = new HashSet<>();

                    for (String state : B) {
                        String target = transitions_table.get(state).get(symbolIndex);
                        if (S.contains(target)) {
                            B1.add(state);
                        } else {
                            B2.add(state);
                        }
                    }

                    if (!B1.isEmpty() && !B2.isEmpty()) {
                        P.remove(B);
                        P.add(B1);
                        P.add(B2);

                        if (L.contains(B)) {
                            L.remove(B);
                            L.add(B1);
                            L.add(B2);
                        } else {
                            L.add(B1.size() < B2.size() ? B1 : B2);
                        }
                    } else {
                        newPartitions.add(B);
                    }
                }
                P.addAll(newPartitions);
            }
        }
        return buildMinimizedDFA(P);
    }

    private AFD buildMinimizedDFA(Set<Set<String>> partitions) {
        HashMap<String, List<String>> newTransitions = new HashMap<>();
        List<String> newStates = new ArrayList<>();
        String newInitialState = null;
        List<String> newAcceptanceStates = new ArrayList<>();
        Map<String, String> stateMapping = new HashMap<>();

        // Crear nombres únicos para los nuevos estados
        int counter = 0;
        for (Set<String> partition : partitions) {
            String newStateName = "q" + counter++;
            newStates.add(newStateName);
            for (String oldState : partition) {
                stateMapping.put(oldState, newStateName);
            }
            if (partition.contains(initial_state)) {
                newInitialState = newStateName;
            }
            if (!Collections.disjoint(partition, acceptance_states)) {
                newAcceptanceStates.add(newStateName);
            }
        }

        // Generar las nuevas transiciones
        for (Set<String> partition : partitions) {
            String representative = partition.iterator().next(); // Tomamos un estado de referencia
            String newStateName = stateMapping.get(representative);
            List<String> newStateTransitions = new ArrayList<>();

            for (int i = 0; i < alphabet.size(); i++) {
                String target = transitions_table.get(representative).get(i);
                newStateTransitions.add(stateMapping.get(target));
            }

            newTransitions.put(newStateName, newStateTransitions);
        }

        return new AFD(newTransitions, newStates, alphabet, newInitialState, newAcceptanceStates);
    }

    // Additional utility methods for displaying and testing the minimized DFA
    public void printDFA() {
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Initial State: " + initial_state);
        System.out.println("Acceptance States: " + acceptance_states);
        System.out.println("Transitions Table:");

        for (String state : states) {
            System.out.print(state + ": ");
            List<String> transitions = transitions_table.get(state);
            for (int i = 0; i < alphabet.size(); i++) {
                System.out.print("" + state + "," + alphabet.get(i) + "=" + transitions.get(i) + " ");
            }
            System.out.println();
        }
    }

    // Example usage
    public static void main(String[] args) {
        // Example DFA construction
        HashMap<String, List<String>> transitions = new HashMap<>();
        transitions.put("A", Arrays.asList("B", "C"));
        transitions.put("B", Arrays.asList("B", "D"));
        transitions.put("C", Arrays.asList("B", "C"));
        transitions.put("D", Arrays.asList("B", "E"));
        transitions.put("E", Arrays.asList("B", "C"));

        List<String> states = Arrays.asList("A", "B", "C", "D", "E");
        List<String> alphabet = Arrays.asList("a", "b");
        String initialState = "A";
        List<String> acceptanceStates = Arrays.asList("E");

        AFDMinimizador dfa = new AFDMinimizador(new AFD(transitions, states, alphabet, initialState, acceptanceStates));
        System.out.println("Original DFA:");
        dfa.printDFA();

        AFD minimizedDFA = dfa.minimize();
        System.out.println("\nMinimized DFA:");
        minimizedDFA.printAFD();
    }
}
