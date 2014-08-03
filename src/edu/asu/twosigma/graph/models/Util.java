/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.models;

import java.util.HashMap;

/**
 *
 * @author victorgirotto
 */
public class Util {
    
    private static final HashMap<String, Float> angles;
    static
    {
        angles = new HashMap<>();
        angles.put("E", 0f);
        angles.put("N", 90f);
        angles.put("W", 180f);
        angles.put("S", 270f);
    }
    
    public static Integer getStudentNumber(String action, String removeString){
        return new Integer(action.replaceAll(removeString, ""));
    }
    
    public static Integer getProblemNumber(String action){
        return new Integer(action.split(":")[1].trim());
    }
    
    public static float getMoveUnits(String units){
        return Float.parseFloat(units);
    }
    
    public static float getTurnUnits(String turn){
        float angle;
        if(isNumeric(turn)){
            angle = Float.parseFloat(turn);
        } else {
            angle = angles.get(turn);
        }
        return angle;
    }
    
    public static boolean isNumeric(String str)
    {
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
