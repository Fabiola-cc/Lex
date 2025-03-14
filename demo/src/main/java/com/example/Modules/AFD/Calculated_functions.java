package com.example.Modules.AFD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.models.node;

public class Calculated_functions {
    public static List<String> getFirstPos(node element) {
        if (element == null) {
            return Collections.emptyList();
        }

        String nodeValue = element.getValue();
        List<String> result = new ArrayList<>();
        if (!element.isAlphanumeric()) {
            switch (nodeValue) {
                case "|":
                    for (int hijo : element.getNodes()) {
                        result.addAll(Direct_AFD.getTree_info().get(hijo).getFirstpos());
                    }
                    return result;
                case "‧":
                    // Si el "hijo" a la *izquierda* es nulo
                    node condition = Direct_AFD.getTree_info().get(element.getNodes().get(0));
                    if (condition.isNullable()) {
                        for (int hijo : element.getNodes()) {
                            result.addAll(Direct_AFD.getTree_info().get(hijo).getFirstpos());
                        }
                    } else {
                        int hijo = element.getNodes().get(0);
                        result.addAll(Direct_AFD.getTree_info().get(hijo).getFirstpos());
                    }
                    return result;
                case "*":
                    int hijo = element.getNodes().get(0);
                    result.addAll(Direct_AFD.getTree_info().get(hijo).getFirstpos());
                    return result;
            }
        } else {
            result.add(element.getName());
        }

        return result;

    }

    public static List<String> getLastPos(node element) {
        if (element == null) {
            return Collections.emptyList();
        }

        String nodeValue = element.getValue();
        List<String> result = new ArrayList<>();

        if (!element.isAlphanumeric()) {
            switch (nodeValue) {
                case "|":
                    for (int hijo : element.getNodes()) {
                        result.addAll(Direct_AFD.getTree_info().get(hijo).getLastpos());
                    }
                    return result;
                case "‧":
                    node condition = Direct_AFD.getTree_info().get(element.getNodes().get(1));
                    if (condition.isNullable()) {
                        for (int hijo : element.getNodes()) {
                            result.addAll(Direct_AFD.getTree_info().get(hijo).getLastpos());
                        }
                    } else {
                        int hijo = element.getNodes().get(1);
                        result.addAll(Direct_AFD.getTree_info().get(hijo).getLastpos());
                    }
                    return result;
                case "*":
                    int hijo = element.getNodes().get(0);
                    result.addAll(Direct_AFD.getTree_info().get(hijo).getLastpos());
                    return result;
            }
        } else {
            result.add(element.getName());
        }
        return result;
    }

    public static void getFollowPos(node element) {
        if (element == null) {
            return;
        }

        String nodeValue = element.getValue();
        switch (nodeValue) {
            case "‧":
                int hijo1 = element.getNodes().get(0);
                int hijo2 = element.getNodes().get(1);
                List<String> sp = Direct_AFD.getTree_info().get(hijo2).getFirstpos();

                for (String lp1 : Direct_AFD.getTree_info().get(hijo1).getLastpos()) {
                    int lp = Direct_AFD.getTreeIndex(lp1);
                    if (lp >= 0) {
                        Direct_AFD.getTree_info().get(lp).getfollowpos().addAll(sp);
                    }
                }
                break;
            case "*":
                int hijo = element.getNodes().get(0);
                List<String> np = Direct_AFD.getTree_info().get(hijo).getFirstpos();

                for (String lp1 : Direct_AFD.getTree_info().get(hijo).getLastpos()) {
                    int lp = Direct_AFD.getTreeIndex(lp1);
                    if (lp >= 0) {
                        Direct_AFD.getTree_info().get(lp).getfollowpos().addAll(np);
                    }
                }
                break;
            default:
                return;
        }
    }

    public static Boolean isNullable(node element) {
        String nodeValue = element.getValue();
        boolean result = false;
        if (!element.isAlphanumeric()) {

            switch (nodeValue) {
                case "|":
                    for (int hijo : element.getNodes()) {
                        result = result || isNullable(Direct_AFD.getTree_info().get(hijo));
                    }
                    return result;
                case "‧":
                    for (int hijo : element.getNodes()) {
                        result = result && isNullable(Direct_AFD.getTree_info().get(hijo));
                    }
                    return result;
                case "*":
                    return true;
            }
        } else if (element.isAlphanumeric()) {
            return false;
        }

        return false; // Add a return statement to avoid compilation error
    }
}
