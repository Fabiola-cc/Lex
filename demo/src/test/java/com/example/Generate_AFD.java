package com.example;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
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
        AFD miniAfd = afd.minimize();
        assertNotNull("El AFD minimizado no debería ser nulo", miniAfd);
    }
}
