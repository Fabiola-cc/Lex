package com.example;

// Estos imports son parte de la lógica de compilación
// No deben quitarse para el buen funcionamiento
import com.example.Modules.Analisis.Lex_Analisis;
import com.example.models.AFD;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class Yalex {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Uso: java Yalex <archivo_de_entrada>");
            return;
        }
        String inputFile = args[0];
        AFD actualModel;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("demo/src/main/resources/AFD.dat"))) {
            actualModel = (AFD) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el AFD: " + e.getMessage());
            return;
        }
        Lex_Analisis analizador = new Lex_Analisis(actualModel, inputFile);
        analizador.print_tokens();
    }
}