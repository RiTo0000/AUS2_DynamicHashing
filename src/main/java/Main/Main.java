/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import DynamicHashing.DynamicHashing;

/**
 *
 * @author namer
 */
public class Main {
    public static void main (String [] args) {
        DynamicHashing dh = new DynamicHashing("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\test.bin");
        
        dh.writeToFile();
        dh.readFromFile();
    }
}
