/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import DynamicHashing.DynamicHashing;
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
        DynamicHashing<TestElement> dh = new DynamicHashing<>("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test.bin", TestElement.class, 1);
        
        TestElement tst = new TestElement(1, 9674);
        TestElement tst2 = new TestElement(2, 7456);
        
        ArrayList<TestElement> insertedElements = new ArrayList<TestElement>();
        
        for (int i = 0; i < 5; i++) {
            tst = new TestElement(i, i*100);
            try {
                dh.insert(tst);
                System.out.println(dh.readWholeFile());
                insertedElements.add(tst);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < 5; i++) {
            tst2 = insertedElements.remove(0);
            try {
                dh.delete(tst2);
                System.out.println(dh.readWholeFile());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        for (int i = 0; i < 5; i++) {
            tst = new TestElement(i, i*100);
            try {
                dh.insert(tst);
                System.out.println(dh.readWholeFile());
                insertedElements.add(tst);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
//        try {
//            dh.insert(tst);
//            System.out.println(dh.readWholeFile());
//            dh.insert(tst2);
//            dh.find(tst);
//            System.out.println(dh.readWholeFile());
////            dh.readFromFile();
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
