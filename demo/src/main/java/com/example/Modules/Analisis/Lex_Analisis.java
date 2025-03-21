package com.example.Modules.Analisis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.example.models.AFD;

public class Lex_Analisis {
    private AFD afd;
    private List<String> tokens_final;
    private String documento;
    
    // Lista para almacenar los caracteres leídos
    private List<String> characters = new ArrayList<>();
    
    /**
     * Constructor que inicializa el analizador léxico con un AFD y procesa un documento
     * 
     * @param afd_para_analisis El autómata finito determinista a utilizar
     * @param documento Ruta al archivo que se va a analizar
     */
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
    
    /**
     * Lee un archivo de texto y guarda cada carácter individualmente
     * El AFD proporcionado se utilizará para analizar estos caracteres
     * 
     * @param filename Nombre del archivo a leer
     * @return Lista de caracteres leídos
     * @throws IOException Si ocurre un error de I/O
     */
    private List<String> processFile(String filename) throws IOException {
        characters.clear(); // Limpiar caracteres previos
        tokens_final.clear(); // Limpiar tokens previos
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            
            // Leer línea por línea
            while ((line = reader.readLine()) != null) {
                // Procesar cada carácter de la línea
                for (int i = 0; i < line.length(); i++) {
                    characters.add(String.valueOf(line.charAt(i)));
                }
            }
        }
        
        // Aquí se podría implementar el análisis utilizando el AFD
        // ...
        
        return characters;
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
    

   
    // Método main para pruebas
    public static void main(String[] args) {
         // Crear un AFD como el del ejemplo
        List<String> alphabet = Arrays.asList("", "EOL", "+", "PLUS", "-", "MINUS", "*", "TIMES", "/", "DIV", 
                                             "(", "LPAREN", ")", "RPAREN", " ", "\t", "lexbuf", 
                                             "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "NUMBER");
        
        List<String> states = Arrays.asList("S7", "S1", "S10", "S8", "S3", "S2", "S4", "S9", "S5", "S0", "S6");
        
        String initial_state = "S0";
        
        List<String> acceptance_states = Arrays.asList("S2");
        
        HashMap<String, List<String>> transitions = new HashMap<>();
        // Inicializar todas las transiciones como se muestra en el ejemplo
        // Este es solo un ejemplo simplificado, necesitarías llenar todas las transiciones del ejemplo
        List<String> transS0 = new ArrayList<>(Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        transS0.set(0, "S1");
        transS0.set(2, "S3");
        transS0.set(4, "S4");
        transS0.set(6, "S5");
        transS0.set(8, "S6");
        transS0.set(10, "S7");
        transS0.set(12, "S8");
        transS0.set(14, "S9");
        transS0.set(15, "S9");
        transS0.set(17, "S10");
        transS0.set(18, "S10");
        transS0.set(19, "S10");
        transS0.set(20, "S10");
        transS0.set(21, "S10");
        transS0.set(22, "S10");
        transS0.set(23, "S10");
        transS0.set(24, "S10");
        transS0.set(25, "S10");
        transS0.set(26, "S10");
        transitions.put("S0", transS0);
        
        // Crear más transiciones para otros estados...
        // Para simplificar, solo muestro algunas

        
        AFD afd = new AFD(transitions, states, alphabet, new ArrayList<>(), initial_state, acceptance_states);
      
        afd.printAFD();
        // Crear el analizador léxico con el AFD y el documento
        Lex_Analisis lexer_analisis = new Lex_Analisis(afd, "C:\\Users\\villa\\Desktop\\Clases_S7\\4.Diseño de Lenguajes de Programación\\2.Proyecto2_YALex\\Lex\\demo\\src\\main\\resources\\ejemplo.txt");
        
        // Imprimir los caracteres leídos
        lexer_analisis.printCharacters();
    }
}