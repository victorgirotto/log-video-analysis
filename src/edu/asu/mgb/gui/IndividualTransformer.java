/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.gui;

import edu.asu.mgb.problem.Action;
import edu.asu.mgb.problem.State;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author victorgirotto
 */
public class IndividualTransformer extends GUITransformer{

    private int student;

    public IndividualTransformer(int student) {
        this.student = student;
    }
    
    @Override
    public Transformer<Action, Stroke> getEdgeStrokeTransformer() {
        return new Transformer<Action, Stroke>() {
            final float dash[] = {1.0f};
            
            @Override
            public Stroke transform(Action s) {
                return new BasicStroke(1.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 
                                       10.0f, dash, 0.0f);
            }
        };
    }

    @Override
    public Transformer<State, Shape> getVertexSizeTransformer() {
        return new Transformer<State, Shape>() {

            @Override
            public Shape transform(State i) {
                return new Ellipse2D.Double(MAX_VERTEX_SIZE/-2, MAX_VERTEX_SIZE/-2, MAX_VERTEX_SIZE, MAX_VERTEX_SIZE);
            }
        };
    }

    @Override
    public Transformer<State, Paint> getVertexPaintTransformer() {
        return new Transformer<State,Paint>() {
            @Override
            public Paint transform(State state) {
                boolean isInCorrect = state.isStudentCorrect(student);
                boolean isInWrong = state.isStudentWrong(student);
                
                if(state.isInitialState()){
                    return Color.BLUE;
                } else if(isInCorrect){
                    return Color.GREEN;
                } else if(isInWrong){
                    return Color.RED;
                } else {
                    return Color.WHITE;
                }
        } };
    }
    
}
