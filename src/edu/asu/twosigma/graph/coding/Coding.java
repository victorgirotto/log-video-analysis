/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.asu.twosigma.graph.coding;

/**
 *
 * @author victorgirotto
 */
public class Coding {
 
    private Integer solutionCards = 0;
    private Integer horizontalProximity = 0;
    private Integer verticalProximity = 0;
    private Integer movementBeforeClick = 0;
    private Integer movementAfterClick = 0;
    
    private Integer totalSolutionCards = 0;
    private Integer totalHorizontalProximity = 0;
    private Integer totalVerticalProximity = 0;
    private Integer totalMovementBeforeClick = 0;
    private Integer totalMovementAfterClick = 0;

    public Coding(){}
    
    public Coding(String solutionCards, 
                    String verticalProximity, 
                  String horizontalProximity, 
                  String movementBeforeClick, 
                  String movementAfterClick) {
        
        this.add(
            CodingUtil.getCheatSheetValue(solutionCards), 
            CodingUtil.getHorizontalProximityValue(horizontalProximity), 
            CodingUtil.getVerticalProximityValue(verticalProximity), 
            CodingUtil.getMovementBeforeValue(movementBeforeClick), 
            CodingUtil.getMovementAfterValue(movementAfterClick)
        );
    }
    
    public Coding(Integer solutionCards, 
                  Integer horizontalProximity, 
                  Integer verticalProximity, 
                  Integer movementBeforeClick, 
                  Integer movementAfterClick) {
        
        this.add(
            solutionCards, 
            horizontalProximity, 
            verticalProximity, 
            movementBeforeClick, 
            movementAfterClick
        );
    }

    @Override
    public String toString() {
        return "Coding{" + 
                "\nsolutionCards=" + solutionCards + 
                "\n, horizontalProximity=" + horizontalProximity + 
                "\n, verticalProximity=" + verticalProximity + 
                "\n, movementBeforeClick=" + movementBeforeClick + 
                "\n, movementAfterClick=" + movementAfterClick +
                "\n, totalSolutionCards=" + totalSolutionCards + 
                "\n, totalHorizontalProximity=" + totalHorizontalProximity + 
                "\n, totalVerticalProximity=" + totalVerticalProximity + 
                "\n, totalMovementBeforeClick=" + totalMovementBeforeClick + 
                "\n, totalMovementAfterClick=" + totalMovementAfterClick + '}';
    }
    
    
    
    public void add(Integer solutionCards,
                    Integer horizontalProximity,
                    Integer verticalProximity,
                    Integer movementBeforeClick,
                    Integer movementAfterClick){
        if(solutionCards != null && solutionCards != 0){
            this.solutionCards += solutionCards;
            this.totalSolutionCards++;
        }
        if(horizontalProximity != null && horizontalProximity != 0){
            this.horizontalProximity += horizontalProximity;
            this.totalHorizontalProximity++;
        }
        if(verticalProximity != null && verticalProximity != 0){
            this.verticalProximity += verticalProximity;
            this.totalVerticalProximity++;
        }
        if(movementBeforeClick != null && movementBeforeClick != 0){
            this.movementBeforeClick += movementBeforeClick;
            this.totalMovementBeforeClick++;
        }
        if(movementAfterClick != null && movementAfterClick != 0){
            this.movementAfterClick += movementAfterClick;
            this.totalMovementAfterClick++;
        }
    }
    
    public void add(Coding c){ 
        this.add(
            c.solutionCards, 
            c.horizontalProximity, 
            c.verticalProximity, 
            c.movementBeforeClick, 
            c.movementAfterClick
        );
    }

    public Integer getSolutionCards() {
        if(totalSolutionCards == 0)
            return 0;
        return solutionCards / totalSolutionCards;
    }

    public Integer getHorizontalProximity() {
        if(totalHorizontalProximity == 0)
            return 0;
        return horizontalProximity / totalHorizontalProximity;
    }

    public Integer getVerticalProximity() {
        if(totalVerticalProximity == 0)
            return 0;
        return verticalProximity / totalVerticalProximity;
    }

    public Integer getMovementBeforeClick() {
        if(totalMovementBeforeClick == 0)
            return 0;
        return movementBeforeClick / totalMovementBeforeClick;
    }

    public Integer getMovementAfterClick() {
        if(totalMovementAfterClick == 0)
            return 0;
        return movementAfterClick / totalMovementAfterClick;
    }
    
    
    
}
