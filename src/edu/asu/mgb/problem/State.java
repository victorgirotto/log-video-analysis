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
public class State {
    
    private Float x;
    private Float y;
    private Float r;
    private Integer count;
    private Problem p;
    private boolean correctAnswer;
    private boolean incorrectAnswer;
    private HashSet<Integer> students;

    public State(Float x, Float y, Float r, Integer count, Problem p, Integer student) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.count = count;
        this.p = p;
        this.correctAnswer = false;
        this.incorrectAnswer = false;
        this.students = new HashSet<>();
        if(student == 0){
            System.out.println(student);
        }
        this.students.add(student);
    }
    
    public State(Problem p, Integer student){
        this(0f, 0f, 0f, 1, p, student);
    }
    
    

    @Override
    public String toString() {
        return x.toString() + " / " + y.toString() + " / " + r.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.x);
        hash = 41 * hash + Objects.hashCode(this.y);
        hash = 41 * hash + Objects.hashCode(this.r);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State other = (State) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        if (!Objects.equals(this.r, other.r)) {
            return false;
        }
        return true;
    }
    
    public State move(float units, Integer student){
        State newState = new State(this.x, this.y, this.r, 1, this.p, student);
        double y = Math.round((Math.sin(Math.toRadians(this.r)) * units) * 1000) / 1000;
        double x = Math.round((Math.cos(Math.toRadians(this.r)) * units) * 1000) / 1000;
        newState.y += (float)y;
        newState.x += (float)x;
        return newState;
    }
    
    public State turn(String original, float angle, Integer student){
        State newState = new State(this.x, this.y, this.r, 1, this.p, student);
        if(Util.isNumeric(original)){
            // If the original was numeric, the angle should be added (turn angle action).
            newState.r = (newState.r + angle) % 360;
        } else {
            // If the original was not numeric, angle is absolute (turn in diretion action).
            newState.r = angle;
        }
        return newState;
    }
        
    public float getNormalizedIntensity(){
        float returnValue;
        if(isInitialState()){
            // Initial position is not counted
            returnValue = Problem.INITIAL;
        } else if(isCorrectAnswer()) {
            returnValue = Problem.CORRECT;
        } else if(isIncorrectAnswer()) {
            returnValue = Problem.INCORRECT;
        } else {
            returnValue = (float)this.count / this.p.getMaxState();
        }
        return returnValue;
    }
    
    public String toFullString(){
        StringBuilder s = new StringBuilder();
        s.append("X,Y: ");
        s.append(this.x);
        s.append(", ");
        s.append(this.y);
        s.append("\nR: ");
        s.append(this.r);
        s.append("\nStudents: ");
        s.append(students);
        return s.toString();
    }
    
    public float getRawNormalizedIntensity(){
        float returnValue = (float)this.count / this.p.getMaxState();
        return returnValue;
    }
    
    public boolean isStudentInState(int i){
        return students.contains(i);
    }
    
    public void incrementCounter(Integer student){
        this.count++;
        this.students.add(student);
    }
    
    public void setAsCorrectAnswer() {
        this.correctAnswer = true;
    }
    
    public void setAsIncorrectAnswer(){
        this.incorrectAnswer = true;
    }
    
    public Float getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public Float getR() {
        return r;
    }

    public Integer getCount() {
        return count;
    }
    
    public boolean isInitialState(){
        return this.x == 0 && this.y == 0 && this.r == 0;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }
    
    public boolean isIncorrectAnswer() {
        return incorrectAnswer;
    }
    
}
