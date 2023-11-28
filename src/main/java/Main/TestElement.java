/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import DynamicHashing.IRecord;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/**
 *
 * @author namer
 */
public class TestElement implements IRecord{
    
    private int key;
    private int number;
    
    public TestElement() {
    }
    
    public TestElement(int key, int number) {
        this.key = key;
        this.number = number;
    }

    @Override
    public boolean equals(IRecord object) {
        TestElement element = (TestElement) object;
        return this.key == element.key;
    }

    @Override
    public BitSet getHash() {
        return BitSet.valueOf(new long[]{this.key});
    }

    @Override
    public int getSize() {
        return 8; //lebo 2*4 byty
    }

    @Override
    public byte[] toByteArray() {
        byte[] result = new byte[this.getSize()];
        byte[] tmp;
        int index = 0;
        
        tmp = ByteBuffer.allocate(4).putInt(this.key).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        tmp = ByteBuffer.allocate(4).putInt(this.number).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        return result;
    }

    @Override
    public void fromByteArray(byte[] input) {
        byte[] tmp = Arrays.copyOfRange(input, 0, 4);
        this.key = ByteBuffer.wrap(tmp).getInt();
        
        tmp = Arrays.copyOfRange(input, 4, 8);
        this.number = ByteBuffer.wrap(tmp).getInt();
    }

    @Override
    public TestElement createClass() {
        return new TestElement();
    }

    @Override
    public String recordToString() {
        String result = Integer.toString(this.key) + " " + 
                        Integer.toString(this.number) + " ";
        
        return result;
    }
    
}
