package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JavaFileGenerator {
    public static void generateFile(List<String> headers) {
        String className = "Yalex"; // Nombre de la clase generada
        String javaOutputPath = "demo/src/main/java/com/example/" + className + ".java"; // Archivo .java
        String batOutputPath = "run.bat"; // Archivo .bat

        // Código fuente en forma de String
        StringBuilder javaCode = new StringBuilder();

        for (String header : headers) {
            javaCode.append(header).append("\n");
        }

        javaCode.append("package com.example;\n\n")
                .append("import com.example.Modules.Analisis.Lex_Analisis;\n")
                .append("import com.example.models.AFD;\n")
                .append("import java.io.FileInputStream;\n")
                .append("import java.io.ObjectInputStream;\n")
                .append("import java.io.IOException;\n\n")
                .append("public class ").append(className).append(" {\n")
                .append("    public static void main(String[] args) throws IOException {\n")
                .append("        if (args.length == 0) {\n")
                .append("            System.out.println(\"Uso: java ").append(className)
                .append(" <archivo_de_entrada>\");\n")
                .append("            return;\n")
                .append("        }\n")
                .append("        String inputFile = args[0];\n")
                .append("        AFD actualModel;\n")
                .append("        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(\"demo/src/main/resources/AFD.dat\"))) {\n")
                .append("            actualModel = (AFD) in.readObject();\n")
                .append("        } catch (IOException | ClassNotFoundException e) {\n")
                .append("            System.err.println(\"Error al cargar el AFD: \" + e.getMessage());\n")
                .append("            return;\n")
                .append("        }\n")
                .append("        Lex_Analisis analizador = new Lex_Analisis(actualModel, inputFile);\n")
                .append("        analizador.print_tokens();\n")
                .append("    }\n")
                .append("}");

        // Escribir el archivo .java
        writeFile(javaOutputPath, javaCode.toString());

        // Contenido del archivo run.bat
        String batCode = "@echo off\n"
                + "set CLASSPATH=bin\n"
                + "javac -d bin -cp demo/src/main/java demo/src/main/java/com/example/Yalex.java\n"
                + "java -cp bin com.example.Yalex %1\n";

        // Escribir el archivo run.bat
        writeFile(batOutputPath, batCode);
    }

    private static void writeFile(String filePath, String content) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(content);
            System.out.println("Archivo generado exitosamente: " + filePath);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}
