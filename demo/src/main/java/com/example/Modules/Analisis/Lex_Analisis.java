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
    private String documento;
    private List<Token> tokens_final;
    private List<String> characters = new ArrayList<>();
    
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
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    characters.add(String.valueOf(line.charAt(i)));
                }
            }
        }
        
        comprobar_en_afd(characters);

        return characters;
    }
    
    public void comprobar_en_afd(List<String> lista_de_caracteres) {
        List<Integer> posiciones_de_tokens = new ArrayList<>();
        List<String> afd_tokens = afd.getAlphabet();
        
        for (String token : afd.getTokens()) {
            int el_indek_del_token = afd_tokens.indexOf(token);
            posiciones_de_tokens.add(el_indek_del_token);
        }
        
        for (String elem : lista_de_caracteres) {
            int lugar_donde_esta_en_Alfabeto = afd.getAlphabet().indexOf(elem.toString());
            String estado_donde_estamos = afd.getInitial_state();            
            List<String> transiciones = afd.getTransitions_table().get(estado_donde_estamos);
            
            if (transiciones.get(lugar_donde_esta_en_Alfabeto) != null) {
                String nuevo_estado = transiciones.get(lugar_donde_esta_en_Alfabeto);
        
                if (nuevo_estado != null && !nuevo_estado.isEmpty()) {
                    estado_donde_estamos = nuevo_estado;
                    transiciones = afd.getTransitions_table().get(estado_donde_estamos);
                    
                    for (int token : posiciones_de_tokens) {
                        String nuevo_estado_2 = transiciones.get(token);   
                        
                        for (String aceptace : afd.getAcceptance_states()) {
                            if (nuevo_estado_2 == aceptace) {
                                tokens_final.add(new Token(elem, afd_tokens.get(token)));
                            }
                        }                        
                    }
                }
            }
        }
    }

    public void print_tokens() {
        System.out.println("\n ----------------------------TOKENS----------------------------");
        for (Token tok : tokens_final) {
            System.out.println(tok);
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

    public static void main(String[] args) throws IOException {
        LexerConfigParser parser = new LexerConfigParser();
        Map<String, Object> result = parser.parseLexerConfig("lexer.yal");
        Map<String, String> regexToTokenMap = (Map<String, String>) result.get("regexToTokenMap");
        Map<String, String> processedRegexMap = convertRegexMap(regexToTokenMap);
        List<RegexToken> combined = generateCombinedRegex(processedRegexMap);
        List<RegexToken> infix = addImplicitConcatenation(combined);
        List<RegexToken> postfix = shuntingYard(infix);
        Calculate_tree treeCalculator = new Calculate_tree();
        List<node> treeNodes = treeCalculator.convertPostfixToTree(postfix);
        Direct_AFD generator = new Direct_AFD();
        AFD model = generator.generate_directAfd(treeNodes);
        Lex_Analisis lexer_analisis = new Lex_Analisis(model, "C:\\Users\\villa\\Desktop\\Clases_S7\\4.Diseño de Lenguajes de Programación\\2.Proyecto2_YALex\\Lex\\demo\\src\\main\\resources\\ejemplo.txt");
        lexer_analisis.print_tokens();
    }
}