package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.example.models.RegexToken;
import com.example.models.node;

public class Calculate_tree {
  
  // INPUT: List<RegexToken> 
  // OUTPUT: List<node> 
  

  /*
  * Crear una función que toma un List<RegexToken> que nos da la información de si es un operador o no y que valor tiene
  * Tenemos que regresar una lista de nodos en donde se indique la jerarquia 
  * 
  * Proceso para lograrlo: leer primer objeto de Lista input si no es operdor obtener valor y grardarlo en un stack
  * , lo mismo con lo siguinte hasta que se tope con un operador va a crear un nodo operador 
  */
  
  //  Asignamos los parámetros a utilizar, la lista de nodos que reto
  // Contadores para nombrar nodos
  private int alfanumericoCounter = 1;
  private int operatorCounter = 1;

  // Estructuras de datos principales
  private final List<node> treeNodes;
  private final Stack<node> operandStack;

  public Calculate_tree() {
      this.treeNodes = new ArrayList<>();
      this.operandStack = new Stack<>();
  }

  /**
   * 
   * Método principal que obtiene la lista de nodos y convierte en un árbol
   * Convierte una expresión postfija en un árbol sintáctico.
   * Cada token es un operando (nodo hoja) o un operador (nodo interno).
   * Retorna la lista completa de nodos con sus relaciones jerárquicas establecidas.
  */

  public List<node> convertPostfixToTree(List<RegexToken> postfix) {
    for (RegexToken token : postfix) {
        if (!token.getIsOperator()) {
            handleAlfanumerico(token);
        } else {
            handleOperator(token);
        }
    }
    return treeNodes;
  }

  /**
   * Maneja nodos hoja que son álfanumétricos que no tiene anidado nungun nodo 
   *    1. crear un nuevo nodo con ese token suma en contador 1
   *    2. Pushea el nuevo nodo al stack
   */
  private void handleAlfanumerico(RegexToken token) {
    node leafNode = createNode(token.getValue().charAt(0), true);
    leafNode.setName(String.valueOf(alfanumericoCounter++));
    operandStack.push(leafNode);
    treeNodes.add(leafNode);
  }

  /**
  * Maneja operadores 
  *   1. Crea nuevo nodo operador 
  *   2. Cambia si nombre a o1, sulicidato para mejorar proceso de AFD
  *   3. Maneja cuando es * puede ser vacio  
  *   4. Si stack no esta vacio, el primero que fue el ultimo en ingresar sera puesto como el nodo de la derecha 
  */
  private void handleOperator(RegexToken token) {
    node operatorNode = createNode(token.getValue().charAt(0), false);
    operatorNode.setName("o" + operatorCounter++);
    
    // Manejo especial para el operador estrella de Kleene
    if (token.getValue().equals("*")) {
        operatorNode.setNullable(true);
        // Extrae un solo operando para operador unario
        if (!operandStack.isEmpty()) {
            node child = operandStack.pop();
            operatorNode.getNodes().add(treeNodes.indexOf(child));
        }
    } else {
        // Maneja operadores binarios (|, ‧)
        if (!operandStack.isEmpty()) {
            node rightChild = operandStack.pop();
            if (!operandStack.isEmpty()) {
                node leftChild = operandStack.pop();
                operatorNode.getNodes().add(treeNodes.indexOf(leftChild));
            }
            operatorNode.getNodes().add(treeNodes.indexOf(rightChild));
        }
    }
    
    operandStack.push(operatorNode);
    treeNodes.add(operatorNode);
  } 

  // Crea un nuevo nodo con el valor y tipo especificados.
  private node createNode(char value, boolean isAlphanumeric) {
    return new node(value, isAlphanumeric);
  }
  


  public static void main(String[] args) {
    // Ejemplo de uso
    Calculate_tree calculator = new Calculate_tree();
    List<RegexToken> postfixExample = new ArrayList<>();
    
    // Ejemplo: a b | * a ‧ b ‧  (representa (a|b)* ‧ a ‧ b)
    postfixExample.add(new RegexToken("a", false));
    postfixExample.add(new RegexToken("b", false));
    postfixExample.add(new RegexToken("|", true));
    postfixExample.add(new RegexToken("*", true));
    postfixExample.add(new RegexToken("a", false));
    postfixExample.add(new RegexToken("‧", true));
    postfixExample.add(new RegexToken("b", false));
    postfixExample.add(new RegexToken("‧", true));
    
    postfixExample.forEach(token -> System.out.print(token.getValue() + " "));
    System.out.println('\n');
    List<node> result = calculator.convertPostfixToTree(postfixExample);
    for (int i = 0; i < result.size(); i++) {
      node n = result.get(i);
      System.out.println("Índice: " + i + 
                       " | Nodo: " + n.getName() + 
                       " Valor: " + n.getValue() + 
                       " Hijos: " + n.getNodes());
    }
  }
}
