package com.example.Modules.Analisis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.Modules.AFD.Calculate_tree;
import com.example.Modules.AFD.Direct_AFD;
import com.example.Modules.Input.LexerConfigParser;
import static com.example.Modules.Regex.RegexConvertor.convertRegexMap;
import static com.example.Modules.Regex.RegexGenerator.addImplicitConcatenation;
import static com.example.Modules.Regex.RegexGenerator.generateCombinedRegex;
import static com.example.Modules.Regex.ShuntingYard.shuntingYard;
import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.Token;
import com.example.models.node;

public class Lex_Analisis {
    private AFD afd;
    private List<String> tokens_final;
    private String documento;
    private List<Token> tokes_finales_totales = new ArrayList<>();
    
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
    public List<String> processFile(String filename) throws IOException {
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
        
        comprobar_en_afd(characters);

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

    public void comprobar_en_afd( List<String>  lista_de_caracteres ){
        //creamos una lista tokens 
        //Esta lista son los ints en donde se encuetran los tokens 
        List<Integer> posiciones_de_tokens = new ArrayList<>(); // Necesitas inicializar la lista
        List<String> afd_tokens = afd.getAlphabet();
        for (String token : afd.getTokens()) {
            int el_indek_del_token = afd_tokens.indexOf(token);
            posiciones_de_tokens.add(el_indek_del_token); // Faltaba el punto y coma
        }
        
        for (String elem : lista_de_caracteres) {
            // Tomamos elemento y buscamos su lugar en List<String> alphabet
            // Guardamos en un int en el lugar en donde está
            int lugar_donde_esta_en_Alfabeto = afd.getAlphabet().indexOf(elem.toString());
            // Buscar estado inicial en S0
            String estado_donde_estamos = afd.getInitial_state();            
            // Buscar estado inicial en Tabla HashMap<String, List<String>>
            List<String> transiciones = afd.getTransitions_table().get(estado_donde_estamos);
            // Si hay una transición válida en la posición del alfabeto
            if (transiciones.get(lugar_donde_esta_en_Alfabeto)!= null) {
                String nuevo_estado = transiciones.get(lugar_donde_esta_en_Alfabeto);
        
                // Si hay una transición (no es null)
                if (nuevo_estado != null && !nuevo_estado.isEmpty()) {
                    
                    estado_donde_estamos = nuevo_estado;
                    transiciones = afd.getTransitions_table().get(estado_donde_estamos);
                    for (int token : posiciones_de_tokens) {
                        String nuevo_estado_2 = transiciones.get(token);   
                        for (String aceptace : afd.getAcceptance_states()){
                            if (nuevo_estado_2 == aceptace){
                                tokes_finales_totales.add(new Token(elem, afd_tokens.get(token)));
                            }
                        }                        
                    }
                    
                }
            }
        }

    }

    public void print_tokens(){
        for (Token tok: tokes_finales_totales){
            System.out.println(tok);
        }
    }
    

   
    // Método main para pruebas
    public static void main(String[] args) throws IOException {

        LexerConfigParser parser = new LexerConfigParser();
        Map<String, Object> result = parser.parseLexerConfig("lexer.yal");
        List<String> headers = (List<String>) result.get("headers");
        Map<String, String> regexToTokenMap = (Map<String, String>) result.get("regexToTokenMap");

        Map<String, String> processedRegexMap = convertRegexMap(regexToTokenMap);
        List<RegexToken> combined = generateCombinedRegex(processedRegexMap);
        List<RegexToken> infix = addImplicitConcatenation(combined);

        List<RegexToken> postfix = shuntingYard(infix);

        System.out.println("Postfix expression");
        for (RegexToken token : postfix) {
            System.out.print(token.getValue() + " ");
        }
        System.out.println("\n");

        // Step 3: Create syntax tree from postfix expression
        System.out.println("Step 2: Creating syntax tree...");
        Calculate_tree treeCalculator = new Calculate_tree();
        List<node> treeNodes = treeCalculator.convertPostfixToTree(postfix);
        Direct_AFD generator = new Direct_AFD();

        AFD model = generator.generate_directAfd(treeNodes);
        System.out.println("AFD results:");
        model.printAFD();

        // Crear el analizador léxico con el AFD y el documento
        Lex_Analisis lexer_analisis = new Lex_Analisis(model, "C:\\Users\\villa\\Desktop\\Clases_S7\\4.Diseño de Lenguajes de Programación\\2.Proyecto2_YALex\\Lex\\demo\\src\\main\\resources\\ejemplo.txt");
        lexer_analisis.print_tokens();
    }
}