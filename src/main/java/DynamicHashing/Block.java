/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicHashing;

import Main.TestElement;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class Block <T extends IRecord> {
    
    private int Address;
    private Block nextBlock;
    private ArrayList<T> records;
//    private T [] records;
    private int validCount;
    
    public Block(int blockingFactor) {
//        this.records = new T [ blockingFactor];
        this.records = new ArrayList<>();
    }
    
    public int getSize(){
        //TODO
        return 0;    
    }
    
    public void insert(T insertedRecord) {
//        for (IRecord record : records) {
//            if (record == null) {
//                record = insertedRecord;
//                break;
//            }
//        }
        this.records.add(insertedRecord);
    }
    
    public byte [] toByteArray(int blockingFactor) {
        byte [] recordOutput = null;
        
        if (!this.records.isEmpty()) {
            recordOutput = new byte[blockingFactor * this.records.get(0).getSize()];
            //TODO 
            for (T record : records) {
                if (record != null) {
                    recordOutput = record.toByteArray();
                }
            }
        }
        return recordOutput;
    }
    public void fromByteArray(byte[] input){
        
        for (T record : this.records) {
            record = (T) new TestElement(); //TODO docastne lebo neviem ako spravit genericky
            record.fromByteArray(input);
        }
        
        T record = (T) new TestElement(); //TODO docastne lebo neviem ako spravit genericky
        record.fromByteArray(input);
        
        this.records.add(record);
    }

    public int getAddress() {
        return this.Address;
    }

    public void setAddress(int Address) {
        this.Address = Address;
    }

    public Block getNextBlock() {
        return this.nextBlock;
    }

    public void setNextBlock(Block nextBlock) {
        this.nextBlock = nextBlock;
    }

    public ArrayList<T> getRecords() {
        return this.records;
    }    
    

}
