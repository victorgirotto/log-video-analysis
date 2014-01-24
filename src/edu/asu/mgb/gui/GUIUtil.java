/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.gui;

import edu.asu.mgb.problem.Action;
import edu.asu.mgb.problem.State;
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
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author victorgirotto
 */
public class GUIUtil {
    
    private static final Integer PADDING = 0;
    
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
    
    public static VisualizationViewer getGraphVisualizationViewerByStudent(Graph g, int student, int width, int height){
        Graph studentGraph;
        // Getting filters for students
        VertexPredicateFilter<State, Action> vertexFilter = GUIUtil.getVertexPredicateFilter(student);
        EdgePredicateFilter<State, Action> edgeFilter = GUIUtil.getEdgePredicateFilter(student);
        // Executing filter
        studentGraph = vertexFilter.transform(g);
        studentGraph = edgeFilter.transform(studentGraph);
        return getGraphVisualizationViewer(studentGraph, width, height, new IndividualTransformer(student));
    }
    
    public static VisualizationViewer getGraphVisualizationViewer(Graph g, int width, int height, GUITransformer t){
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
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
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
    
    
}
