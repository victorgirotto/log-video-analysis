/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.gui;

import edu.asu.twosigma.graph.models.Action;
import edu.asu.twosigma.graph.models.State;
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author victorgirotto
 */
public class GUIUtil {
    
    private static final Integer PADDING = 0;
    public static Transformer defaultLabelTransformer;
    public static Transformer stringLabelTransformer;
    
    static {
        defaultLabelTransformer = new Transformer() {
            @Override
            public String transform(Object i) {
                return "";
            }
        };
        
        stringLabelTransformer = new ToStringLabeller();
    }
    
    /**
     * Obtains the vertex filter by student;
     * @param student
     * @return 
     */
    public static VertexPredicateFilter getVertexPredicateFilter(final int student){
        return new VertexPredicateFilter(new Predicate<State>() {
            @Override
            public boolean evaluate(State s) {
                return s.isStudentInState(student);
            }
        });
    }
    
    /**
     * Obtains the edge filter by student
     * @param student
     * @return 
     */
    public static EdgePredicateFilter getEdgePredicateFilter(final int student){
        return new EdgePredicateFilter<>(new Predicate<Action>() {
            @Override
            public boolean evaluate(Action a) {
                return a.isStudentInACtion(student);
            }
        });
    }

    /**
     * Obtain the graph layout
     * @param g
     * @return 
     */
    public static Layout<State, Action> getLayout(Graph g) {
        //return new ISOMLayout(g);
        //return new SpringLayout<>(g);
        //return new FRLayout<>(g);
        //return new CircleLayout<>(g);
        return new KKLayout<>(g);
    }
    
    public static VisualizationViewer getGraphVisualizationViewerByStudent(Graph g, Transformer t, int student, int width, int height){
        Graph studentGraph;
        // Getting filters for students
        VertexPredicateFilter<State, Action> vertexFilter = GUIUtil.getVertexPredicateFilter(student);
        EdgePredicateFilter<State, Action> edgeFilter = GUIUtil.getEdgePredicateFilter(student);
        // Executing filter
        studentGraph = vertexFilter.transform(g);
        studentGraph = edgeFilter.transform(studentGraph);
        return getGraphVisualizationViewer(studentGraph, t, width, height, new IndividualTransformer(student));
    }
    
    public static VisualizationViewer getGraphVisualizationViewer(Graph g, Transformer labelTransformer, int width, int height, GUITransformer t){
        if(t == null){
            t = new AggregateTransformer();
        }

        // Getting transformers for line
        Transformer<Action, Stroke> edgeStrokeTransformer = t.getEdgeStrokeTransformer();
        // Getting transformers for edges
        Transformer<State,Shape> vertexSize = t.getVertexSizeTransformer();
        Transformer<State,Paint> vertexPaint = t.getVertexPaintTransformer();
        Transformer<State,Stroke> vertexStroke = t.getVertexStrokeTransformer();
        
        // Setting layout
        Layout<State, Action> layout = GUIUtil.getLayout(g);
        layout.setSize(new Dimension(width - PADDING, height - PADDING)); // sets the initial size of the space
        VisualizationViewer<State,Action> vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(width, height)); //Sets the viewing area size
        vv.setSize(new Dimension(width, height));
        
        // Setting the transformers and appearance of edges and vertices
        vv.getRenderContext().setEdgeFillPaintTransformer(new Transformer<Action, Paint>() {

            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getSolutionCards()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        vv.getRenderContext().setVertexLabelTransformer(defaultLabelTransformer);
        vv.getRenderContext().setEdgeLabelTransformer(defaultLabelTransformer);
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
        vv.getRenderContext().setVertexStrokeTransformer(vertexStroke);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        
        // Mouse operations
        DefaultModalGraphMouse gm = MouseUtil.getModalGraphMouse();
        vv.setGraphMouse(gm);
        vv.addKeyListener(gm.getModeKeyListener()); 
        
        // Creating picked states for vertices and edges
        PickedState<State> pickedState = vv.getPickedVertexState();
        PickedState<Action> pickedAction = vv.getPickedEdgeState();
        
        // Attaching the listeners
        pickedState.addItemListener(MouseUtil.getVertexListener(pickedState));
        pickedAction.addItemListener(MouseUtil.getEdgeVertexListener(pickedAction));
        
        return vv;
    }

    public static Transformer[] getVisualizationTransformerList() {
        List<Transformer> list = new ArrayList<>();
        
        // Default
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "None";
            }            
            @Override
            public Paint transform(Action i) {
                return new Color(0, 0, 0, 0);
            }
        });
        // Solution cards
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "Solution card";
            }
            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getSolutionCards()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        // Horizontal distance
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "Horizontal Distance";
            }
            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getHorizontalProximity()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        // Vertical distance
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "Vertical Distance";
            }
            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getVerticalProximity()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        // Movement before click
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "Movement before click";
            }
            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getMovementBeforeClick()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        // Movement after click
        list.add(new Transformer<Action, Paint>() {
            @Override
            public String toString() {
                return "Movement after click";
            }
            @Override
            public Paint transform(Action i) {
                Integer total = i.getCoding().getMovementAfterClick()*63;
                return new Color(255-total, 255, 255-total);
            }
        });
        return list.toArray(new Transformer[list.size()]);
    }
    
    
}
