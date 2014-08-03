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
    private HashSet<Integer> correctAnswerStudents;
    private HashSet<Integer> incorrectAnswerStudents;
    private HashSet<Integer> students;
    private boolean highlighted;

    public State(Float x, Float y, Float r, Integer count, Problem p, Integer student) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.count = count;
        this.p = p;
        this.correctAnswerStudents = new HashSet<>();
        this.incorrectAnswerStudents = new HashSet<>();
        this.students = new HashSet<>();
        this.students.add(student);
        this.highlighted = false;
    }
    
    public State(Problem p, Integer student){
        this(0f, 0f, 0f, 1, p, student);
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return x.intValue() + " / " + y.intValue()+ " / " + r.intValue();
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
        double newY = Math.round((Math.sin(Math.toRadians(this.r)) * units) * 1000) / 1000;
        double newX = Math.round((Math.cos(Math.toRadians(this.r)) * units) * 1000) / 1000;
        newState.y += (float)newY;
        newState.x += (float)newX;
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
        s.append("\nCorrect students: ");
        s.append(correctAnswerStudents);
        s.append("\nWrong Students: ");
        s.append(incorrectAnswerStudents);
        return s.toString();
    }
    
    public float getRawNormalizedIntensity(){
        float returnValue = (float)this.count / this.p.getMaxState();
        return returnValue;
    }
    
    public float getCorrectRatio(){
        return ((float)correctAnswerStudents.size())/students.size();
    }
    
    public float getWrongRatio(){
        System.out.println(((float)incorrectAnswerStudents.size())/students.size());
        return ((float)incorrectAnswerStudents.size())/students.size();
    }
    
    public boolean isStudentInState(int i){
        return students.contains(i);
    }
    
    public void incrementCounter(Integer student){
        this.count++;
        this.students.add(student);
    }
    
    public void setAsCorrectAnswer(Integer student) {
        this.correctAnswerStudents.add(student);
    }
    
    public void setAsIncorrectAnswer(Integer student){
        this.incorrectAnswerStudents.add(student);
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
        return !correctAnswerStudents.isEmpty();
    }
    
    public boolean isIncorrectAnswer() {
        return !incorrectAnswerStudents.isEmpty();
    }
    
    public boolean isStudentCorrect(int student){
        return correctAnswerStudents.contains(student);
    }
    
    public boolean isStudentWrong(int student){
        return incorrectAnswerStudents.contains(student);
    }
    
}
