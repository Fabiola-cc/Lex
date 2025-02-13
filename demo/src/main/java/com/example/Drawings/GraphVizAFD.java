package com.example.Drawings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.models.AFD;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
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
        
        // Crear mapa de nodos
        Map<String, MutableNode> nodes = new HashMap<>();
        
        // Crear todos los nodos
        for (String state : automaton.getStates()) {
            MutableNode node = mutNode(state);
            
            // Estilo para estados de aceptación
            if (automaton.getAcceptance_states().contains(state)) {
                node.add(Style.BOLD);
            }
            
            // Estilo para estado inicial
            if (state.equals(automaton.getInitial_state())) {
                node.add(Color.rgb("87CEEB").fill()); // Azul claro
            } else {
                node.add(Color.YELLOW.fill());
            }
            
            nodes.put(state, node);
            graph.add(node);
        }
        
        // Agregar transiciones
        HashMap<String, List<String>> transitions = automaton.getTransitions_table();
        List<Character> alphabet = automaton.getAlphabet();
        
        for (String fromState : transitions.keySet()) {
            List<String> toStates = transitions.get(fromState);
            for (int i = 0; i < toStates.size(); i++) {
                String toState = toStates.get(i);
                if (toState != null && !toState.isEmpty()) {
                    String symbol = String.valueOf(alphabet.get(i));
                    nodes.get(fromState).addLink(
                        to(nodes.get(toState))
                        .with("label", symbol)
                    );
                }
            }
        }

        // Configurar y guardar el grafo
        Graphviz.fromGraph(graph)
            .width(800)
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