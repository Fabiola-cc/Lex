package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.models.RegexToken;
import com.example.models.node;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

public class Draw_Tree {
    // Estos son los objetos que se irán modificando para luego crear el árbol
    private final MutableGraph graph;
    private final AtomicInteger nodeCounter;
    // private static final String OUTPUT_FILENAME = "Syntax_Tree.png";
    private static String OUTPUT_FILENAME = "Syntax_Tree" + ".png";

    // Constructor inicializa el grafo y el contador de nodos
    public Draw_Tree() {
        this.graph = mutGraph("syntax_tree").setDirected(true);
        this.nodeCounter = new AtomicInteger(0);
    }

    /**
     * Usa como parámetro la lista de nodos del árbol sintáctico
     * Método principal que genera la visualización del árbol
     * @param treeNodes Lista de nodos que forman el árbol sintáctico
     */
    public void visualizeTree(List<node> treeNodes) {
        try {
            if (treeNodes == null || treeNodes.isEmpty()) {
                throw new IllegalArgumentException("La lista de nodos no puede ser nula o vacía");
            }

            // Obtiene el nodo raíz (último nodo en la lista) y comienza la creación
            node root = treeNodes.get(treeNodes.size() - 1);
            createNodes(root, null, treeNodes);

            // Configura y guarda el grafo
            // De todos los nodos que se van agregando se guardan en graph para que
            // con la dependencia se haga el archivo
            Graphviz.fromGraph(graph)
                   .render(Format.PNG)
                   .toFile(new File(OUTPUT_FILENAME));
            
            System.out.println("Tu imagen del árbol se ha generado como '" + OUTPUT_FILENAME + "'");
        } catch (Exception e) {
            System.err.println("Error al generar el árbol sintáctico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea una estructura de nodos recursivamente
     * Si el nodo tiene hijos, se procesan recursivamente
     * Cada nodo se conecta con su padre si existe
     */
    private MutableNode createNodes(node currentNode, MutableNode parentGraphNode, List<node> allNodes) {
        // Genera ID único para el nodo
        String nodeId = "node" + nodeCounter.getAndIncrement();
        
        // Crea el nodo con el símbolo y decoración
        MutableNode graphNode = createSymbolNode(currentNode, nodeId);
        
        // Añade el nodo al grafo
        graph.add(graphNode);
        
        // Si tiene padre, conecta con él
        if (parentGraphNode != null) {
            parentGraphNode.addLink(graphNode);
        }
        
        // Procesa recursivamente los hijos
        for (Integer childIndex : currentNode.getNodes()) {
            if (childIndex >= 0 && childIndex < allNodes.size()) {
                node childNode = allNodes.get(childIndex);
                createNodes(childNode, graphNode, allNodes);
            }
        }
        return graphNode;
    }

    /*DECORACIONES DE LA IMAGEN*/
    private MutableNode createSymbolNode(node node, String id) {
        // Muestra solo el valor del nodo
        String label = String.valueOf(node.getValue());
        
        // Configuración base del estilo del nodo
        MutableNode nodeStyle = mutNode(id)
            .add("shape", "circle")
            .add("fixedsize", "false")
            .add("width", "0.9")
            .add("height", "0.9")
            .add("fontsize", "50")
            .add("style", "filled,rounded")
            .add("penwidth", "2.0")
            .add("label", label);
            
        // Diferente color para nodos terminales y no terminales
        if (node.isAlphanumeric()) {
            return nodeStyle.add(Color.rgb("FFB6C1").fill()); // Rosa claro para nodos hoja
        } else {
            return nodeStyle.add(Color.rgb("87CEEB").fill()); // Azul claro para operadores
        }
    }

    /**
     * Método principal para pruebas de visualización
     * Crea un árbol de ejemplo y genera su visualización
     */
    public static void main(String[] args) {
        // Crear un árbol de ejemplo para pruebas
        Calculate_tree calculator = new Calculate_tree();
        List<RegexToken> postfixExample = new ArrayList<>();
        
             
        // Primera parte: (a|b)*
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        
        // Segunda parte: (c|d)
        postfixExample.add(new RegexToken("c", false));
        postfixExample.add(new RegexToken("d", false));
        postfixExample.add(new RegexToken("|", true));
        
        // Concatenar primera y segunda parte
        postfixExample.add(new RegexToken("‧", true));
        
        // Tercera parte: (e|f)*
        postfixExample.add(new RegexToken("e", false));
        postfixExample.add(new RegexToken("f", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        
        // Cuarta parte: (g|h)*
        postfixExample.add(new RegexToken("g", false));
        postfixExample.add(new RegexToken("h", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        
        // Concatenar tercera y cuarta parte
        postfixExample.add(new RegexToken("‧", true));
        
        // Quinta parte: (i|j|k)
        postfixExample.add(new RegexToken("i", false));
        postfixExample.add(new RegexToken("j", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("k", false));
        postfixExample.add(new RegexToken("|", true));
        
        // Concatenar todas las partes
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("‧", true));
        
        List<node> result = calculator.convertPostfixToTree(postfixExample);
        
        // Visualizar el árbol
        Draw_Tree drawer = new Draw_Tree();
        drawer.visualizeTree(result);
    }
}