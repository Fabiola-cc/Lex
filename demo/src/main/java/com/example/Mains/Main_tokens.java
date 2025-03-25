package com.example.Mains;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.example.Modules.AFD.Calculate_tree;
import com.example.Modules.AFD.Direct_AFD;
import com.example.Modules.Analisis.Lex_Analisis;
import com.example.Modules.Input.LexerConfigParser;
import static com.example.Modules.Regex.RegexConvertor.convertRegexMap;
import static com.example.Modules.Regex.RegexGenerator.addImplicitConcatenation;
import static com.example.Modules.Regex.RegexGenerator.generateCombinedRegex;
import static com.example.Modules.Regex.ShuntingYard.shuntingYard;
import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

public class Main_tokens{
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
        Lex_Analisis lexer_analisis = new Lex_Analisis(model, "ejemplo.txt");
        lexer_analisis.print_tokens();
        // lexer_analisis.printCharacters();
    }
}