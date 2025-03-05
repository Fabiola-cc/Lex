package com.example;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.example.models.AFD;
import com.example.models.RegexToken;
import com.example.models.node;

public class Generate_AFD {
    AFD afd;
    AFD miniAfd;

    public Generate_AFD() {

    }

    @Test
    public void testAFDGenerationAndAcceptance() {
        // Configuración
        Calculate_tree calculator = new Calculate_tree();
        List<RegexToken> postfixExample = new ArrayList<>();

        // Expresión (a|b)* ‧ a ‧ b
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));

        List<node> result = calculator.convertPostfixToTree(postfixExample);
        Direct_AFD generator = new Direct_AFD();
        AFD afd = generator.generate_directAfd(result);

        // Verificación de AFD generado
        assertNotNull("El AFD no debería ser nulo", afd);

        // Prueba de aceptación de cadena
        String inputString = "baabb#";
        ArrayList<ArrayList<String>> derivationProcess = afd.derivation(afd.getInitial_state(), inputString);
        Boolean resultString = afd.accepted(
                derivationProcess.get(derivationProcess.size() - 1).get(0),
                afd.getAcceptance_states());

        assertTrue("La cadena debería ser aceptada por el AFD", resultString);

        String input2 = "baabbaa#";
        ArrayList<ArrayList<String>> derivation2 = afd.derivation(afd.getInitial_state(), input2);
        Boolean result2 = afd.accepted(
                derivation2.get(derivation2.size() - 1).get(0),
                afd.getAcceptance_states());

        assertFalse("La cadena NO debería ser aceptada, tiene más caracteres", result2);

        String input3 = "baab#";
        ArrayList<ArrayList<String>> derivation3 = afd.derivation(afd.getInitial_state(), input3);
        Boolean result3 = afd.accepted(
                derivation3.get(derivation3.size() - 1).get(0),
                afd.getAcceptance_states());

        assertFalse("La cadena NO debería ser aceptada, tiene menos caracteres", result3);
    }

    @Test
    public void testAFDMinimization() {
        // Configuración
        Calculate_tree calculator = new Calculate_tree();
        List<RegexToken> postfixExample = new ArrayList<>();

        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("|", true));
        postfixExample.add(new RegexToken("*", true));
        postfixExample.add(new RegexToken("a", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));
        postfixExample.add(new RegexToken("b", false));
        postfixExample.add(new RegexToken("‧", true));

        List<node> result = calculator.convertPostfixToTree(postfixExample);
        Direct_AFD generator = new Direct_AFD();
        AFD afd = generator.generate_directAfd(result);

        // Minimización
        AFDMinimizador dfa = new AFDMinimizador(afd);
        AFD miniAfd = dfa.minimize();
        assertNotNull("El AFD minimizado no debería ser nulo", miniAfd);
    }
}
