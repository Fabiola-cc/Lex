package com.example;

import java.util.List;
import java.util.Scanner;

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
        
        // Step 4: Create Direct AFD from syntax tree
        
        // Generate the AFD
        
        // Print AFD information
       
        
        // Step 5: Minimize the AFD (optional)
  }
}