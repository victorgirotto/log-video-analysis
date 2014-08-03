/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.models;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.HashMap;

/**
 *
 * @author victorgirotto
 */
public class Problem {
    
    private Integer number;
    private HashMap<String, State> states;
    private HashMap<Integer, Action> actions;
    private Graph<State, Action> graph;
    private State current;
    private Integer maxState;
    private Integer maxAction;
    private ProblemManager manager;
    
    private static final String MAKE_COMMAND = "Make command";
    private static final String MOVE = "Move";
    private static final String TURN = "Turn";
    private static final String SOLUTION = "Solution";
    private static final String SOLUTION_CORRECT = "Correct";
    private static final String SOLUTION_INCORRECT = "Incorrect";
    private static final String SOLUTION_SEPARATOR = "is ";
    private static final String REFRESH = "Refresh";
    private static final String ACTION_SPLITTER = " ";
    
    public static final int INITIAL = -1;
    public static final int CORRECT = -2;
    public static final int INCORRECT = -3;

    public Problem(Integer number, ProblemManager manager) {
        this.number = number;
        this.manager = manager;
        this.states = new HashMap<>();
        this.actions = new HashMap<>();
        this.graph = new SparseMultigraph<>();
        this.current = new State(this, manager.getCurrentStudent());
        this.states.put(this.current.toString(), this.current);
        this.maxState = 1;
        this.maxAction = 1;
        
        // Adding initial state to graph
        this.graph.addVertex(this.current);
    }
    
    public ActionParsedDTO handleAction(String action){
        String[] actionParam;
        ActionParsedDTO dto = null;
        if(action.startsWith(MAKE_COMMAND)){
            // Separating action and parameters
            action = action.replace(MAKE_COMMAND, "").trim();
            actionParam = action.split(ACTION_SPLITTER);
            switch(actionParam[0]){
                case MOVE:{
                    // State changes. Move n units
                    float distance = Util.getMoveUnits(actionParam[1]);
                    dto = move(action, distance);
                    break;
                }
                case TURN:{
                    // State changes. Turn n degrees
                    float angle = Util.getTurnUnits(actionParam[1]);
                    dto = turn(action, actionParam[1], angle);
                    break;
                }
            }
        } else if (action.startsWith(SOLUTION)){
            actionParam = action.split(SOLUTION_SEPARATOR);
            switch(actionParam[1]){
                case SOLUTION_CORRECT:
                    current.setAsCorrectAnswer(manager.getCurrentStudent());
                    break;
                case SOLUTION_INCORRECT:
                    current.setAsIncorrectAnswer(manager.getCurrentStudent());
                    break;
            }
        } else if (action.startsWith(REFRESH)){
            goBackToOrigin();
        }
        // Setting the problem number
        if(dto != null){
            dto.setProblem(number);
        }
        return dto;
    }
    
    private ActionParsedDTO move(String actionString, float units){
        return doAction(actionString, current.move(units, manager.getCurrentStudent()));
    }
    
    private ActionParsedDTO turn(String actionString, String original, float units){
        return doAction(actionString, current.turn(original, units, manager.getCurrentStudent()));
    }
    
    private ActionParsedDTO doAction(String actionString, State newState){
        ActionParsedDTO dto = new ActionParsedDTO(current);
        State newCurrent = states.get(newState.toString());
        // See if state exists
        if(newCurrent != null){
            // State exists. Increment counter
            newCurrent.incrementCounter(manager.getCurrentStudent());
        } else {
            // State doesn' exist. Set newCurrent
            newCurrent = newState;
            // Add to states hashmap
            states.put(newCurrent.toString(), newCurrent);
        }
        dto.setFinalState(newCurrent);
        // Add connection to graph and set the new current
        dto.setAction(addEdge(actionString, current, newCurrent));
        this.current = newCurrent;
        // Update max count
        if(!this.current.isInitialState() && this.current.getCount() > this.maxState){
            this.maxState = this.current.getCount();
        }
        return dto;
    } 
    
    public Action addEdge(String action, State s1, State s2){
        Action edge = new Action(action, s1, s2, this, manager.getCurrentStudent());
        Action retrieved = actions.get(edge.hashCode());
        if(retrieved != null){
            retrieved.incrementCounter(manager.getCurrentStudent());
        } else {
            retrieved = edge;
            actions.put(retrieved.hashCode(), retrieved);
            this.graph.addEdge(retrieved, s1, s2, EdgeType.DIRECTED);
        }
        if(retrieved.getCount() > maxAction){
            maxAction = retrieved.getCount();
        }
        return retrieved;
        
    }

    public Graph<State, Action> getGraph() {
        return graph;
    }

    public Integer getMaxState() {
        return maxState;
    }
    
    public Integer getMaxAction() {
        return maxAction;
    }
    
    public void goBackToOrigin(){
        State newState = states.get(new State(this, manager.getCurrentStudent()).toString()); // Getting initial state;
        // This will add edges to the origin when problem is restarted
        // addEdge("Restart ", this.current, newState);
        this.current = newState;
        this.current.incrementCounter(manager.getCurrentStudent()); // increment the counter
    }
    
}
