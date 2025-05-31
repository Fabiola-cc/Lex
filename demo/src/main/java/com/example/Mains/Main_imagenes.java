package com.example.Mains;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.example.Drawings.Draw_Tree;
import com.example.Drawings.GraphVizAFD;
import com.example.JavaFileGenerator;
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
import com.example.models.node;

public class Main_imagenes {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        System.out.println("\nStep 1: Obtener la información del documento...");
        LexerConfigParser parser = new LexerConfigParser();
        Map<String, Object> result = parser.parseLexerConfig("demo/src/main/resources/lexer.yal");
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
        // Create image of tree
        Draw_Tree drawer = new Draw_Tree();
        drawer.visualizeTree(treeNodes);
        System.out.println("Puedes ver tu árbol como 'Syntax_Tree.png' en la carpeta de resultados en Drawings");

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

        System.out.println("\n Generando imagen de AFD...");
        GraphVizAFD drawerAfd = new GraphVizAFD(model,
                "demo\\src\\main\\java\\com\\example\\Drawings\\Results\\AFDimage.png");
        drawerAfd.draw();
        System.out.println("Puedes ver tu AFD como 'AFDimage.png' en la carpeta de resultados en Drawings");

        System.out.println("\n Generando imagen de AFD minimizado...");
        GraphVizAFD drawerAfdmini = new GraphVizAFD(model,
                "demo\\src\\main\\java\\com\\example\\Drawings\\Results\\MiniAFDimage.png");
        drawerAfdmini.draw();
        System.out.println("Puedes ver tu AFD como 'MiniAFDimage.png' en la carpeta de resultados en Drawings");

        System.out.println("\nStep 5: Crear Analizador léxico");
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("demo/src/main/resources/AFD.dat"))) {
            out.writeObject(miniAFD);
            System.out.println("AFD guardado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al guardar el AFD: " + e.getMessage());
        }

        JavaFileGenerator.generateFile(headers);
    }
}