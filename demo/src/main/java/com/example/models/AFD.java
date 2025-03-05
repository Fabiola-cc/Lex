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
    }
}
