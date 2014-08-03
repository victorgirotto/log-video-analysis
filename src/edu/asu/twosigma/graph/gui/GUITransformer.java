/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.gui;

import edu.asu.twosigma.graph.models.Action;
import edu.asu.twosigma.graph.models.State;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author victorgirotto
 */
public abstract class GUITransformer {
    
    public static final Integer MAX_VERTEX_SIZE = 50;
    
    public abstract Transformer<Action, Stroke> getEdgeStrokeTransformer();
    
    public abstract Transformer<State, Shape> getVertexSizeTransformer();
    
    public abstract Transformer<State, Paint> getVertexPaintTransformer();
    
    public Transformer<State, Stroke> getVertexStrokeTransformer(){
        return new Transformer<State, Stroke>() {
            final float dash[] = {1.0f};
            @Override
            public Stroke transform(State i) {
                if(i.isHighlighted()){
                    return new BasicStroke(5);
                } else {
                    return new BasicStroke();
                }
            }
        };
    }
    
}
