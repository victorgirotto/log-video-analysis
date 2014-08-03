/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.tool.models;

import edu.asu.twosigma.tool.*;
import edu.asu.twosigma.graph.models.Action;
import edu.asu.twosigma.graph.models.State;

/**
 *
 * @author Elissa
 */
public class LogMessage {
    
    private String timestamp;
    private String action;
    private Action edge;
    private State vertex;
    
   public LogMessage(String ts, String a, Action e, State v) {
       timestamp = ts;
       action = a;
       edge = e;
       vertex = v;
    }
   
   public Action getEdge() {
       return edge;
   }
   
   public State getVertex() {
       return vertex;
   }   
    
}
