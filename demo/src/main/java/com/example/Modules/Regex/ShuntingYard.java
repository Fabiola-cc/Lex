package com.example.Modules.Regex;

import java.util.*;

import com.example.models.RegexToken;

public class ShuntingYard {
    private static final Set<String> OPERATORS = Set.of("|", "*", "‧");

    private static final Map<String, Integer> PRECEDENCE = Map.of(
            "|", 1, // Unión
            "‧", 2, // Concatenación
            "*", 3 // Cierre de Kleene
    );

    /**
     * Convierte una expresión regular en una lista de tokens (componentes básicos
     * de la expresión).
     * Este método procesa la expresión regular caracter por caracter y genera una
     * lista de tokens.
     * 
     * @param regex Expresión regular que se convertirá en una lista de tokens.
     * @return Lista de tokens que representan los componentes de la expresión
     *         regular.
     */
    public static List<RegexToken> convertToArray(String regex) {
        List<RegexToken> tokens = new ArrayList<>();

        for (int i = 0; i < regex.length(); i++) {
            String c = String.valueOf(regex.charAt(i));
            boolean isOperator = OPERATORS.contains(c);
            RegexToken token = new RegexToken(c, isOperator);

            if (c.equals("[")) {
                String content = extractBracketContent(regex, i);
                if (i + 5 < regex.length() && regex.charAt(i + 5) == '#') {
                    String range2 = extractBracketContent(regex, i + 6);
                    calculateRangeDifference(content, range2, tokens);
                    i += 10;
                } else {
                    expandRegex(content, tokens);
                    i += content.length() + 1;
                }
            } else if (c.equals("\\")) {
                String literal = String.valueOf(regex.charAt(i + 1));
                tokens.add(new RegexToken(literal, false));
                i++;
            } else if (c.equals("^")) {
                String next = String.valueOf(regex.charAt(i + 1));
                if (next.equals("[")) {
                    String content = extractBracketContent(regex, i + 1);
                    expandRegexNegation(content, tokens);
                    i = i + content.length() + 2;
                } else {
                    tokens.add(new RegexToken("^" + next, false));
                    i++;
                }
            } else if (c.equals("?")) {
                RegexToken prevToken = tokens.get(tokens.size() - 1);
                if (prevToken.getValue().equals(")")) {
                    int parenIndex = tokens.size() -1 ;
                    while (!tokens.get(parenIndex).getValue().equals("(")) {
                        parenIndex--;
                    }
                    tokens.add(parenIndex, new RegexToken("(", true));
                    tokens.add(new RegexToken("|", true));
                    tokens.add(new RegexToken("\0", false));
                    tokens.add(new RegexToken(")", true));
                }
                else {
                    tokens.remove(tokens.size() - 1);
                    tokens.add(new RegexToken("(", true));
                    tokens.add(new RegexToken(String.valueOf(regex.charAt(i - 1)), false));
                    tokens.add(new RegexToken("|", true));
                    tokens.add(new RegexToken("\0", false));
                    tokens.add(new RegexToken(")", true));
                }
            } else if (c.equals("+")) {
                if (!tokens.isEmpty()) {
                    RegexToken prevToken = tokens.get(tokens.size() - 1);

                    // Si el token previo es un paréntesis de cierre, copiar todo lo dentro de los
                    // paréntesis
                    if (prevToken.getValue().equals(")")) {
                        // Buscar el paréntesis abierto correspondiente
                        int openIndex = findMatchingOpenParen(tokens);
                        if (openIndex != -1) {
                            List<RegexToken> insideParens = tokens.subList(openIndex, tokens.size());

                            // Copiar los tokens dentro del paréntesis y agregar *
                            tokens.addAll(new ArrayList<>(insideParens));
                            tokens.add(new RegexToken("*", true));
                        }
                    } else {
                        // Caso simple: a+ → aa*
                        tokens.add(prevToken);
                        tokens.add(new RegexToken("*", true));
                    }
                }
            } else {
                if (c.equals("(") || c.equals(")")) {
                    tokens.add(new RegexToken(c, true));
                } else {
                    tokens.add(token);
                }

            }
        }

        return addImplicitConcatenation(tokens);
    }

    /**
     * Añade la concatenación implícita entre los tokens cuando sea necesario.
     * Este método revisa si existen patrones donde es necesario insertar un
     * operador de concatenación.
     * 
     * @param tokens Lista de tokens que representan la expresión regular en
     *               notación infija.
     * @return Lista de tokens modificada, con concatenación implícita añadida
     *         cuando corresponde.
     */
    private static List<RegexToken> addImplicitConcatenation(List<RegexToken> tokens) {
        List<RegexToken> output = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            RegexToken current = tokens.get(i);
            output.add(current);

            if (i < tokens.size() - 1) {
                RegexToken next = tokens.get(i + 1);

                boolean isCurrentOperator = current.getIsOperator();
                boolean isNextOperator = next.getIsOperator();

                boolean isCurrentCloseParen = current.getValue().equals(")");
                boolean isNextOpenParen = next.getValue().equals("(");
                boolean isNextPipeOrCloseParen = next.getValue().equals("|") || next.getValue().equals(")");

                // Concatenación implícita: Si un operador no es seguido por otro operador,
                // agregamos la concatenación
                if (!isCurrentOperator && !isNextOperator && !isCurrentCloseParen && !isNextPipeOrCloseParen) {
                    output.add(new RegexToken("‧", true)); // Concatenación implícita
                }

                // Si un operador * o + es seguido de un paréntesis de apertura o un no
                // operador, también agregamos la concatenación
                if ((isCurrentOperator && (current.getValue().equals("*") || current.getValue().equals("+")))
                        && (isNextOpenParen || !isNextOperator)) {
                    output.add(new RegexToken("‧", true)); // Agregar concatenación
                }

                // Si hay un paréntesis de cierre seguido de un token que no es operador ni
                // pipe, agregamos la concatenación
                if (isCurrentCloseParen && !isNextOperator) {
                    output.add(new RegexToken("‧", true)); // Agregar concatenación
                }

                if ((isCurrentCloseParen && isNextOpenParen) || (!isCurrentOperator && isNextOpenParen)) {
                    output.add(new RegexToken("‧", true));
                }

            }
        }

        return output;
    }

    /**
     * Encuentra el índice del paréntesis de apertura correspondiente a un
     * paréntesis de cierre en la expresión regular.
     * Recorre los tokens en busca del paréntesis de apertura correspondiente a un
     * paréntesis de cierre dado.
     * 
     * @param tokens Lista de tokens que representan la expresión regular en
     *               notación infija.
     * @return El índice del paréntesis de apertura correspondiente.
     */
    private static int findMatchingOpenParen(List<RegexToken> tokens) {
        int balance = 0;
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getValue().equals(")")) {
                balance++;
            } else if (tokens.get(i).getValue().equals("(")) {
                balance--;
                if (balance == 0) {
                    return i; // Encontramos el `(`
                }
            }
        }
        return -1; // No se encontró un `(`
    }

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

    /**
     * Expande una referencia de expresión regular dentro de corchetes (por ejemplo,
     * [a-z], [0-9]).
     * Este método genera todos los caracteres que corresponden a un rango de
     * caracteres dentro de corchetes.
     * 
     * @param regexReference El rango de caracteres que se va a expandir (por
     *                       ejemplo, "a-z").
     * @param output         Lista donde se agregarán los tokens resultantes de la
     *                       expansión del rango.
     */
    public static void expandRegex(String regexReference, List<RegexToken> output) {
        int i = 0;

        // Abrimos un solo grupo para todo el conjunto de caracteres
        output.add(new RegexToken("(", true));

        while (i < regexReference.length()) {
            char c = regexReference.charAt(i);

            if (i + 1 < regexReference.length() && regexReference.charAt(i + 1) == '-') {
                if (i + 2 >= regexReference.length()) {
                    throw new IllegalArgumentException("Rango incompleto en: " + regexReference);
                }

                char start = regexReference.charAt(i);
                char end = regexReference.charAt(i + 2);

                if (start > end) {
                    throw new IllegalArgumentException("Rango inválido: " + start + "-" + end);
                }

                for (char ch = start; ch <= end; ch++) {
                    output.add(new RegexToken(String.valueOf(ch), false));

                    if (ch < end) { // Solo agrega "|" si no es el último carácter
                        output.add(new RegexToken("|", true));
                    }
                }

                i += 3; // Saltamos el rango completo (start - end)
                output.add(new RegexToken("|", true));
                continue;
            } else {
                output.add(new RegexToken(String.valueOf(c), false));
                i++;
            }

            if (i < regexReference.length()) {
                output.add(new RegexToken("|", true));
            }
        }

        // Cerramos el grupo
        output.remove(output.size() - 1);
        output.add(new RegexToken(")", true));
    }

    /**
     * Expande una referencia de expresión regular con negación dentro de corchetes
     * (por ejemplo, [^a-z]).
     * Este método genera todos los caracteres que no están en el rango de
     * caracteres dentro de corchetes.
     * 
     * @param regexReference El rango de caracteres negado que se va a expandir (por
     *                       ejemplo, "[^a-z]").
     * @param output         Lista donde se agregarán los tokens resultantes de la
     *                       expansión de la negación.
     */
    public static void expandRegexNegation(String regexReference, List<RegexToken> output) {
        int i = 0;
        while (i < regexReference.length()) {
            String c = String.valueOf(regexReference.charAt(i));

            if (i + 2 < regexReference.length() && regexReference.charAt(i + 1) == '-') {
                String start = String.valueOf(regexReference.charAt(i));
                String end = String.valueOf(regexReference.charAt(i + 2));

                if (start.compareTo(end) > 0) {
                    throw new IllegalArgumentException("Rango inválido: " + regexReference);
                }

                output.add(new RegexToken("(", true));

                for (char ch = start.charAt(0); ch <= end.charAt(0); ch++) {
                    output.add(new RegexToken("^" + String.valueOf(ch), false));

                    if (ch < end.charAt(0)) {
                        output.add(new RegexToken("|", true));
                    }
                }

                output.add(new RegexToken(")", true));

                i += 3;
            } else {
                output.add(new RegexToken(c, false));
                i++;
            }
        }
    }

    /**
     * Extrae el contenido dentro de corchetes en una expresión regular.
     * Este método extrae el texto que está dentro de los corchetes en la expresión
     * regular.
     * 
     * @param regex      Expresión regular que contiene los corchetes.
     * @param startIndex Índice donde comienza la búsqueda de corchetes.
     * @return El contenido dentro de los corchetes como una cadena.
     */
    private static String extractBracketContent(String regex, int startIndex) {
        StringBuilder brackets = new StringBuilder();
        int i = startIndex;

        while (i + 1 < regex.length() && regex.charAt(i + 1) != ']') {
            i++;
            brackets.append(regex.charAt(i));
        }

        if (i + 1 < regex.length() && regex.charAt(i + 1) == ']') {
            return brackets.toString();
        } else {
            throw new IllegalArgumentException("Error: Falta el cierre ']' en la expresión regular.");
        }
    }

    /**
     * Calcula la diferencia entre dos rangos de caracteres y genera una nueva
     * expresión regular con el resultado.
     * Este método obtiene la diferencia entre dos rangos de caracteres, generando
     * un nuevo conjunto de caracteres.
     * 
     * @param range1 Primer rango de caracteres.
     * @param range2 Segundo rango de caracteres con el que se calculará la
     *               diferencia.
     * @param output Lista donde se agregarán los tokens resultantes de la
     *               diferencia de los rangos.
     */
    public static void calculateRangeDifference(String range1, String range2, List<RegexToken> output) {
        // Convertimos los rangos en listas de caracteres
        char start1 = range1.charAt(0); // Inicia el primer rango
        char end1 = range1.charAt(2); // Termina el primer rango
        char start2 = range2.charAt(0); // Inicia el segundo rango
        char end2 = range2.charAt(2); // Termina el segundo rango

        output.add(new RegexToken("(", true));
        // Generamos los caracteres del primer rango
        Set<Character> range1Set = new HashSet<>();
        for (char c = start1; c <= end1; c++) {
            range1Set.add(c);
        }

        // Generamos los caracteres del segundo rango
        Set<Character> range2Set = new HashSet<>();
        for (char c = start2; c <= end2; c++) {
            range2Set.add(c);
        }

        // Calculamos la diferencia entre los dos rangos
        range1Set.removeAll(range2Set);

        // Agregamos los caracteres restantes al output
        boolean first = true;
        for (char c : range1Set) {
            if (!first) {
                output.add(new RegexToken("|", true)); // Concatenamos con |
            }
            output.add(new RegexToken(String.valueOf(c), false)); // Agregamos el carácter al output
            first = false;
        }
        output.add(new RegexToken(")", true));
    }

    public static void main(String[] args) {
        System.out.println("Headless mode: " + java.awt.GraphicsEnvironment.isHeadless());
        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario que ingrese una expresión regular
        System.out.print("Ingresa una expresión regular: ");
        String regex = scanner.nextLine(); // Captura la línea completa de la entrada

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

        scanner.close();
    }

}
