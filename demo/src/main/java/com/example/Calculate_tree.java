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
  private static int nodeCounter = 1;
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
   * Handles leaf nodes (operands) in the expression
   */
  private void handleAlfanumerico(RegexToken token) {

  }

  /**
  * Handles operator nodes based on their type
  */
  private void handleOperator(RegexToken token) {

  }


  public static void main(String[] args) {
    
  }

}
