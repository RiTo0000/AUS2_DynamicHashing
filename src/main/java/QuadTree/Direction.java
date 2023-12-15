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
public enum Direction {
    E, //vychod
    W, //zapad
    N, //sever
    S; //juh
    
    public static Direction getDirectFromString(String direction) {
        switch (direction){
            case "E":
                return Direction.E;
            case "W":
                return Direction.W;
            case "N":
                return Direction.N;
            case "S":
                return Direction.S;
            default:
                return null;
        }
    }
}
