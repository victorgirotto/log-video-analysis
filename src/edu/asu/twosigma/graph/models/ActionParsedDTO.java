/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.models;

/**
 *
 * @author victorgirotto
 */
public class ActionParsedDTO {
    
    private State initialState;
    private State finalState;
    private Action action;
    private Integer problem;
    private Integer student;

    public ActionParsedDTO() {}

    @Override
    public String toString() {
        return "ActionParsedDTO{" + "initialState=" + initialState + ", finalState=" + finalState + ", action=" + action + ", problem=" + problem + ", student=" + student + '}';
    }
    
    public ActionParsedDTO(State initialState) {
        this.initialState = initialState;
    }

    public State getInitialState() {
        return initialState;
    }

    public State getFinalState() {
        return finalState;
    }

    public Action getAction() {
        return action;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public void setFinalState(State finalState) {
        this.finalState = finalState;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Integer getProblem() {
        return problem;
    }

    public void setProblem(Integer problem) {
        this.problem = problem;
    }

    public Integer getStudent() {
        return student;
    }

    public void setStudent(Integer student) {
        this.student = student;
    }
    
    
    
}
