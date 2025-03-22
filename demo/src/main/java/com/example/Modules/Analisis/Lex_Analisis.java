package com.example.Modules.Analisis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.Modules.Error.Lex_errors;
import com.example.models.AFD;
import com.example.models.Token;

public class Lex_Analisis {
    private AFD afd;
    private String documento;
    private List<Token> tokens_final;
    private List<String> characters = new ArrayList<>();
    private Lex_errors found_error;

    public Lex_Analisis(AFD afd_para_analisis, String documento) {
        this.afd = afd_para_analisis;
        this.documento = documento;
        this.tokens_final = new ArrayList<>();

        try {
            processFile(documento);
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
        }
    }

    public List<String> processFile(String filename) throws IOException {
        characters.clear();
        tokens_final.clear();

        File file = new File(filename);
        try {
            if (!file.exists()) {
                throw new FileNotFoundException("No se encontró el archivo: " + filename);
            }
            InputStream inputStream = new FileInputStream(file);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Agregar un carácter de nueva línea antes de cada línea excepto la primera
                    if (!firstLine) {
                        characters.add("\n");
                    } else {
                        firstLine = false;
                    }

                    // Agregar caracteres de la línea
                    for (int i = 0; i < line.length(); i++) {
                        characters.add(String.valueOf(line.charAt(i)));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Hay un problema con el archivo a procesar: " + e.getMessage());
        }

        comprobar_en_afd(characters);
        return characters;
    }

    public void comprobar_en_afd(List<String> lista_de_caracteres) {
        List<Integer> posiciones_de_tokens = new ArrayList<>();
        List<String> afd_tokens = afd.getAlphabet();
        int line = 1;

        if (afd_tokens == null | afd.getTokens() == null | afd.getInitial_state() == null) {
            found_error = new Lex_errors(
                    "Error interno, el analizador léxico no tiene la información de los símbolos a usar",
                    line, 0);
            return;
        }

        // Preparar posiciones de tokens
        for (String token : afd.getTokens()) {
            int el_indek_del_token = afd_tokens.indexOf(token);
            posiciones_de_tokens.add(el_indek_del_token);
        }

        int i = 0;
        while (i < lista_de_caracteres.size()) {
            // Valores para el token actual
            String estado_actual = afd.getInitial_state();
            StringBuilder lexema = new StringBuilder();
            String tipo_token = null;
            int longitud_maxima = 0;

            // Manejar saltos de línea explícitamente
            if (lista_de_caracteres.get(i).equals("\n")) {
                // Simplemente saltamos el carácter de nueva línea
                i++;
                line++;
                continue;
            }

            // Intentar construir el token más largo posible
            int j = i;
            while (j < lista_de_caracteres.size()) {
                String caracter = lista_de_caracteres.get(j);

                // Detener el análisis si encontramos un carácter de nueva línea
                if (caracter.equals("\n")) {
                    break;
                }

                int indice_caracter = afd.getAlphabet().indexOf(caracter);

                // Si el carácter no está en el alfabeto, terminamos
                if (indice_caracter == -1) {
                    found_error = new Lex_errors(
                            "No es un caracter válido",
                            line, i);
                    break;
                }

                List<String> transiciones = afd.getTransitions_table().get(estado_actual);
                if (transiciones == null)
                    break;

                String nuevo_estado = transiciones.get(indice_caracter);

                // Si no hay transición, terminamos
                if (nuevo_estado == null || nuevo_estado.isEmpty())
                    break;

                // Avanzamos al nuevo estado
                estado_actual = nuevo_estado;
                lexema.append(caracter);
                j++;

                // Verificar si estamos en un estado de aceptación
                for (int k = 0; k < posiciones_de_tokens.size(); k++) {
                    int posicion_token = posiciones_de_tokens.get(k);
                    List<String> trans = afd.getTransitions_table().get(estado_actual);
                    if (trans != null && posicion_token < trans.size()) {
                        String estado_siguiente = trans.get(posicion_token);

                        if (afd.getAcceptance_states().contains(estado_siguiente)) {
                            tipo_token = afd_tokens.get(posicion_token);
                            longitud_maxima = j - i;
                        }
                    }
                }
            }

            // Si encontramos un token válido, lo agregamos
            if (longitud_maxima > 0) {
                tokens_final.add(new Token(lexema.substring(0, longitud_maxima), tipo_token));
                i += longitud_maxima;
            } else {
                found_error = new Lex_errors("No se encontró un token", line, (i + 1));
                break;
            }
        }
    }

    public void print_tokens() {
        System.out.println("\n ----------------------------TOKENS----------------------------");
        for (Token tok : tokens_final) {
            System.out.println(tok);
        }
        if (found_error != null) {
            System.err.println("\n" + found_error.toString());
        }
    }

    /**
     * Obtiene la lista de caracteres leídos
     * 
     * @return Lista de caracteres
     */
    public List<String> getCharacters() {
        return characters;
    }

    /**
     * Obtiene el AFD utilizado por este analizador
     * 
     * @return El AFD utilizado
     */
    public AFD getAFD() {
        return afd;
    }

    /**
     * Imprime los caracteres leídos para debug
     */
    public void printCharacters() {
        System.out.println("Caracteres leídos del documento '" + documento + "':");
        for (String c : characters) {
            System.out.print(c + ", ");
        }
        System.out.println();
    }
}