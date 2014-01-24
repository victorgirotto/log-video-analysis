/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.gui;

import edu.asu.mgb.problem.Action;
import edu.asu.mgb.problem.Problem;
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
public class AggregateTransformer extends GUITransformer {
    
    private static final Integer HIGHLIGHT_STROKE = 5;
    
    @Override
    public Transformer<Action, Stroke> getEdgeStrokeTransformer() {
        return new Transformer<Action, Stroke>() {
            final float dash[] = {1.0f};
            
            @Override
            public Stroke transform(Action s) {
                if(s.isHighlighted()){
                    return new BasicStroke(HIGHLIGHT_STROKE);
                } else {
                    return new BasicStroke(1.0f + ((float)10.0 * s.getNormalizedCount()), BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 
                                       10.0f, dash, 0.0f);
                }
            }
        };
    }

    @Override
    public Transformer<State, Shape> getVertexSizeTransformer() {
        return new Transformer<State,Shape>(){
            @Override
            public Shape transform(State state){
                if(state.isInitialState()){
                    return new Ellipse2D.Double(MAX_VERTEX_SIZE/-2, MAX_VERTEX_SIZE/-2, MAX_VERTEX_SIZE, MAX_VERTEX_SIZE);
                }
                float size = (MAX_VERTEX_SIZE/2) + ((MAX_VERTEX_SIZE/2) * state.getRawNormalizedIntensity());
                return new Ellipse2D.Double(size / -2, size / -2, size, size);
//                return new Ellipse2D.Double(-30, -30, 60, 60);
            }
        };
    }

    @Override
    public Transformer<State, Paint> getVertexPaintTransformer() {
        return new Transformer<State,Paint>() {
            @Override
            public Paint transform(State state) {
                float ratio;
                float intensity = state.getNormalizedIntensity();
                if(intensity == Problem.INITIAL){
                    // Initial position
                    return Color.BLUE;    
                } else if(intensity == Problem.CORRECT) {
                    ratio = state.getCorrectRatio();
                    return new Color(1-ratio, 1, 1-ratio);
                } else if(intensity == Problem.INCORRECT) {
                    ratio = state.getWrongRatio();
                    return new Color(1, 1-ratio, 1-ratio);
                } else {
                    return Color.WHITE;
                }
        } };
    }
    
}
