package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.Drawings.Draw_Tree;
import com.example.Drawings.GraphVizAFD;
import com.example.Modules.AFD.AFDMinimizador;
import com.example.Modules.AFD.Calculate_tree;
import com.example.Modules.AFD.Direct_AFD;
import com.example.Modules.Regex.ShuntingYard;
import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bienvenido al Compilador de Expresiones Regulares");
        System.out.println("----------------------------------------");
        System.out.println("Este programa convierte expresiones regulares a Autómatas Finitos Deterministas");
        System.out.println("Caracteres especiales:");
        System.out.println("  * - Estrella de Kleene (cero o más)");
        System.out.println("  | - Unión (alternancia)");
        System.out.println("  + - Uno o más");
        System.out.println("  ? - Cero o uno");
        System.out.println("  [] - Clase de caracteres (ej., [a-z])");
        System.out.println("  [^] - Clase de caracteres negada");
        System.out.println("  () - Agrupación");
        System.out.println("----------------------------------------");

        // Step 1: Get the regular expression from user
        System.out.print("Enter a regular expression: ");
        String regex = scanner.nextLine();

        // Step 2: Convert regex to tokens and then to postfix notation
        System.out.println("\nStep 1: Converting to postfix notation...");
        List<RegexToken> infixTokens = ShuntingYard.convertToArray(regex);
        List<RegexToken> postfixTokens = ShuntingYard.shuntingYard(infixTokens);

        // Print the postfix expression for verification
        System.out.println("Postfix expression");
        for (RegexToken token : postfixTokens) {
            System.out.print(token.getValue() + " ");
        }
        System.out.println("\n");

        // Step 3: Create syntax tree from postfix expression
        System.out.println("Step 2: Creating syntax tree...");
        Calculate_tree treeCalculator = new Calculate_tree();
        List<node> treeNodes = treeCalculator.convertPostfixToTree(postfixTokens);

        // Print tree nodes for verification
        System.out.println("Tree nodes:");
        for (int i = 0; i < treeNodes.size(); i++) {
            node n = treeNodes.get(i);
            System.out.println("Node " + i + ": " +
                    "Name=" + n.getName() + ", " +
                    "Value=" + n.getValue() + ", " +
                    "Children=" + n.getNodes());
        }
        System.out.println();
        System.out.println("\n Generando imagen de árbol...");
        // Create image of tree
        Draw_Tree drawer = new Draw_Tree();
        drawer.visualizeTree(treeNodes);
        System.out.println("Puedes ver tu árbol como 'Syntax_Tree.png'");

        // Step 4: Create Direct AFD from syntax tree
        System.out.println("\nStep 3: Generating AFD");
        Direct_AFD generator = new Direct_AFD();

        // Generate the AFD
        AFD model = generator.generate_directAfd(treeNodes);
        System.out.println("AFD results:");
        model.printAFD();

        // Step 5: Minimize the AFD (optional)
        System.out.println("\nStep 4: Try to Minimize AFD");
        AFDMinimizador minimizer = new AFDMinimizador(model);
        AFD miniAFD = minimizer.minimize();
        System.out.println("\nMinimized DFA:");
        System.out.println("AFD Minimization results:");
        miniAFD.printAFD();

        // Step 6: Checks if a string is valid in AFD
        System.out.println("\nStep 5: Check string");
        System.out.print("Enter a string to check with the AFD: ");
        String inputString = scanner.nextLine() + "#";

        ArrayList<ArrayList<String>> derivationProcessO = miniAFD.derivation(miniAFD.getInitial_state(), inputString);
        Boolean resultO = miniAFD.accepted(derivationProcessO.get(derivationProcessO.size() - 1).get(2),
                miniAFD.getAcceptance_states());
        if (resultO) {
            System.out.println("\nLa cadena es aceptada");
        } else {
            System.out.println("\nLa cadena no es aceptada");
        }

        System.out.println("\n Generando imagen de AFD...");
        GraphVizAFD drawerAfd = new GraphVizAFD(model, "AFDimage.png");
        drawerAfd.draw();
        System.out.println("Puedes ver tu AFD como 'AFDimage.png'");

        System.out.println("\n Generando imagen de AFD minimizado...");
        GraphVizAFD drawerAfdmini = new GraphVizAFD(miniAFD, "MiniAFDimage.png");
        drawerAfdmini.draw();
        System.out.println("Puedes ver tu AFD como 'MiniAFDimage.png'");

        scanner.close();
    }
}