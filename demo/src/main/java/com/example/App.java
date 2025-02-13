package com.example;

import java.util.List;
import java.util.Scanner;

import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario que ingrese una expresión regular
        System.out.print("Ingresa una expresión regular: ");
        String regex = scanner.nextLine(); // Captura la línea completa de la entrada

        // Llamamos a la función convertToArray con el input del usuario
        List<RegexToken> infix = ShuntingYard.convertToArray(regex);
        List<RegexToken> postfix = ShuntingYard.shuntingYard(infix);

        postfix.forEach(token -> System.out.print(token.getValue() + " "));
        System.out.println('\n');

        Calculate_tree calculator = new Calculate_tree();
        List<node> result = calculator.convertPostfixToTree(postfix);

        Direct_AFD generator = new Direct_AFD(result);
        AFD model = generator.generate_directAfd(result);

        for (String s : model.getTransitions_table().keySet()) {
            System.err.println(s + "\t" + model.getTransitions_table().get(s));
        }
    }
}
