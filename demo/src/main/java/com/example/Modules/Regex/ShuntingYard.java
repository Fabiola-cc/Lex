package com.example.Modules.Regex;

import java.util.*;

import com.example.models.RegexToken;

public class ShuntingYard {
    // private static final Set<String> OPERATORS = Set.of("|", "*", "‧");

    private static final Map<String, Integer> PRECEDENCE = Map.of(
            "|", 1, // Unión
            "‧", 2, // Concatenación
            "*", 3 // Cierre de Kleene
    );

    /**
     * Convierte una lista de tokens en notación infija a notación postfix
     * utilizando el algoritmo de Shunting Yard.
     * Este método aplica el algoritmo de Shunting Yard para cambiar la expresión
     * regular de notación infija a postfix.
     * 
     * @param infix Lista de tokens que representan la expresión regular en notación
     *              infija.
     * @return Lista de tokens en notación postfix.
     */
    public static List<RegexToken> shuntingYard(List<RegexToken> infix) {
        List<RegexToken> output = new ArrayList<>();
        Stack<RegexToken> operatorStack = new Stack<>();

        for (RegexToken token : infix) {
            String c = token.getValue();

            if (!token.getIsOperator()) {
                output.add(token); // Agregar operandos directamente al resultado
            } else if (c.equals("(")) {
                operatorStack.push(token);
            } else if (c.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals("(")) {
                    output.add(operatorStack.pop());
                }
                operatorStack.pop(); // Eliminar el paréntesis izquierdo
            } else {
                while (!operatorStack.isEmpty() &&
                        PRECEDENCE.getOrDefault(operatorStack.peek().getValue(), 0) >= PRECEDENCE.getOrDefault(c, 0)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return output;
    }

}
