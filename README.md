# LEX

El proyecto LEX implementa un analizador léxico basado en YALEX (Yet Another Lex), una herramienta que genera Autómatas Finitos Deterministas (AFD) a partir de expresiones regulares definidas en un archivo de configuración (.yal). Su propósito es automatizar la generación de analizadores léxicos eficientes, facilitando la construcción de compiladores e intérpretes.

Este sistema toma expresiones regulares y reglas léxicas definidas por el usuario, las transforma en un AFD optimizado, y genera código en Java capaz de analizar archivos de entrada y reconocer tokens válidos según la gramática especificada. Además, ofrece herramientas para la visualización de árboles sintácticos y autómatas, lo que permite una mejor comprensión del proceso de conversión.

## Características

- Lee un archivo .yal y procesa sus caracteristicas para generar un correcto AFD
- Genera una expresion regular conjunta con tokens
- Convierte expresiones regulares a AFD usando el método de conversión directa
- Soporta varios operadores de expresiones regulares:
  - Unión (|)
  - Estrella de Kleene (*)
  - Más (+)
  - Opcional (?)
- Maneja características especiales de regex:
  - Rangos de caracteres ([a-z])
  - Rangos negados (^[a-z])
  - Diferencias de rangos ([a-z]#[p-t])
  - Caracteres escapados (\char)
- Proporciona representación visual de:
  - Árboles sintácticos
  - AFDs generados
  - AFDs minimizados
- Genera un código de procesamiento de archivos utilizando el AFD previamente generado
- Es capaz de marcar errores en el procesamiento en caso de no reconocer algún patrón

## Estructura del Proyecto

### Módulos Principales

- `JavaFileGenerator.java`: Genera el archivo de código que procesa ek archivo de texto utilizando el AFD previamente generado
- `Main.java`: Punto de entrada principal de la aplicación


#### Input

- `LexerConfigParser.java`: Recibe el archivo .yal de configuarción y regresa una lista de imports en el header y un mapa de relación entre expreciones regulares y tokens

#### Regex

- `RegexConvertor.java`: Limpia las expresiones regulares del mapa relación para su procesamiento
- `RegexGenerator.java`: Genera una expresión regular conjunta de todas las anteriores incluyendo su respectivo token
- `ShuntingYard.java`: Convierte expresiones regulares de notación infija a postfija

#### AFD

- `Calculate_tree.java`: Construye el árbol sintáctico a partir de la expresión postfija
- `Calculated_functions.java`: Implementa funciones de recorrido del árbol (firstpos, lastpos, followpos)
- `Direct_AFD.java`: Genera el AFD a partir del árbol sintáctico
- `AFDMinimizador.java` : Minimiza el AFD generado

#### Analisis

- `Lex_Analisis.java` : Utiliza el AFD generado para poder procesar un archivo de texto 

#### Error

- `Lex_errors.java` : Utilizado para imprimir errores en el procesamiento del archivo 

### Modelos

- `RegexToken.java`: Representa los tokens en la expresión regular
- `node.java`: Representa los nodos en el árbol sintáctico
- `AFD.java`: Representa el Autómata Finito Determinista

### Visualización

- `Draw_Tree.java`: Genera representación visual de árboles sintácticos
- `Draw_AFD.java`: Crea representación visual de autómatas

## Uso

1. Ingresar al archivo lexer.yal que se encuentra al archivo de resources y seguir las siguientes instrucciones:
   -  Al inicio del archivo se encuentran los headers (Se pueden identificar porque se encuntran entre corchetes "{}"). Para el funcionamiento correcto del lexer, los import que se encuentran allí **NO**   se pueden borrar
   -  Se puden definir nombres de expresiones regulares con este formato: let NOMBRE = EXPRESION REGULAR
   -  Para poder definir los tokens, se debe de definir un entrypoint llamado gettoken, cada regla se debe de definir con este formato: REGEX o NOMBRE { return TOKEN }. Cada uno debe de ser separado con      "|".
   -  Al definir una expresión regular, se debe de agregar comillas simples cuando se quiera tomar como literal. "[A-Z]" -> "['A'-'Z']
   -  Para poder definir saltos de linea o espacios, se deben definir afuera de las reglas de token. 
  
2. Ejecutar la clase Main
3. El programa:
   - Procesará lo encontrado en el archivo lexer.yal
   - Generará una regex conjunta, usando todas las definiciones planteadas
   - Convertirá la regex a notación postfija
   - Generará un árbol sintáctico
   - Creará una representación visual del árbol
   - Generará el AFD
   - Mostrará la visualización del AFD
   - Creará una versión minimizada del AFD
   - Generará el código de analisis llamado Yalex.java
   - Compilará el código y producirá su ejecutable run.bat
     
4. Ahora se debe de modificar el archivo ejemplo.txt agregando cadenas de texto que fueron definidas para reconocer en el arhcivo lexer.yal.
4. Para ejecutar el código analizador, se debe utilizar este comando:

```bash
./run.bat ejemplo.txt 
```


### Ejemplo

```java
{
    // Estos imports son parte de la lógica de compilación
    // No deben quitarse para el buen funcionamiento
    import com.example.Modules.Analisis.Lex_Analisis;
    import com.example.models.AFD;
    import java.io.FileInputStream;
    import java.io.ObjectInputStream;
    import java.io.IOException;
}

let digit = ['0'-'9']
let number = digit+
let blanks = ' '|'\t'

rule gettoken =
blanks { return lexbuf }
| '\n' { return EOL }
| '+' { return PLUS }
| '-' { return MINUS }
| '*' { return TIMES }
| '/' { return DIV }
| '(' { return LPAREN }
| ')' { return RPAREN }
| digit { return DIGIT }
| number { return NUMBER }
```

## Dependencias

- Java Swing (para componentes GUI)
- Graphviz (para visualización de árboles)

## Compilación y Ejecución

1. Asegúrese de tener Java JDK instalado
2. Compile el proyecto:
   ```bash
   javac com/example/*.java
   ```
3. Ejecute la clase principal:
   ```bash
   java com.example.Main
   ```

## Archivos de Salida

- `Syntax_Tree.png`: Representación visual del árbol sintáctico
- Las visualizaciones de AFD se mostrarán en ventanas separadas: `AFD_image.png` y  `MiniAFDimage.png`
- `Yalex.java`: Código generado para analísis
- `run.bat`: Compilación del código generado

## Manejo de Errores

El proyecto incluye manejo de errores para:
- Expresiones regulares inválidas
- Corchetes mal formados
- Operadores faltantes
- Rangos de caracteres inválidos
- Casos que no se reconozca un token

## Autores 

+ Fabiola Contreras 22787
+ Diego Duarte 22075
+ María José Villafuerte 22129
