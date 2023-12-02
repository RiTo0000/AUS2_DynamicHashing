/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package OperationGeneratorTester;

import DynamicHashing.DynamicHashing;
import Main.TestElement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class OperationGeneratorTester {
    
    public static int numOfOperations = 100;
    public static int numOfInitialInserts = 10;
    
    public static void main (String[] args) {
        
        Random rand = new Random();
        double randNum;
        
        double startXVal;
        double endXVal;
        double startYVal;
        double endYVal;
        double hlpVal;
        TestElement tst;
        TestElement foundElement;
        ArrayList<TestElement> foundElements;
        ArrayList<TestElement> insertedElements = new ArrayList<TestElement>();
        
        DynamicHashing<TestElement> dh = null;
        try {
            dh = new DynamicHashing<>("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test.bin", 
                                        "C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test_second.bin", TestElement.class, 1, 1, 3);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        for (int i = 0; i < numOfInitialInserts; i++) {
            tst = new TestElement(i, i*100);
            try {
                dh.insert(tst);
                insertedElements.add(tst);
            } catch (IOException ex) {
                Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            System.out.println("Po uvodnom inserte:");
            System.out.println(dh.readWholeMainFile());
            
            for (int i = 0; i < numOfOperations; i++) {
                System.out.print("Operation num: " + i);
                randNum = rand.nextDouble();
                if ( randNum <= 0.2) { //insert
                    System.out.println(" operation insert");
                    tst = new TestElement((numOfInitialInserts+i), ((numOfInitialInserts+i)*100));
                    try {
                        dh.insert(tst);
                        insertedElements.add(tst);
                    } catch (IOException ex) {
                        Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    System.out.println(dh.readWholeMainFile());
                }
                else if ( randNum > 0.5 && randNum <= 0.8) { //delete
                    System.out.println(" operation delete");
                    if (!insertedElements.isEmpty()) {
                        tst = insertedElements.remove(rand.nextInt(insertedElements.size()));
                        if (!dh.delete(tst)) {
                            System.out.println("Operacia nejako zle prebehla");
                        }
                    }
                    
                    System.out.println(dh.readWholeMainFile());
                }   
                else { //find
                    System.out.println(" operation find");
                    if (!insertedElements.isEmpty()) {
                        tst = insertedElements.get(rand.nextInt(insertedElements.size()));
                        foundElement = dh.find(tst);

                        if (foundElement == null) {
                            System.out.println("Operacia nejako zle prebehla");
                        }
                        else {
                            if (!tst.equals(foundElement)) {
                                System.out.println("Operacia nejako zle prebehla");
                            }
                        }
                    }
                }
            } 
            
        } catch (IOException ex) {
            Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OperationGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
