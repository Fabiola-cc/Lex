package com.example.Modules.Input;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LexerConfigParser {
    private final Map<String, String> regexDefinitions = new LinkedHashMap<>();
    private final Map<String, String> tokenDefinitions = new LinkedHashMap<>();
    private final Map<String, String> regexToTokenMap = new LinkedHashMap<>();
    private final List<String> headers = new ArrayList<>(); // Lista para almacenar los encabezados

    private boolean parsingHeaders = false;
    private boolean parsingTokens = false;
    private boolean firstTokenRule = true;

    // Funcion a Exportar a Main
    public Map<String, Object> parseLexerConfig(String filename) throws IOException {
        readFile(filename);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("headers", new ArrayList<>(headers));
        result.put("regexToTokenMap", new LinkedHashMap<>(regexToTokenMap));
        return result;
    }

    public void readFile(String filename) throws IOException {
        InputStream inputStream = new FileInputStream(filename);

        // InputStream inputStream =
        // getClass().getClassLoader().getResourceAsStream(filename);
        try {
            // if (inputStream == null) {
            // throw new FileNotFoundException("No se encontró el archivo: " + filename);
            // }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    parseLine(line.trim()); // Procesar cada línea
                }
            }

            replaceReferences();
            mapRegexToTokens();
        } catch (FileNotFoundException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void parseLine(String line) {
        if (line.isEmpty())
            return; // Ignorar líneas vacías

        if (line.startsWith("rule gettoken =")) {
            parsingTokens = true; // Cambiamos al modo de lectura de tokens
            return;
        }

        if (line.startsWith("{")) {
            parsingHeaders = true;
            return;
        }

        if (line.startsWith("}")) {
            parsingHeaders = false;
            return;
        }

        // Detectar y guardar encabezados
        if (parsingHeaders) {
            headers.add(line);
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
            if (firstTokenRule) {
                // Primer bloque de reglas de tokens, puede ser con regex o variable
                Pattern tokenPattern = Pattern.compile("\\s*(\\S+)\\s*\\{\\s*return\\s+(\\w+)\\s*\\}");
                match(line, tokenPattern);
                firstTokenRule = false;
            }

            // Modo de lectura de tokens
            Pattern tokenPattern = Pattern.compile("\\|\\s*(\\S+)\\s*\\{\\s*return\\s+(\\w+)\\s*\\}");
            match(line, tokenPattern);
        }
    }

    private void match(String line, Pattern tokenPattern) {
        Matcher matcher = tokenPattern.matcher(line);
        if (matcher.matches()) {
            String name = matcher.group(1);
            String token = matcher.group(2);

            if (regexDefinitions.containsKey(name)) {
                tokenDefinitions.put(name, token);
            } else {
                regexToTokenMap.put(name, token);
            }
        }
    }

    // Reemplazar las referencias de regex en las expresiones regulares
    private void replaceReferences() {
        // Iteramos sobre las definiciones de regex
        for (Map.Entry<String, String> entry : regexDefinitions.entrySet()) {
            String value = entry.getValue();

            // Sustituir las referencias a otras expresiones regulares por su valor
            // correspondiente
            for (Map.Entry<String, String> replacement : regexDefinitions.entrySet()) {
                // Utilizamos una expresión regular para hacer el reemplazo de las referencias
                value = value.replaceAll("\\b" + replacement.getKey() + "\\b", replacement.getValue());
            }

            // Actualizamos la definición de la regex con el valor ya resuelto
            regexDefinitions.put(entry.getKey(), value);
        }
    }

    // Mapear las expresiones regulares a los tokens correspondientes
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

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        LexerConfigParser parser = new LexerConfigParser();

        try {
            Map<String, Object> result = parser.parseLexerConfig("lexer.yal");

            // Imprimir los encabezados
            List<String> headers = (List<String>) result.get("headers");
            System.out.println("Encabezados:");
            for (String header : headers) {
                System.out.println(header);
            }

            // Obtener el mapeo de regex a tokens
            Map<String, String> regexToTokenMap = (Map<String, String>) result.get("regexToTokenMap");
            System.out.println("\nMapeo de Regex a Tokens:");
            for (Map.Entry<String, String> entry : regexToTokenMap.entrySet()) {
                System.out.println(entry.getKey() + " , " + entry.getValue());
            }

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

}
