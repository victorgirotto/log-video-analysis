/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author victorgirotto
 */
public class ProblemManager {
    
    private final HashMap<Integer, Problem> problems;
    private final HashSet<Integer> studentsList;
    private Problem currentProblem;
    private Integer currentStudent;
    
    // Actions on the blacklist will not be considered into the graph
    private static final List<String> BLACKLIST;
    private static final String CHANGE_PROBLEM_STRING;
    private static final String SWITCH_USER;

    static {
        BLACKLIST = Arrays.asList("Click");
        CHANGE_PROBLEM_STRING = "Current";
        SWITCH_USER = "Switch user ";
    }
    
    public ProblemManager() {
        this.studentsList = new HashSet<>();
        this.problems = new HashMap<>();
        this.currentStudent = 1;
    }
    
    public Problem getProblem(int id){
        return problems.get(id);
    }

    public Integer getCurrentStudent() {
        return currentStudent;
    }
    
    public void addStudentToSet(int student){
        this.studentsList.add(student);
    }

    public Integer[] getStudentsList() {
        return studentsList.toArray(new Integer[studentsList.size()]);
    }
    
    public Integer[] getProblemsList() {
        return problems.keySet().toArray(new Integer[problems.size()]);
    }
    
    public ActionParsedDTO handleAction(String action){
        ActionParsedDTO dto = null;
        Problem newCurrentProblem;
        int problemNumber;
        int studentNumber;
        // Removing whitespaces
        action = action.trim();
        // Don't do anything if there is no state or if action is in blacklist
        if(!BLACKLIST.contains(action)){
            if (action.startsWith(SWITCH_USER)){
                studentNumber = Util.getStudentNumber(action, SWITCH_USER);
                currentStudent = studentNumber;
                addStudentToSet(currentStudent);
            } else if(action.startsWith(CHANGE_PROBLEM_STRING)){
                // Instantiate a new Problem, or load the existing one
                problemNumber = Util.getProblemNumber(action);
                if(problems.containsKey(problemNumber)){
                    // This problem has been analysed before. Retrieve it.
                    newCurrentProblem = problems.get(problemNumber);
                    newCurrentProblem.goBackToOrigin();
                } else {
                    // This problem hasn' been analysed yet. Instantiate a new one
                    newCurrentProblem = new Problem(problemNumber, this);
                    problems.put(problemNumber, newCurrentProblem);
                }
                // Setting the problem as current
                this.currentProblem = newCurrentProblem;
            } else if(currentProblem != null) {
                // Sending action to be handled by the problem (it will change the its state)
                dto = currentProblem.handleAction(action);
                // Setting student number. Problem number was set in handleAction
                if(dto != null){
                    dto.setStudent(currentStudent);
                }
            }
        }
        return dto;
    }
    
}
