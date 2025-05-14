package com.example.Modules.Analisis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.Modules.AFD.AFDMinimizador;
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

@SuppressWarnings("unchecked")
public class Complete_Lex {
    private static AFD toUseAFD;

    public static List<Token> completeLex(String inputFile) throws IOException {
        System.out.println("***********ANÁLISIS LÉXICO*************");
        System.out.println("\nStep 1: Obtener la información del documento...");
        LexerConfigParser parser = new LexerConfigParser();
        Map<String, Object> result = parser.parseLexerConfig("lexer.yal");

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
        System.out.println("Step 2: Crear el árbol sintáctico...");
        Calculate_tree treeCalculator = new Calculate_tree();
        List<node> treeNodes = treeCalculator.convertPostfixToTree(postfix);

        // Print tree nodes for verification
        System.out.println("Tree nodes:");
        for (int i = 0; i < treeNodes.size(); i++) {
            node n = treeNodes.get(i);
            System.out.println("Node " + i + ": " +
                    "Name=" + n.getName() + ", " +
                    "Value=" + n.getValue() + ", " +
                    "Children=" + n.getNodes());
        }
        System.out.println();
        System.out.println("\n Generando imagen de árbol...");

        // Step 4: Create Direct AFD from syntax tree
        System.out.println("\nStep 3: Generar el AFD");
        Direct_AFD generator = new Direct_AFD();

        // Generate the AFD
        System.out.println("\nStep 4: Intentar minimizar el AFD");
        AFD model = generator.generate_directAfd(treeNodes);
        System.out.println("Resultados AFD:");
        model.printAFD();

        AFDMinimizador minimizer = new AFDMinimizador(model);
        AFD miniAFD = minimizer.minimize();
        toUseAFD = miniAFD;

        System.out.println("\nStep 5: Crear Analizador léxico");
        Lex_Analisis analizador = new Lex_Analisis(miniAFD, inputFile);
        return analizador.getTokens_final();
    }

    public static List<Token> AnalizeFile(String inputFile) throws IOException {
        if (toUseAFD == null) {
            System.err.println("No se ha generado el analizador léxico");
            return null;
        }
        Lex_Analisis analizador = new Lex_Analisis(toUseAFD, inputFile);
        return analizador.getTokens_final();
    }
}
