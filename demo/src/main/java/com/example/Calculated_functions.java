package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.models.node;

public class Calculated_functions {
    public static List<String> getFirstPos(node element) {
        if (element == null) {
            return Collections.emptyList();
        }

        Character nodeValue = element.getValue();
        List<String> result = new ArrayList<>();
        switch (nodeValue) {
            case '|':
                for (int hijo : element.getNodes()) {
                    result.addAll(getFirstPos(AFD.getTree_info().get(hijo)));
                }
                return result;
            case '‧':
                node condition = AFD.getTree_info().get(element.getNodes().get(0));
                if (condition.isNullable()) {
                    for (int hijo : element.getNodes()) {
                        result.addAll(getFirstPos(AFD.getTree_info().get(hijo)));
                    }
                } else {
                    int hijo = element.getNodes().get(0);
                    result.addAll(getFirstPos(AFD.getTree_info().get(hijo)));
                }
                return result;
            case '*':
                int hijo = element.getNodes().get(0);
                result.addAll(getFirstPos(AFD.getTree_info().get(hijo)));
                return result;
            default:
                if (element.isAlphanumeric()) {
                    result.add(element.getName());
                }
                break;
        }
        return result;
    }

    public static List<String> getLastPos(node element) {
        if (element == null) {
            return Collections.emptyList();
        }

        Character nodeValue = element.getValue();
        List<String> result = new ArrayList<>();
        switch (nodeValue) {
            case '|':
                for (int hijo : element.getNodes()) {
                    result.addAll(getLastPos(AFD.getTree_info().get(hijo)));
                }
                return result;
            case '‧':
                node condition = AFD.getTree_info().get(element.getNodes().get(1));
                if (condition.isNullable()) {
                    for (int hijo : element.getNodes()) {
                        result.addAll(getLastPos(AFD.getTree_info().get(hijo)));
                    }
                } else {
                    int hijo = element.getNodes().get(1);
                    result.addAll(getLastPos(AFD.getTree_info().get(hijo)));
                }
                return result;
            case '*':
                int hijo = element.getNodes().get(0);
                result.addAll(getLastPos(AFD.getTree_info().get(hijo)));
                return result;
            default:
                if (element.isAlphanumeric()) {
                    result.add(element.getName());
                }
                break;
        }
        return result;
    }

    public static void getFollowPos(node element) {
        if (element == null) {
            return;
        }

        Character nodeValue = element.getValue();
        switch (nodeValue) {
            case '‧':
                int hijo1 = element.getNodes().get(0);
                int hijo2 = element.getNodes().get(1);
                List<String> sp = AFD.getTree_info().get(hijo2).getFirstpos();

                for (String lp1 : AFD.getTree_info().get(hijo1).getLastpos()) {
                    int lp = AFD.getTreeIndex(lp1);
                    if (lp >= 0) {
                        AFD.getTree_info().get(lp).getfollowpos().addAll(sp);
                    }
                }
                break;
            case '*':
                int hijo = element.getNodes().get(0);
                List<String> np = AFD.getTree_info().get(hijo).getFirstpos();

                for (String lp1 : AFD.getTree_info().get(hijo).getLastpos()) {
                    int lp = AFD.getTreeIndex(lp1);
                    if (lp >= 0) {
                        AFD.getTree_info().get(lp).getfollowpos().addAll(np);
                    }
                }
                break;
            default:
                return;
        }
    }

    public static Boolean isNullable(node element) {
        Character nodeValue = element.getValue();
        boolean result = false;
        switch (nodeValue) {
            case '|':
                for (int hijo : element.getNodes()) {
                    result = result || isNullable(AFD.getTree_info().get(hijo));
                }
                return result;
            case '‧':
                for (int hijo : element.getNodes()) {
                    result = result && isNullable(AFD.getTree_info().get(hijo));
                }
                return result;
            case '*':
                return true;
            default:
                if (element.isAlphanumeric()) {
                    return false;
                }
                break;
        }

        return true; // Add a return statement to avoid compilation error
    }
}
