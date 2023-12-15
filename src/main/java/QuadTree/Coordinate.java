/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuadTree;

/**
 *
 * @author namer
 */
public class Coordinate {
    private Direction direction; // (E, W, S, N)
    private double value; // pozicia
    
    public Coordinate(Direction direction, double value) {
        this.direction = direction;
        this.value = value;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getValue() {
        return value;
    }
    
    public double getRoundedValue(int numOfDecimalPlaces) {
        double scale = Math.pow(10, numOfDecimalPlaces);
        return Math.round(this.value * scale) / scale;  
    }
    
    public double getRealValueOnAxis() {
        if (this.direction == Direction.W || this.direction == Direction.S){
            return this.value * -1;
        }
        else {
            return this.value;
        }
    }
    
    public static Coordinate getCoordinateFromRealValueOnAxis(boolean isAxisX, double realValue) {
        Coordinate coordinate; 
        
        if (realValue < 0) { //zaporna hodnota
            if (isAxisX) 
                coordinate = new Coordinate(Direction.W, realValue * -1);
            else
                coordinate = new Coordinate(Direction.S, realValue * -1);
        }
        else { // kladna hodnota
            if (isAxisX) 
                coordinate = new Coordinate(Direction.E, realValue);
            else
                coordinate = new Coordinate(Direction.N, realValue);
        }
        return coordinate;
    }
    
    
}
