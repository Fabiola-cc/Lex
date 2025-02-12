package com.example;

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

    private String transition(String q, char a, String[][] d) {
        String transited_state = "";
        for (String[] d1 : d) {
            if (d1[0].equals(q) && d1[1].equals(Character.toString(a))) {
                transited_state = d1[2];
            }
        }
        return transited_state;
    }

    private String final_state(String q, String w, String[][] d) {
        char value = w.charAt(w.length() - 1);

        return transition(String.valueOf(q), value, d);
    }

    public Boolean accepted(String q, String w, String[] F, String[][] d) {
        return Arrays.asList(F).contains(final_state(q, w, d));
    }

    public ArrayList<ArrayList<String>> derivation(String q, String w, String[][] d) {
        String state = q;
        ArrayList<ArrayList<String>> transitions = new ArrayList<>();

        for (int i = 0; i < w.length(); i++) {
            if (w.charAt(i) != ' ') {
                ArrayList<String> actualT = new ArrayList<>();
                actualT.add(state);
                actualT.add(String.valueOf(w.charAt(i)));

                state = transition(state, w.charAt(i), d);
                actualT.add(state);

                transitions.add(actualT);
            }

        }

        return transitions;
    }

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

    private Set<String> difference(Set<String> Q, Set<String> F) {
        Set<String> difference = new HashSet<>(Q);
        difference.removeAll(F);
        return difference;
    }

    public AFD minimize() {
        // Convert transitions into HashMap<String, HashMap<String, String>>
        HashMap<String, HashMap<String, String>> transitionMap = new HashMap<>();

        for (String state : transitions_table.keySet()) {
            for (int i = 0; i < alphabet.size(); i++) {
                String symbol = Character.toString(alphabet.get(i));
                String stateTo = transitions_table.get(state).get(i);

                transitionMap.putIfAbsent(symbol, new HashMap<>());
                transitionMap.get(symbol).put(state, stateTo);
            }
        }

        // Converts states and acceptance states into sets
        Set<String> F = new HashSet<>(acceptance_states);
        Set<String> Q = new HashSet<>(states);

        Set<String> P = new HashSet<>();
        P = difference(Q, F);

        Set<String[]> combinations = new HashSet<>();

        // Doing cartesian product
        for (String i : P) {
            for (String j : F) {
                String[] combination = { i, j };
                combinations.add(combination);
            }
        }

        Set<String[]> newCombinations = new HashSet<>();
        Set<String[]> oldCombinations = new HashSet<>(combinations);
        do {
            oldCombinations = new HashSet<>(combinations);
            for (String[] combination : combinations) {
                for (String alphabet : transitionMap.keySet()) {
                    HashMap<String, String> alphabetTransition = transitionMap.get(alphabet);

                    ArrayList<String> firstValues = new ArrayList<>();
                    ArrayList<String> secondValues = new ArrayList<>();

                    if (alphabetTransition.values().contains(combination[0])
                            && alphabetTransition.values().contains(combination[1])) {
                        for (String key : alphabetTransition.keySet()) {
                            if (alphabetTransition.get(key).equals(combination[0])
                                    && Arrays.asList(acceptance_states).contains(key) == false) {
                                firstValues.add(key);
                            }
                            if (alphabetTransition.get(key).equals(combination[1])
                                    && Arrays.asList(acceptance_states).contains(key) == false) {
                                secondValues.add(key);
                            }
                        }
                    }

                    if (firstValues.size() > 0 && secondValues.size() > 0) {
                        for (String i : firstValues) {
                            for (String j : secondValues) {
                                String[] newCombination = { i, j };

                                // Convertimos el array en una lista para comparar por contenido
                                List<String> newCombinationList = Arrays.asList(newCombination);

                                // Comparamos si esa lista ya está en las combinaciones existentes
                                boolean exists = combinations.stream()
                                        .map(Arrays::asList) // Convertimos cada combinación existente en una lista
                                        .anyMatch(
                                                existingCombination -> existingCombination.equals(newCombinationList));

                                if (!exists) {
                                    newCombinations.add(newCombination);
                                }
                            }
                        }
                    }

                }
            }

            // Add all new combinations to the original set
            combinations.addAll(newCombinations);

            Set<List<String>> combinationsList = new HashSet<>();
            Set<List<String>> oldCombinationsList = new HashSet<>();

            for (String[] arr : combinations) {
                combinationsList.add(Arrays.asList(arr));
            }
            for (String[] arr : oldCombinations) {
                oldCombinationsList.add(Arrays.asList(arr));
            }

            newCombinations.clear();
        } while (!differenceList(combinations, oldCombinations).isEmpty());

        Set<String[]> matrix_combinations = new HashSet<>();

        for (String q1 : states) {
            boolean same_state = true;

            for (String q2 : states) {

                if (same_state && q1.equals(q2)) {
                    same_state = false;
                } else if (!same_state && !q1.equals(q2)) {
                    String[] matrix_combination = { q1, q2 };
                    matrix_combinations.add(matrix_combination);
                }
            }
        }

        Set<List<String>> states_to_combine = differenceList(matrix_combinations, combinations);

        Set<String> combinedStates = new HashSet<>();

        for (List<String> q : states_to_combine) {
            String combined = String.join("", q);
            combinedStates.add(combined);
        }

        Set<String> combinedStates2 = new HashSet<>();
        int counter = combinedStates.size();
        while (combinedStates.size() > 1 && counter > 1) {
            Set<String> toRemove = new HashSet<>();
            Set<String> toAdd = new HashSet<>();

            for (String x : combinedStates) {
                String state = x;
                for (String y : combinedStates) {
                    if (!x.equals(y) && (x.contains(y.substring(0, 1)) || x.contains(y.substring(2, 3)))) {
                        state = state + y;
                        toRemove.add(y); // Marcar para eliminar después
                    }
                }
                toAdd.add(state); // Marcar para agregar después
                if (!state.equals(x)) {
                    break; // Salir si se modificó
                }
            }

            // Modificar el conjunto fuera del bucle de iteración
            combinedStates.removeAll(toRemove);
            combinedStates2.addAll(toAdd);

            counter--;
        }

        // Define new states
        ArrayList<String> newStates = new ArrayList<>();
        if (combinedStates2.size() > 0) {
            for (String state1 : combinedStates2) {
                for (String state2 : states) {
                    if (!state1.contains(state2)) {
                        newStates.add(state2);
                    }
                }
                newStates.add(state1);
            }
        } else {
            newStates = new ArrayList<>(states);
        }

        // Define new initial state
        String newInitialState = "";
        for (String x : newStates) {
            if (x.contains(initial_state)) {
                newInitialState = x;
                break;
            }
        }

        // Define new acceptances states
        ArrayList<String> newAcceptanceStates = new ArrayList<>();
        for (String x : newStates) {
            for (String y : acceptance_states) {
                if (x.contains(y)) {
                    newAcceptanceStates.add(x);
                    break;
                }
            }
        }

        // Define transition function
        HashMap<String, List<String>> newTransitions = new HashMap<>();
        for (String newState : newStates) {
            int totalAlphabet = alphabet.size();
            for (String oldState : transitions_table.keySet()) {
                if (newState.contains(oldState)) {
                    List<String> newTransition = new ArrayList<String>();
                    for (String realState : combinedStates2) {
                        for (int i = 0; i < alphabet.size(); i++) {
                            String stateTo = transitions_table.get(oldState).get(i);
                            if (realState.contains(stateTo)) {
                                stateTo = realState;
                            }
                            newTransition.add(stateTo);
                        }
                    }
                    newTransitions.put(newState, newTransition);
                    totalAlphabet--;
                    if (totalAlphabet < 1) {
                        break;
                    }
                }
            }
        }

        return new AFD(newTransitions, newStates, alphabet, newInitialState, newAcceptanceStates);
    }

}
