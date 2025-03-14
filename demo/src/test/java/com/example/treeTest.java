package com.example;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import com.example.Modules.AFD.Calculate_tree;
import com.example.models.RegexToken;
import com.example.models.node;

public class treeTest {
    public treeTest() {
    }

    @Test
    public void testConvertPostfixToTree() {
        // Configuración
        Calculate_tree calculator = new Calculate_tree();
        List<RegexToken> postfixExample = new ArrayList<>();

        // Ejemplo: (a|b)* ‧ a ‧ b
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

        // Ejecución
        List<node> result = calculator.convertPostfixToTree(postfixExample);

        // Verificación
        assertNotNull("El árbol de expresión no debería ser nulo", result);
        assertFalse("El árbol de expresión no debería estar vacío", result.isEmpty());
    }
}
