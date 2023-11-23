/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import DynamicHashing.IRecord;
import java.util.BitSet;

/**
 *
 * @author namer
 */
public class TestElement implements IRecord{
    
    private int key;
    private int number;
    
    public TestElement(int key, int number) {
        this.key = key;
        this.number = number;
    }

    @Override
    public boolean equals(IRecord object) {
        //TODO
        return true;
    }

    @Override
    public BitSet getHash() {
        return BitSet.valueOf(new long[]{this.key % 7});
    }

    @Override
    public int getSize() {
        return 4; //lebo 4 byty
    }

    @Override
    public byte[] toByteArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fromByteArray(byte[] input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
