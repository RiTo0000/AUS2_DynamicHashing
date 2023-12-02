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
    
    /**
     * Velkost v subore ktoru zaberaju riadiace zaznamy
     */
    private static final int blockInfoSize = 12;
    
    private int Address;
    private int nextBlockAddress;
    private int previousBlockAddress;
    private ArrayList<T> records;
    private int BlockingFactor;
    /**
     * Pocet platnych bitov v bloku
     */
    private int validCount;
    
    private Class<T> classType;
    
    public Block(int Address, int blockingFactor, Class<T> classType) {
        this.Address = Address;
        this.BlockingFactor = blockingFactor;
        this.records = new ArrayList<>();
        this.classType = classType;
        this.validCount = blockInfoSize; //pociatocna hodnota blockInfoSize lebo taku velkost potrebuju riadiace zaznamy
    }
    
    public int getSize(){
        T dummyRecord;
        try {
            dummyRecord = (T) classType.newInstance().createClass();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        //blocking factor * dummyRecord.getSize() + velkost objektov riadiacich na zaciatku
        return this.BlockingFactor * dummyRecord.getSize() + blockInfoSize;    
    }
    
    public void insert(T insertedRecord) {
        this.records.add(insertedRecord);
        this.validCount += insertedRecord.getSize();
    }
    
    public byte [] toByteArray(int blockingFactor) {
        
        byte[] result = new byte[this.getSize()];
        byte[] tmp;
        int index = 0;
        
        tmp = ByteBuffer.allocate(4).putInt(this.previousBlockAddress).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        tmp = ByteBuffer.allocate(4).putInt(this.nextBlockAddress).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        tmp = ByteBuffer.allocate(4).putInt(this.validCount).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        if (!this.records.isEmpty()) {
            for (T record : this.records) {
                if (record != null) {
                    tmp = record.toByteArray();
                    for (byte b : tmp) {
                        result[index] = b;
                        index++;
                    }
                }
            }
        }
        return result;
    }
    public void fromByteArray(byte[] input, int blockingFactor){
        byte[] tmp;
        int startIndex = 0;
        T record;
        
        //nacitanie riadiacich zaznamov bloku
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + 4);
        this.previousBlockAddress = ByteBuffer.wrap(tmp).getInt();
        startIndex += 4;
        
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + 4);
        this.nextBlockAddress = ByteBuffer.wrap(tmp).getInt();
        startIndex += 4;
        
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + 4);
        this.validCount = ByteBuffer.wrap(tmp).getInt();
        startIndex += 4;
        
        if (this.validCount <= startIndex) {
            return;
        }
        
        for (int i = 0; i < blockingFactor; i++) {
            
            try {
                record = (T) classType.newInstance().createClass();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            tmp = Arrays.copyOfRange(input, startIndex, startIndex + record.getSize());
            record.fromByteArray(tmp);
            this.records.add(record);
            
            startIndex += record.getSize();
            
            if (this.validCount <= startIndex) {
                return;
            }
        }
    }
    
    public String blockToString() {
        String result = Integer.toString(this.previousBlockAddress) + " " +
                        Integer.toString(this.nextBlockAddress) + " " + 
                        Integer.toString(this.validCount) + " ";
        
        for (T record : this.records) {
            result += record.recordToString();
        }
        
        result += "\n";
        
        return result;
    }

    public int getAddress() {
        return this.Address;
    }

    public void setAddress(int Address) {
        this.Address = Address;
    }

    public int getNextBlockAddress() {
        return this.nextBlockAddress;
    }

    public void setNextBlockAddress(int nextBlockAddress) {
        this.nextBlockAddress = nextBlockAddress;
    }

    public int getPreviousBlockAddress() {
        return this.previousBlockAddress;
    }

    public void setPreviousBlockAddress(int previousBlockAddress) {
        this.previousBlockAddress = previousBlockAddress;
    }
    
    

    public ArrayList<T> getRecords() {
        return this.records;
    }  
    
    /**
     * Metoda na spravne odstranenie zaznamu z bloku
     * @param record zaznam na odstranenie
     * @return true ak sa odstranenie zanzamu podarilo, false ak nie
     */
    public boolean removeRecord(T record) {
        boolean result;
        
        result = this.records.remove(record); //odstranenie zaznamu zo zoznamu zaznamov
        
        if (result) 
            this.validCount -= record.getSize(); //Znizenie validCountu
        
        return result;
    }
    

}
