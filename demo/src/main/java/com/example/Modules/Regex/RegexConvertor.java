package com.example.Modules.Regex;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RegexConvertor {
    private static final Set<String> OPERATORS = Set.of("|", "*", "+", "(", ")", "[", "]", "#", "", "_", "?");

    public static Map<String, String> convertRegexMap(Map<String, String> regexToTokenMap) {
        Map<String, String> processedMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : regexToTokenMap.entrySet()) {
            String processedRegex = processRegex(entry.getKey());
            processedMap.put(processedRegex, entry.getValue());
        }

        return processedMap;
    }

    private static String processRegex(String regex) {
        StringBuilder result = new StringBuilder();
        boolean insideQuotes = false;
        StringBuilder literalBuffer = new StringBuilder();
        int quoteCount = 0; 

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);

            if (c == '\'') {
                quoteCount += 1; 
                
                if (quoteCount == 2) {
                    result.append("\\'");
                    quoteCount = 0; 
                    i = i + 1;
                    insideQuotes = !insideQuotes;
                    continue;
                }

                if (insideQuotes) {
                    String literal = literalBuffer.toString();
                    literalBuffer.setLength(0);

                    if (literal.startsWith("\\")) {
                        result.append("'").append(literal).append("'");
                    } else if (OPERATORS.contains(literal)) {
                        result.append("\\").append(literal);
                    } else {
                        result.append(literal);
                    }

                    quoteCount = 0; 
                }
                insideQuotes = !insideQuotes;
            } else {
                if (insideQuotes) {
                    literalBuffer.append(c);
                    quoteCount = 0; 
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Map<String, String> regexToTokenMap = new LinkedHashMap<>();
        regexToTokenMap.put("'\"'(['A'-'Z''a'-'z']+|['0'-'9']|' '|'?'|'!'|'.')+'\"'", "PLUS");
        regexToTokenMap.put("['a'-'z''A'-'Z']#['a'-'b']'['", "INDENT");
        regexToTokenMap.put("' '|'\\t'", "WHITESPACE");
        regexToTokenMap.put("'H'", "IDENTIFIER");

        Map<String, String> processedRegexMap = convertRegexMap(regexToTokenMap);
        System.out.println("Mapa procesado: " + processedRegexMap);
    }
}
