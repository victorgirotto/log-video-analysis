/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.coding;

import java.util.HashMap;

/**
 *
 * @author victorgirotto
 */
public class CodingUtil {
    
    private static final Integer CATEGORIES = 4;
    
    private static final HashMap<String, Integer> CHEAT_SHEET;
    private static final HashMap<String, Integer> VERTICAL_PROX;
    private static final HashMap<String, Integer> HORIZONTAL_PROX;
    private static final HashMap<String, Integer> MOVEMENT_BEFORE;
    private static final HashMap<String, Integer> MOVEMENT_AFTER;
    
    static {
        // Instantiating hashmaps
        CHEAT_SHEET = new HashMap<>();
        VERTICAL_PROX = new HashMap<>();
        HORIZONTAL_PROX = new HashMap<>();
        MOVEMENT_BEFORE = new HashMap<>();
        MOVEMENT_AFTER = new HashMap<>();
        // Setting cheat sheet
        CHEAT_SHEET.put("quick", 1);
        CHEAT_SHEET.put("long", 2);
        // Setting Vertical prox
        VERTICAL_PROX.put("slightly bent at waist", 1);
        VERTICAL_PROX.put("bent at waist", 2);
        VERTICAL_PROX.put("kneeling", 3);
        VERTICAL_PROX.put("kneeling w/knees on floor", 4);
        // Setting Horizontal prox
        HORIZONTAL_PROX.put("far", 1);
        HORIZONTAL_PROX.put("close", 2);
        // Setting Movement before
        MOVEMENT_BEFORE.put("little/no movement", 1);
        MOVEMENT_BEFORE.put("purposeful movement", 2);
        // Setting Movement after
        MOVEMENT_AFTER.put("little/no movement", 1);
        MOVEMENT_AFTER.put("purposeful movement", 2);
    }
    
    public static Integer getCheatSheetValue(String value){
        if(value != null && !value.trim().isEmpty()){
            value = value.trim();
            Integer retrieved = CHEAT_SHEET.get(value);
            if(retrieved != null){
                Double returnValue = retrieved * getRatio(CHEAT_SHEET.size());
                return returnValue.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public static Integer getVerticalProximityValue(String value){
        if(value != null && !value.trim().isEmpty()){
            value = value.trim();
            Integer retrieved = VERTICAL_PROX.get(value);
            if(retrieved != null){
                Double returnValue = retrieved * getRatio(VERTICAL_PROX.size());
                return returnValue.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public static Integer getHorizontalProximityValue(String value){
        if(value != null && !value.trim().isEmpty()){
            value = value.trim();
            Integer retrieved = HORIZONTAL_PROX.get(value);
            if(retrieved != null){
                Double returnValue = retrieved * getRatio(HORIZONTAL_PROX.size());
                return returnValue.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public static Integer getMovementBeforeValue(String value){
        if(value != null && !value.trim().isEmpty()){
            value = value.trim();
            Integer retrieved = MOVEMENT_BEFORE.get(value);
            if(retrieved != null){
                Double returnValue = retrieved * getRatio(MOVEMENT_BEFORE.size());
                return returnValue.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    public static Integer getMovementAfterValue(String value){
        if(value != null && !value.trim().isEmpty()){
            value = value.trim();
            Integer retrieved = MOVEMENT_AFTER.get(value);
            if(retrieved != null){
                Double returnValue = retrieved * getRatio(MOVEMENT_AFTER.size());
                return returnValue.intValue();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    private static Double getRatio(int size){
        return size / (double)CATEGORIES;
    }
}
