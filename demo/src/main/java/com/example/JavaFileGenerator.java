package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JavaFileGenerator {
    public static void generateFile(List<String> headers) {
        String className = "Yalex"; // Nombre de la clase generada
        String outputPath = className + ".java"; // Nombre del archivo

        // Código fuente en forma de String
        String javaCode = "";

        for (String string : headers) {
            javaCode += string + "\n";
        }

        javaCode += "import com.example.Modules.Analisis.Lex_Analisis;\n"
                + "import com.example.models.AFD;\n"
                + "import java.io.FileInputStream;\n"
                + "import java.io.ObjectInputStream;\n"
                + "import java.io.IOException;\n\n"
                + "public class " + className + " {\n"
                + "    public static void main(String[] args) throws IOException {\n"
                + "        if (args.length == 0) {\n"
                + "            System.out.println(\"Uso: java " + className + " <archivo_de_entrada>\");\n"
                + "            return;\n"
                + "        }\n"
                + "        String inputFile = args[0];\n"
                + "        AFD actualModel;\n"
                + "        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(\"AFD.dat\"))) {\n"
                + "            actualModel = (AFD) in.readObject();\n"
                + "        } catch (IOException | ClassNotFoundException e) {\n"
                + "            System.err.println(\"Error al cargar el AFD: \" + e.getMessage());\n"
                + "            return;\n"
                + "        }\n"
                + "        Lex_Analisis analizador = new Lex_Analisis(actualModel, inputFile);\n"
                + "        analizador.print_tokens();\n"
                + "    }\n"
                + "}";

        // Escribir el código generado en un archivo
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            fileWriter.write(javaCode);
            System.out.println("Archivo generado exitosamente: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}
