package com.example.Drawings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.models.AFD;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.model.Factory.to;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

public class GraphVizAFD {
    private final AFD automaton;
    private final String outputFile;

    public GraphVizAFD(AFD automaton, String outputFile) {
        this.automaton = automaton;
        this.outputFile = outputFile;
    }

    public void draw() throws IOException {
        MutableGraph graph = mutGraph("afd").setDirected(true);
        Map<String, MutableNode> nodes = new HashMap<>();
        
        // Crear nodos
        for (String state : automaton.getStates()) {
            MutableNode node = mutNode(state)
                .add("shape", "circle")
                .add("style", "filled");

            // Asignar color según el tipo de estado
            if (state.equals(automaton.getInitial_state())) {
                node.add("fillcolor", "#87CEEB");  // Estado inicial: azul claro
            } else if (automaton.getAcceptance_states().contains(state)) {
                node.add("fillcolor", "#98FB98");  // Estado final: verde claro
                node.add("peripheries", "2");      // Doble círculo para estados finales
            } else {
                node.add("fillcolor", "yellow");   // Estados normales: amarillo
            }
            
            nodes.put(state, node);
            graph.add(node);
        }

        // Agregar flecha inicial punteada
        MutableNode start = mutNode("Inicio")
            .add("shape", "none")
            .add("width", "0");
        graph.add(start);
        start.addLink(
            to(nodes.get(automaton.getInitial_state()))
            .with("style", "dashed")
        );
        
        // Agregar transiciones
        HashMap<String, List<String>> transitions = automaton.getTransitions_table();
        List<Character> alphabet = automaton.getAlphabet();
        
        for (String fromState : transitions.keySet()) {
            List<String> toStates = transitions.get(fromState);
            for (int i = 0; i < toStates.size(); i++) {
                String toState = toStates.get(i);
                if (toState != null && !toState.isEmpty()) {
                    nodes.get(fromState).addLink(
                        to(nodes.get(toState))
                        .with("label", alphabet.get(i).toString())
                    );
                }
            }
        }

        Graphviz.fromGraph(graph)
            .render(Format.PNG)
            .toFile(new File(outputFile));
    }

    public static void main(String[] args) throws IOException {
        // Ejemplo de uso
        HashMap<String, List<String>> transitions = new HashMap<>();
        transitions.put("q0", Arrays.asList("q1", "q0"));
        transitions.put("q1", Arrays.asList("q2", "q1"));
        transitions.put("q2", Arrays.asList("q0", "q1"));
        
        AFD afd = new AFD(
            transitions,
            Arrays.asList("q0", "q1", "q2"),
            Arrays.asList('a', 'b'),
            "q0",
            Arrays.asList("q0")
        );
        
        GraphVizAFD drawer = new GraphVizAFD(afd, "automaton.png");
        drawer.draw();
    }
}