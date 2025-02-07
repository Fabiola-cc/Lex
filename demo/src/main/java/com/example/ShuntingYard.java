package com.example;
import com.example.models.RegexToken;

import javax.sound.midi.Soundbank;
import java.util.*;

public class ShuntingYard {
    private static final Set<Character> OPERATORS = Set.of('|', '*', '+', '‧');

    private static final Map<Character, Integer> PRECEDENCE = Map.of(
            '|', 1,  // Unión
            '.', 2,  // Concatenación
            '*', 3,  // Cierre de Kleene
            '+', 3 // Una o más repeticiones
    );

    public static List<RegexToken> convertToArray(String regex) {
        List<RegexToken> output = new ArrayList<>();

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            boolean isOperator = OPERATORS.contains(c);
            RegexToken token = new RegexToken(c, isOperator);

            if (c == '[') {
                StringBuilder brakets = new StringBuilder();
                while (i + 1 < regex.length() && regex.charAt(i + 1) != ']') {
                    i++;
                    brakets.append(regex.charAt(i));
                }

                if (i + 1 < regex.length() && regex.charAt(i + 1) == ']') {
                    i++;
                } else {
                    throw new IllegalArgumentException("Error: Falta el cierre ']' en la expresión regular.");
                }
                expandRegex(brakets.toString(), output);
            }
            else if (c == '\\'){
                char literal = regex.charAt(i + 1);
                output.add(new RegexToken(literal, false));
                i++;
            }
            else if (c == '?'){
                output.remove(output.size() - 1);
                output.add(new RegexToken('(', true));
                output.add(new RegexToken(regex.charAt(i - 1), false));
                output.add(new RegexToken('|', true));
                output.add(new RegexToken('\0', false));
                output.add(new RegexToken(')', true));
            }
            else if (!isOperator) {
                if (!output.isEmpty() && !output.get(output.size() - 1).getIsOperator()) {
                    output.add(new RegexToken('‧', true)); // Agregamos el operador de concatenación
                }
                output.add(token);
            }
            else if (isOperator) {
                output.add(token);
            }
        }
        return output;
    }

    public static List<RegexToken> shuntingYard(List<RegexToken> infix) {
        List<RegexToken> output = new ArrayList<>();
        Stack<RegexToken> operatorStack = new Stack<>();

        for (int i = 0; i < infix.size(); i++) {
            RegexToken token = infix.get(i);
            char c = token.getValue();

            if (!token.getIsOperator()) {
                // Si el token es un operando (carácter normal), lo añadimos al resultado
                output.add(token);
            } else if (c == '(') {
                // Si el token es un paréntesis izquierdo, lo añadimos a la pila
                operatorStack.push(token);
            } else if (c == ')') {
                // Si el token es un paréntesis derecho, sacamos de la pila hasta encontrar '('
                while (!operatorStack.isEmpty() && operatorStack.peek().getValue() != '(') {
                    output.add(operatorStack.pop());
                }
                operatorStack.pop(); // Descartar el paréntesis izquierdo '('
            } else if (c == '‧') {  // Si es el operador de concatenación
                // Aquí no necesitamos un control especial para concatenación,
                // porque es implícito entre dos operandos.
                while (!operatorStack.isEmpty() &&
                        PRECEDENCE.getOrDefault(operatorStack.peek().getValue(), 0) >= PRECEDENCE.getOrDefault(c, 0)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                // Si es otro operador, procesamos en base a su precedencia
                while (!operatorStack.isEmpty() &&
                        PRECEDENCE.getOrDefault(operatorStack.peek().getValue(), 0) >= PRECEDENCE.getOrDefault(c, 0)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        // Finalmente, vaciamos la pila
        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return output;
    }


    public static void expandRegex(String regexReference, List<RegexToken> output){
        int i = 0;
        while (i < regexReference.length()) {
            char c = regexReference.charAt(i);

            // Detectar un rango (ej. "a-z")
            if (i + 2 < regexReference.length() && regexReference.charAt(i + 1) == '-') {
                char start = c;
                char end = regexReference.charAt(i + 2);

                if (start > end) {
                    throw new IllegalArgumentException("Rango inválido: " + regexReference);
                }

                // Agregar el operador '(' antes del grupo
                output.add(new RegexToken('(', true));

                // Expandir el rango en la lista
                for (char ch = start; ch <= end; ch++) {
                    output.add(new RegexToken(ch, false)); // Es un carácter normal

                    // Agregar '|', excepto en el último carácter
                    if (ch < end) {
                        output.add(new RegexToken('|', true)); // Es un operador
                    }
                }

                // Agregar el operador ')' al final del grupo
                output.add(new RegexToken(')', true));

                i += 3; // Saltamos el rango completo
            } else {
                // Agregar caracteres individuales
                output.add(new RegexToken(c, false));
                i++;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario que ingrese una expresión regular
        System.out.print("Ingresa una expresión regular: ");
        String regex = scanner.nextLine();  // Captura la línea completa de la entrada

        // Llamamos a la función convertToArray con el input del usuario
        List<RegexToken> infix = convertToArray(regex);
        List<RegexToken> postfix = shuntingYard(infix);

        for (RegexToken token : infix) {
            System.out.print(token.getValue());
        }

        System.out.println(" ");

        for (RegexToken token : postfix) {
            System.out.print(token.getValue());
        }
    }
}
