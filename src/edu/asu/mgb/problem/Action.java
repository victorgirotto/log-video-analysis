/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.problem;

import java.util.HashSet;
import java.util.Objects;

/**
 *
 * @author victorgirotto
 */
public class Action {
    
    private String action;
    private State origin;
    private State destination;
    private int count;
    private Problem p;
    private HashSet<Integer> students;
    private boolean highlighted;

    public Action(String action, State origin, State destination, Problem p, Integer student) {
        this.action = action;
        this.origin = origin;
        this.destination = destination;
        this.p = p;
        this.count = 1;
        this.students = new HashSet<>();
        this.students.add(student);
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if(obj instanceof Action){
            Action compAction = (Action)obj;
            if(this.action.equals(compAction.action) && 
               this.origin.equals(compAction.origin) && 
               this.destination.equals(compAction.destination)){
                equals = true;
            }
        }
        return equals;
    }

    @Override
    public String toString() {
        return this.action;
    }
    
    public String toFullString(){
        StringBuilder s = new StringBuilder();
        s.append(this.action);
        s.append("\n");
        s.append(students);
        return s.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.action);
        hash = 97 * hash + Objects.hashCode(this.origin);
        hash = 97 * hash + Objects.hashCode(this.destination);
        return hash;
    }
    
    public void incrementCounter(Integer student){
        this.count++;
        this.students.add(student);
    }
    
    public boolean isStudentInACtion(int s){
        return students.contains(s);
    }

    public int getCount() {
        return this.count;
    }
    
    public float getNormalizedCount(){
        return (float)this.count / this.p.getMaxAction();
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
   
}
