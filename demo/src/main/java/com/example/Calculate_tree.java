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
  
  //  Asignamos los parámetros a utilizar, la lista de nodos que retornaremos, el nodo en el que nuesencotramos y el stack que nos ayudará a hacer el arbol 
  private int alfanumericoCounter = 1;
  private int operatorCounter = 1;
  private final List<node> treeNodes;
  private final Stack<node> operandStack;

  public Calculate_tree() {
      this.treeNodes = new ArrayList<>();
      this.operandStack = new Stack<>();
  }

  /**
   * Método principal que obtiene la lista de nodos y convierte en un árbol
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
  *   4.
  */
  private void handleOperator(RegexToken token) {
    node operatorNode = createNode(token.getValue().charAt(0), false);
    operatorNode.setName("o" + operatorCounter++);
    if (token.getValue().equals("*")){
      operatorNode.setNullable(true);
    }
    
    if (!operandStack.isEmpty()) {
        node rightChild = operandStack.pop();
        if (!operandStack.isEmpty()) {
            node leftChild = operandStack.pop();
            operatorNode.getNodes().add(treeNodes.indexOf(leftChild));
        }
        operatorNode.getNodes().add(treeNodes.indexOf(rightChild));
    }

    operandStack.push(operatorNode);
    treeNodes.add(operatorNode);
  }


  private node createNode(char value, boolean isAlphanumeric) {
    return new node(value, isAlphanumeric);
}
  


  public static void main(String[] args) {
    
  }

}
