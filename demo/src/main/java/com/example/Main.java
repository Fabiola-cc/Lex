package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.Drawings.Draw_AFD;
import com.example.Drawings.Draw_Tree;
import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Get the regular expression from user
        System.out.print("Enter a regular expression: ");
        String regex = scanner.nextLine();

        // Step 2: Convert regex to tokens and then to postfix notation
        System.out.println("\nStep 1: Converting to postfix notation...");
        List<RegexToken> infixTokens = ShuntingYard.convertToArray(regex);
        List<RegexToken> postfixTokens = ShuntingYard.shuntingYard(infixTokens);

        // Print the postfix expression for verification
        System.out.println("Postfix expression:");
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

        // Create image of tree
        Draw_Tree drawer = new Draw_Tree();
        drawer.visualizeTree(treeNodes);

        // Step 4: Create Direct AFD from syntax tree
        Direct_AFD generator = new Direct_AFD();

        // Generate the AFD
        AFD model = generator.generate_directAfd(treeNodes);

        // Create image of AFD
        Draw_AFD drawerAfd = new Draw_AFD(model);
        drawerAfd.displayAutomaton();

        // Step 5: Minimize the AFD (optional)
        AFD miniAfd = model.minimize();

        // Create image of minimized AFD
        Draw_AFD drawerMini = new Draw_AFD(miniAfd);
        drawerMini.displayAutomaton();

        // Step 6: Checks if a string is valid in AFD
        System.out.print("Enter a string to check with the AFD: ");
        String inputString = scanner.nextLine();

        ArrayList<ArrayList<String>> derivationProcess = miniAfd.derivation(miniAfd.getInitial_state(), inputString);
        Boolean result = miniAfd.accepted(derivationProcess.get(derivationProcess.size() - 1).get(0), inputString,
                miniAfd.getAcceptance_states());
        if (result) {
            System.out.println("La cadena es aceptada");
        } else {
            System.out.println("La cadena no es aceptada");
        }

    }
}