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
public class Point {
    private Coordinate x;
    private Coordinate y;

    public Point(Coordinate x, Coordinate y) {
        this.x = x;
        this.y = y;
    }
    
    public Coordinate getX() {
        return x;
    }

    public Coordinate getY() {
        return y;
    }    
    
}
