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
public class Property extends QuadTreeElementKey {
    private int regNumber;
    private String description;
    private ArrayList<Land> lands;
    
    public Property(Area space, int regNumber, String description) {
        super(space);
        this.regNumber = regNumber;
        this.description = description;
    }

    public int getRegNumber() {
        return this.regNumber;
    }

    public void setRegNumber(int regNumber) {
        this.regNumber = regNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Land> getLands() {
        return this.lands;
    }

    public void setLands(ArrayList<Land> lands) {
        this.lands = lands;
    }
    
    public boolean addLand(Land land) {
        return this.lands.add(land);
    }
    
    /**
     * Metoda edituje neklucove prvky nehnutelnosti (supisne cislo, popis)
     * @param regNumber nove supisne cislo
     * @param description novy popis
     */
    public void edit(int regNumber, String description) {
        this.setRegNumber(regNumber);
        this.setDescription(description);
    }

    @Override
    public void PrintInfo() {
        System.out.println("Property number: " + this.regNumber + " description: " + this.description);
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
        
        info[0] = Integer.toString(this.regNumber);
        info[1] = this.description;
        info[2] = "X: " + this.getSpace().getStart().getX().getDirection() + " " + startX + 
                  " Y: " + this.getSpace().getStart().getY().getDirection() + " " + startY;
        info[3] = "X: " + this.getSpace().getEnd().getX().getDirection() + " " + endX + 
                  " Y: " + this.getSpace().getEnd().getY().getDirection() + " " + endY;
        
        return info;
    }
    
}
