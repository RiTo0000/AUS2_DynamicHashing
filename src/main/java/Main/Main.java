/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import App.LandDH;
import DynamicHashing.DynamicHashing;
import QuadTree.Area;
import QuadTree.Coordinate;
import QuadTree.Direction;
import QuadTree.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class Main {
    public static void main (String [] args) {
        DynamicHashing<TestElement> dh = null;
        try {
            dh = new DynamicHashing<>("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test.bin", 
                    "C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test_second.bin", TestElement.class, 1, 1, 3);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        LandDH land = new LandDH(11, new Area(new Point(new Coordinate(Direction.E, 10), new Coordinate(Direction.E, 11)), 
//                                                        new Point(new Coordinate(Direction.E, 13), new Coordinate(Direction.E, 14))), 12, "tst krtky");
//        land.getProperties().add(13);
//        byte[] tst = land.toByteArray();
//        land.fromByteArray(tst);
        
        TestElement tst = new TestElement(1, 9674);
        TestElement tst2 = new TestElement(2, 7456);
        
        ArrayList<TestElement> insertedElements = new ArrayList<TestElement>();
        
        for (int i = 0; i < 10; i++) { 
            tst = new TestElement(i, i*100);
            try {
                dh.insert(tst);
                System.out.println("MainFile:");
                System.out.println(dh.readWholeMainFile());
                System.out.println("SecondFile:");
                System.out.println(dh.readWholeSecondFile());
                insertedElements.add(tst);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            tst = insertedElements.get(8);
            tst.setNumber(1234);
            System.out.println("MainFile:");
            System.out.println(dh.readWholeMainFile());
            System.out.println("SecondFile:");
            System.out.println(dh.readWholeSecondFile());
            dh.edit(tst);
            System.out.println("MainFile:");
            System.out.println(dh.readWholeMainFile());
            System.out.println("SecondFile:");
            System.out.println(dh.readWholeSecondFile());
////        for (int i = 0; i < 10; i++) {
////            tst2 = insertedElements.remove(0);
////            try {
////                dh.delete(tst2);
////                System.out.println("MainFile:");
////                System.out.println(dh.readWholeMainFile());
////                System.out.println("SecondFile:");
////                System.out.println(dh.readWholeSecondFile());
////            } catch (IOException ex) {
////                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (Exception ex) {
////                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////            }
////            
////        }
//        try {
//            dh.delete(new TestElement(2, 200));
//            System.out.println("MainFile:");
//            System.out.println(dh.readWholeMainFile());
//            System.out.println("SecondFile:");
//            System.out.println(dh.readWholeSecondFile());
//            
//            dh.delete(new TestElement(6, 600));
//            System.out.println("MainFile:");
//            System.out.println(dh.readWholeMainFile());
//            System.out.println("SecondFile:");
//            System.out.println(dh.readWholeSecondFile());
//            
//            dh.delete(new TestElement(4, 400));
//            System.out.println("MainFile:");
//            System.out.println(dh.readWholeMainFile());
//            System.out.println("SecondFile:");
//            System.out.println(dh.readWholeSecondFile());
////
////        for (int i = 0; i < 5; i++) {
////            tst = new TestElement(i, i*100);
////            try {
////                dh.insert(tst);
////                System.out.println(dh.readWholeMainFile());
////                insertedElements.add(tst);
////            } catch (IOException ex) {
////                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (Exception ex) {
////                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
//
//
//
////        try {
////            dh.insert(tst);
////            System.out.println(dh.readWholeFile());
////            dh.insert(tst2);
////            dh.find(tst);
////            System.out.println(dh.readWholeFile());
//////            dh.readFromFile();
////        } catch (IOException ex) {
////            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
