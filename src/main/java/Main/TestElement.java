/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import DynamicHashing.IRecord;
import java.nio.ByteBuffer;
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
        return BitSet.valueOf(new long[]{this.key % 7});
    }

    @Override
    public int getSize() {
        return 4; //lebo 4 byty
    }

    @Override
    public byte[] toByteArray() {
        return ByteBuffer.allocate(4).putInt(this.number).array();
    }

    @Override
    public void fromByteArray(byte[] input) {
        this.number = ByteBuffer.wrap(input).getInt();
    }
    
}
