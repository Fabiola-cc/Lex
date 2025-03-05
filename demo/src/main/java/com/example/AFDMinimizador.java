package com.example;

import java.util.*;
import com.example.models.AFD;

public class AFDMinimizador {
    private HashMap<String, List<String>> transitions_table;
    private List<String> states;
    private List<Character> alphabet;
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
        List<Set<String>> partition = new ArrayList<>();
        Set<String> acceptingStates = new HashSet<>(acceptance_states);
        Set<String> nonAcceptingStates = new HashSet<>(states);
        nonAcceptingStates.removeAll(acceptingStates);

        if (!acceptingStates.isEmpty())
            partition.add(acceptingStates);
        if (!nonAcceptingStates.isEmpty())
            partition.add(nonAcceptingStates);

        Queue<Set<String>> workList = new LinkedList<>(partition);
        while (!workList.isEmpty()) {
            Set<String> group = workList.poll();
            for (Character symbol : alphabet) {
                Map<Set<String>, Set<String>> splitMap = new HashMap<>();
                for (String state : group) {
                    String nextState = getTransition(state, symbol);
                    for (Set<String> part : partition) {
                        if (part.contains(nextState)) {
                            splitMap.computeIfAbsent(part, k -> new HashSet<>()).add(state);
                            break;
                        }
                    }
                }
                if (splitMap.size() > 1) {
                    partition.remove(group);
                    partition.addAll(splitMap.values());
                    workList.addAll(splitMap.values());
                    break;
                }
            }
        }

        return createMinimizedDFA(partition);
    }

    private String getTransition(String state, Character symbol) {
        List<String> transitions = transitions_table.get(state);
        if (transitions == null)
            return null;
        int index = alphabet.indexOf(symbol);
        return (index >= 0 && index < transitions.size()) ? transitions.get(index) : null;
    }

    private AFD createMinimizedDFA(List<Set<String>> finalPartition) {
        Map<String, String> stateMapping = new HashMap<>();
        List<String> newStates = new ArrayList<>();

        for (Set<String> group : finalPartition) {
            String newStateName = "s" + newStates.size();
            newStates.add(newStateName);
            for (String oldState : group)
                stateMapping.put(oldState, newStateName);
        }

        HashMap<String, List<String>> newTransitionsTable = new HashMap<>();
        for (String newState : newStates) {
            List<String> newTransitions = new ArrayList<>();
            String representative = stateMapping.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(newState))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            for (Character symbol : alphabet) {
                String oldNextState = getTransition(representative, symbol);
                newTransitions.add((oldNextState != null) ? stateMapping.get(oldNextState) : null);
            }
            newTransitionsTable.put(newState, newTransitions);
        }

        String newInitialState = stateMapping.get(initial_state);
        List<String> newAcceptanceStates = new ArrayList<>();
        for (String oldAcceptState : acceptance_states) {
            String newState = stateMapping.get(oldAcceptState);
            if (!newAcceptanceStates.contains(newState))
                newAcceptanceStates.add(newState);
        }

        System.out.println("\n Mapeo de estados, AFD minimizado:");
        System.out.println(stateMapping);

        return new AFD(newTransitionsTable, newStates, alphabet, newInitialState, newAcceptanceStates);
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
        List<Character> alphabet = Arrays.asList('a', 'b');
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
