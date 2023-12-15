/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuadTree;

import java.util.Random;

/**
 *
 * @author namer
 */
public class Area {
    private Point start; // lavy dolny bod
    private Point end; // pravy horny bod
    
    public Area(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
    
    public Area[] devideTo4() {
        Area[] areas = new Area[4];
        
        Coordinate middle_x = get_middle_btw_2_coordinates(this.start.getX(), this.end.getX());
        Coordinate middle_y = get_middle_btw_2_coordinates(this.start.getY(), this.end.getY());
        
        areas[0] = new Area(new Point(this.start.getX(), middle_y),
                            new Point(middle_x, this.end.getY())); //NW
        
        areas[1] = new Area(new Point(middle_x, middle_y),
                            this.end); //NE
        
        areas[2] = new Area(new Point(middle_x, this.start.getY()),
                            new Point(this.end.getX(), middle_y)); //SE
        
        areas[3] = new Area(this.start,
                            new Point(middle_x, middle_y)); //SW
        
        return areas;
    }
    
    private Coordinate get_middle_btw_2_coordinates(Coordinate start, Coordinate end) {
        double val_start = start.getValue();
        Direction dir_start = start.getDirection();
        double val_end = end.getValue();
        Direction dir_end = end.getDirection();
        
        if (dir_start == dir_end) {
            return new Coordinate(dir_start, (val_start + val_end) / 2);   
        }
        else {
            if (val_start > val_end) { 
                return new Coordinate(dir_start, Math.abs(val_end - val_start)/2);
            }
            else {
                return new Coordinate(dir_end, Math.abs(val_end - val_start)/2);
            }
        } 
    }
    
    public boolean equals(Area Object) {
        Random rand = new Random();
        double smallNum = rand.nextDouble();
        smallNum /= 1000000.00;
        
        double real_start_X = this.start.getX().getRealValueOnAxis(); 
        double real_start_Y = this.start.getY().getRealValueOnAxis();
        double real_end_X = this.end.getX().getRealValueOnAxis();
        double real_end_Y = this.end.getY().getRealValueOnAxis();
        
        double real_Object_start_X = Object.start.getX().getRealValueOnAxis(); 
        double real_Object_start_Y = Object.start.getY().getRealValueOnAxis();
        double real_Object_end_X = Object.end.getX().getRealValueOnAxis();
        double real_Object_end_Y = Object.end.getY().getRealValueOnAxis();
        
        
        return (real_Object_start_X + smallNum >= real_start_X && real_Object_start_X - smallNum <= real_start_X &&
                real_Object_end_X + smallNum >= real_end_X && real_Object_end_X - smallNum <= real_end_X &&
                real_Object_start_Y + smallNum >= real_start_Y && real_Object_start_Y - smallNum <= real_start_Y &&
                real_Object_end_Y + smallNum >= real_end_Y && real_Object_end_Y - smallNum <= real_end_Y);
    }
    
    public boolean check_if_object_fits(Area Object) {
        double real_start_X = this.start.getX().getRealValueOnAxis(); 
        double real_start_Y = this.start.getY().getRealValueOnAxis();
        double real_end_X = this.end.getX().getRealValueOnAxis();
        double real_end_Y = this.end.getY().getRealValueOnAxis();
        
        double real_Object_start_X = Object.start.getX().getRealValueOnAxis(); 
        double real_Object_start_Y = Object.start.getY().getRealValueOnAxis();
        double real_Object_end_X = Object.end.getX().getRealValueOnAxis();
        double real_Object_end_Y = Object.end.getY().getRealValueOnAxis();
        
        if (real_Object_start_X >= real_start_X && 
                real_Object_end_X < real_end_X &&
                real_Object_start_Y >= real_start_Y &&
                real_Object_end_Y < real_end_Y) {
            return true;
        }
        else
            return false;
         
    }
    
    public boolean check_if_object_overlaps(Area Object) {
        double real_start_X = this.start.getX().getRealValueOnAxis(); 
        double real_start_Y = this.start.getY().getRealValueOnAxis();
        double real_end_X = this.end.getX().getRealValueOnAxis();
        double real_end_Y = this.end.getY().getRealValueOnAxis();
        
        double real_Object_start_X = Object.start.getX().getRealValueOnAxis(); 
        double real_Object_start_Y = Object.start.getY().getRealValueOnAxis();
        double real_Object_end_X = Object.end.getX().getRealValueOnAxis();
        double real_Object_end_Y = Object.end.getY().getRealValueOnAxis();
        
        
        if (real_Object_end_X < real_start_X ||
                real_Object_start_X > real_end_X ||
                real_Object_end_Y < real_start_Y ||
                real_Object_start_Y > real_end_Y) {
            return false;
        }
        else
            return true;
    }
}
