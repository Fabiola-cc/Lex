# Conversor de Expresiones Regulares a AFD

Este laboratorio implementa un conversor de Expresiones Regulares a Autómatas Finitos Deterministas (AFD) con capacidades de visualización. Utiliza el método de conversión directa, que incluye la construcción de un árbol sintáctico y la generación del AFD directamente a partir de él.

## Características

- Convierte expresiones regulares a AFD usando el método de conversión directa
- Soporta varios operadores de expresiones regulares:
  - Concatenación (‧)
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

## Estructura del Proyecto

### Componentes Principales

- `ShuntingYard.java`: Convierte expresiones regulares de notación infija a postfija
- `Calculate_tree.java`: Construye el árbol sintáctico a partir de la expresión postfija
- `Calculated_functions.java`: Implementa funciones de recorrido del árbol (firstpos, lastpos, followpos)
- `Direct_AFD.java`: Genera el AFD a partir del árbol sintáctico
- `Main.java`: Punto de entrada principal de la aplicación

### Modelos

- `RegexToken.java`: Representa los tokens en la expresión regular
- `node.java`: Representa los nodos en el árbol sintáctico
- `AFD.java`: Representa el Autómata Finito Determinista

### Visualización

- `Draw_Tree.java`: Genera representación visual de árboles sintácticos
- `Draw_AFD.java`: Crea representación visual de autómatas

## Uso

1. Ejecutar la clase Main
2. Ingresar una expresión regular cuando se solicite
3. El programa:
   - Convertirá la regex a notación postfija
   - Generará un árbol sintáctico
   - Creará una representación visual del árbol
   - Generará el AFD
   - Mostrará la visualización del AFD
   - Creará una versión minimizada del AFD
   - Probará cadenas de entrada contra el AFD

### Ejemplo

```java
// Expresión regex de entrada: (a|b)*‧a‧b
// Esto creará un AFD que acepta cadenas que:
// - Comienzan con cualquier número de 'a's o 'b's
// - Terminan con 'ab'
```

## Sintaxis de Expresiones Regulares

- `|` : Alternativa (unión)
- `‧` : Concatenación
- `*` : Estrella de Kleene (cero o más)
- `+` : Uno o más
- `?` : Opcional (cero o uno)
- `[a-z]` : Rango de caracteres
- `^[a-z]` : Rango negado
- `[a-z]#[p-t]` : Diferencia de rangos
- `\char` : Carácter escapado

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
- Las visualizaciones de AFD se mostrarán en ventanas separadas

## Manejo de Errores

El proyecto incluye manejo de errores para:
- Expresiones regulares inválidas
- Corchetes mal formados
- Operadores faltantes
- Rangos de caracteres inválidos

## Autores 

+ Fabiola Contreras 22787
+ Diego Duarte 22075
+ María José Villafuerte 22129
