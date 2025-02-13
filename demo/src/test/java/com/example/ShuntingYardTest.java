package com.example;

import static org.junit.Assert.assertTrue;

import com.example.models.RegexToken;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;


/**
 * Unit test for simple App.
 */
public class ShuntingYardTest {

    // Test para el método convertToArray
    @Test
    public void testConvertToArray() {
        String regex = "a(b|c)*";
        List<RegexToken> expected = Arrays.asList(
                new RegexToken("a", false), new RegexToken("‧", true), new RegexToken("(", true),
                new RegexToken("b", false), new RegexToken("|", true), new RegexToken("c", false),
                new RegexToken(")", true), new RegexToken("*", true)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testShuntingYard() {

        List<RegexToken> infix = Arrays.asList(
                new RegexToken("a", false), new RegexToken("‧", true), new RegexToken("(", true),
                new RegexToken("b", false), new RegexToken("|", true), new RegexToken("c", false),
                new RegexToken(")", true), new RegexToken("*", true)
        );

        List<RegexToken> expected = Arrays.asList(
                new RegexToken("a", false), new RegexToken("b", false),
                new RegexToken("c", false), new RegexToken("|", true),
                new RegexToken("*", true),new RegexToken("‧", true)
        );

        List<RegexToken> result = ShuntingYard.shuntingYard(infix);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testExpand() {
        String regex = "[a-d]*";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("|", true),
                new RegexToken("b", false), new RegexToken("|", true),
                new RegexToken("c", false), new RegexToken("|", true),
                new RegexToken("d", false),
                new RegexToken(")", true),
                new RegexToken("*", true)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testKleineLock() {
        String regex = "(ab)+";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("‧", true),
                new RegexToken("b", false), new RegexToken(")", true),
                new RegexToken("‧", true),   new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("‧", true),
                new RegexToken("b", false), new RegexToken(")", true),
                new RegexToken("*", true)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testOptionalQuantifier() {
        String regex = "a?";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("|", true),
                new RegexToken("\0", false),  new RegexToken(")", true)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testDifference() {
        String regex = "[a-z]#[c-z]";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("|", true),
                new RegexToken("b", false),  new RegexToken(")", true)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testImplicitConcatenation() {
        String regex = "ab(ab)*(ab)(a)a";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("a", false), new RegexToken("‧", true),
                new RegexToken("b", false),  new RegexToken("‧", true),
                new RegexToken("(", true), new RegexToken("a", false),
                new RegexToken("‧", true),  new RegexToken("b", false),
                new RegexToken(")", true), new RegexToken("*", true),
                new RegexToken("‧", true), new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken("‧", true),
                new RegexToken("b", false), new RegexToken(")", true),
                new RegexToken("‧", true),new RegexToken("(", true),
                new RegexToken("a", false), new RegexToken(")", true),
                new RegexToken("‧", true),  new RegexToken("a", false)
                );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

    @Test
    public void testLiteral() {
        String regex = "\\*";
        List<RegexToken> expected;
        expected = Arrays.asList(
                new RegexToken("*", false)
        );

        List<RegexToken> result = ShuntingYard.convertToArray(regex);

        assertEquals(expected.size(), result.size(), "El tamaño de la lista resultante no coincide con el esperado.");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), result.get(i).getValue(),
                    "Error en el token " + i + ": esperado '" + expected.get(i).getValue() + "', pero se obtuvo '" + result.get(i).getValue() + "'");

            assertEquals(expected.get(i).getIsOperator(), result.get(i).getIsOperator(),
                    "Error en el token " + i + ": esperado getIsOperator()=" + expected.get(i).getIsOperator() +
                            ", pero se obtuvo " + result.get(i).getIsOperator());
        }
    }

}
