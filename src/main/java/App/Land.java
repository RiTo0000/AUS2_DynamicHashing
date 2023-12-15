/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import QuadTree.Area;
import QuadTree.QuadTreeElementKey;
import java.util.ArrayList;

/**
 *
 * @author namer
 */
public class Land extends QuadTreeElementKey {
    private int landNumber;
    private String description;
    private ArrayList<Property> properties;
    
    public Land(Area space, int landNumber, String description) {
        super(space);
        this.landNumber = landNumber;
        this.description = description;
    }

    public int getLandNumber() {
        return this.landNumber;
    }

    public void setLandNumber(int landNumber) {
        this.landNumber = landNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }
    
    public boolean addProperty(Property property) {
        return this.properties.add(property);
    }
    
    /**
     * Metoda edituje neklucove prvky parcely (cislo parcely, popis)
     * @param landNumber nove cislo parcely
     * @param description novy popis
     */
    public void edit(int landNumber, String description) {
        this.setLandNumber(landNumber);
        this.setDescription(description);
    }
    
    @Override
    public void PrintInfo() {
        System.out.println("Land number: " + this.landNumber + " description: " + this.description);
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
        
        String[] info = new String[4];
        
        info[0] = Integer.toString(this.landNumber);
        info[1] = this.description;
        info[2] = "X: " + this.getSpace().getStart().getX().getDirection() + " " + startX + 
                  " Y: " + this.getSpace().getStart().getY().getDirection() + " " + startY;
        info[3] = "X: " + this.getSpace().getEnd().getX().getDirection() + " " + endX + 
                  " Y: " + this.getSpace().getEnd().getY().getDirection() + " " + endY;
        
        return info;
    }
}
