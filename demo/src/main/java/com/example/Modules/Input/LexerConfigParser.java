package com.example.Modules.Input;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LexerConfigParser {
    private final Map<String, String> regexDefinitions = new LinkedHashMap<>();
    private final Map<String, String> tokenDefinitions = new LinkedHashMap<>();
    private final Map<String, String> regexToTokenMap = new LinkedHashMap<>();

    private boolean parsingTokens = false;

    public void readFile(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new FileNotFoundException("No se encontró el archivo: " + filename);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line.trim()); // Procesar cada línea
            }
        }

        replaceReferences();
        escapeSingleCharValues();
        mapRegexToTokens();
    }

    private void parseLine(String line) {
        if (line.isEmpty()) return; // Ignorar líneas vacías

        if (line.startsWith("rule tokens:")) {
            parsingTokens = true; // Cambiamos al modo de lectura de tokens
            return;
        }

        if (!parsingTokens) {
            // Modo de lectura de regex
            Pattern pattern = Pattern.compile("let\\s+(\\w+)\\s*=\\s*(.+)");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String name = matcher.group(1);
                String value = matcher.group(2);
                regexDefinitions.put(name, value);
            }
        } else {
            // Modo de lectura de tokens
            Pattern tokenPattern = Pattern.compile("(\\w+)\\s*=\\s*\\{return\\s+(\\w+)\\}");
            Matcher matcher = tokenPattern.matcher(line);
            if (matcher.matches()) {
                String name = matcher.group(1);
                String token = matcher.group(2);
                tokenDefinitions.put(name, token);
            }
        }
    }

    private void replaceReferences() {
        for (Map.Entry<String, String> entry : regexDefinitions.entrySet()) {
            String value = entry.getValue();
            for (Map.Entry<String, String> replacement : regexDefinitions.entrySet()) {
                value = value.replaceAll("\\b" + replacement.getKey() + "\\b", replacement.getValue());
            }
            regexDefinitions.put(entry.getKey(), value);
        }
    }

    private void escapeSingleCharValues() {
        for (Map.Entry<String, String> entry : regexDefinitions.entrySet()) {
            String value = entry.getValue();
            if (value.length() == 1) {
                regexDefinitions.put(entry.getKey(), "\\" + value);
            }
        }
    }

    private void mapRegexToTokens() {
        for (Map.Entry<String, String> tokenEntry : tokenDefinitions.entrySet()) {
            String regexKey = tokenEntry.getKey();
            String tokenValue = tokenEntry.getValue();

            if (regexDefinitions.containsKey(regexKey)) {
                String regexValue = regexDefinitions.get(regexKey);
                regexToTokenMap.put(regexValue, tokenValue);
            } else {
                System.err.println("Error: El token '" + regexKey + "' no tiene una definición de regex.");
            }
        }
    }

    public void printDefinitions() {
        System.out.println("Definiciones de Regex:");
        for (Map.Entry<String, String> entry : regexDefinitions.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
        System.out.println("\nDefiniciones de Tokens:");
        for (Map.Entry<String, String> entry : tokenDefinitions.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
        System.out.println("\nMapeo de Regex a Tokens:");
        for (Map.Entry<String, String> entry : regexToTokenMap.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
    }

    public static void main(String[] args) {
        LexerConfigParser parser = new LexerConfigParser();
        try {
            parser.readFile("lexer.rules"); // Leer el archivo
            parser.printDefinitions(); // Imprimir las definiciones
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}

