/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.tool.models;

import edu.asu.twosigma.tool.*;

/**
 *
 * @author Elissa
 */
public class Annotation {
    private String solutionCards;
    private String verticalProximity;
    private String horizontalProximity;
    private String preClickMovement;
    private String postClickMovement;
    
    public Annotation(String s, String vp, String hp, String pre, String post)
    {
        solutionCards = s;
        verticalProximity = vp;
        horizontalProximity = hp;
        preClickMovement = pre;
        postClickMovement = post;
    }
}
