/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import QuadTree.Area;
import QuadTree.QuadTreeElementKey;

/**
 *
 * @author namer
 */
public class Property extends QuadTreeElementKey {
    
    public Property(Area space, int key) {
        super(space);
        super.setKey(key);
    }

    @Override
    public void PrintInfo() {
        System.out.println("X_Start: " + this.getSpace().getStart().getX().getDirection() +
                                        this.getSpace().getStart().getX().getValue() + "; " + 
                                        "Y_Start: " + this.getSpace().getStart().getY().getDirection() +
                                        this.getSpace().getStart().getY().getValue() );
        System.out.println("X_End: " + this.getSpace().getEnd().getX().getDirection() +
                                        this.getSpace().getEnd().getX().getValue() + "; " + 
                                        "Y_End: " + this.getSpace().getEnd().getY().getDirection() +
                                        this.getSpace().getEnd().getY().getValue() );
    }
    
    public String[] getInfo() {
        int hlpNum;
        double startX;
        double endX;
        double startY;
        double endY;
        
        hlpNum = (int) (this.getSpace().getStart().getX().getValue() * 100.00);
        startX = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getStart().getY().getValue() * 100.00);
        startY = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getEnd().getX().getValue() * 100.00);
        endX = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getEnd().getY().getValue() * 100.00);
        endY = ((double)hlpNum)/100.00;
        
        String[] info = new String[2];
        
        info[0] = "X: " + this.getSpace().getStart().getX().getDirection() + " " + startX + 
                  " Y: " + this.getSpace().getStart().getY().getDirection() + " " + startY;
        info[1] = "X: " + this.getSpace().getEnd().getX().getDirection() + " " + endX + 
                  " Y: " + this.getSpace().getEnd().getY().getDirection() + " " + endY;
        
        return info;
    }
    
}
