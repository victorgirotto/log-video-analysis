/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.mgb.gui;

import edu.asu.mgb.problem.Action;
import edu.asu.mgb.problem.State;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JOptionPane;

/**
 *
 * @author victorgirotto
 */
public class MouseUtil {

    public static DefaultModalGraphMouse getModalGraphMouse() {
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        return gm;
    }

    public static ItemListener getVertexListener(final PickedState pickedState) {
        return new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
            Object subject = e.getItem();
                // The graph uses Integers for vertices.
                if (subject instanceof State) {
                    State vertex = (State) subject;
                    if (pickedState.isPicked(vertex)) {
                        JOptionPane.showMessageDialog(null, vertex.toFullString());
                    } else {
                        System.out.println("Vertex " + vertex
                            + " no longer selected");
                    }
                }
            }
        };
    }

    public static ItemListener getEdgeVertexListener(final PickedState pickedAction) {
        return new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
            Object subject = e.getItem();
                // The graph uses Integers for vertices.
                if (subject instanceof Action) {
                    Action edge = (Action) subject;
                    if (pickedAction.isPicked(edge)) {
                        JOptionPane.showMessageDialog(null, edge.toFullString());
                    } else {
                        System.out.println("Edge " + edge
                            + " no longer selected");
                    }
                }
            }
        };
    }
    
    
    
}
