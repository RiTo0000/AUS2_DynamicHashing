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
    private static final int blockInfoSize = Long.BYTES + Long.BYTES + Integer.BYTES + 1;
    
    private long Address;
    private long nextBlockAddress;
    private boolean nextBlockInSecondFile;
    private long previousBlockAddress;
    private ArrayList<T> records;
    private int BlockingFactor;
    /**
     * Pocet platnych bitov v bloku
     */
    private int validCount;
    
    private Class<T> classType;
    
    public Block(long Address, int blockingFactor, Class<T> classType) {
        this.Address = Address;
        this.nextBlockAddress = -1;
        this.nextBlockInSecondFile = false;
        this.previousBlockAddress = -1;
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
    
    public void editRecord(int index, T record) {
        this.records.set(index, record);
    }
    
    public byte [] toByteArray() {
        
        byte[] result = new byte[this.getSize()];
        byte[] tmp;
        int index = 0;
        
        tmp = ByteBuffer.allocate(Long.BYTES).putLong(this.previousBlockAddress).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        tmp = ByteBuffer.allocate(Long.BYTES).putLong(this.nextBlockAddress).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        result[index] = (byte)(this.nextBlockInSecondFile ? 1 : 0);
        index++;

        
        tmp = ByteBuffer.allocate(Integer.BYTES).putInt(this.validCount).array();
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
    public void fromByteArray(byte[] input){
        byte[] tmp;
        int startIndex = 0;
        T record;
        
        //nacitanie riadiacich zaznamov bloku
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + Long.BYTES);
        this.previousBlockAddress = ByteBuffer.wrap(tmp).getLong();
        startIndex += Long.BYTES;
        
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + Long.BYTES);
        this.nextBlockAddress = ByteBuffer.wrap(tmp).getLong();
        startIndex += Long.BYTES;
        
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + 1);
        this.nextBlockInSecondFile = tmp[0]!=0;
        startIndex += 1;
        
        tmp = Arrays.copyOfRange(input, startIndex, startIndex + Integer.BYTES);
        this.validCount = ByteBuffer.wrap(tmp).getInt();
        startIndex += Integer.BYTES;
        
        if (this.validCount <= startIndex) {
            return;
        }
        
        for (int i = 0; i < this.BlockingFactor; i++) {
            
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
        String result = Long.toString(this.previousBlockAddress) + " " +
                        Long.toString(this.nextBlockAddress) + " " + 
                        Boolean.toString(this.nextBlockInSecondFile) + " " +
                        Integer.toString(this.validCount) + " ";
        
        for (T record : this.records) {
            result += record.recordToString();
        }
        
        result += "\n";
        
        return result;
    }

    public long getAddress() {
        return this.Address;
    }

    public void setAddress(long Address) {
        this.Address = Address;
    }

    public long getNextBlockAddress() {
        return this.nextBlockAddress;
    }

    public void setNextBlockAddress(long nextBlockAddress) {
        this.nextBlockAddress = nextBlockAddress;
    }

    public long getPreviousBlockAddress() {
        return this.previousBlockAddress;
    }

    public void setPreviousBlockAddress(long previousBlockAddress) {
        this.previousBlockAddress = previousBlockAddress;
    }

    public boolean isNextBlockInSecondFile() {
        return this.nextBlockInSecondFile;
    }

    public void setNextBlockInSecondFile(boolean nextBlockInSecondFile) {
        this.nextBlockInSecondFile = nextBlockInSecondFile;
    }
    
    /**
     * Vrati info ci je blok prazdny (obsahuje iba informacne zaznamy o bloku)
     * @param secondFile true ak ide o blok v preplnujucom subore
     * @return true ak je blok prazdny, false inak
     */
    public boolean isEmpty() {
        return this.validCount == blockInfoSize && !this.nextBlockInSecondFile; //v hlavnom subore nie je prazdny ak ma vazbu na preplujuci
    }
    
    /**
     * Vrati info ci je blok plny
     * @return true ak je blok plny, false inak
     */
    public boolean isFull() {
        return this.records.size() == BlockingFactor;
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
    
    /**
     * Vycisti zoznam zaznamov
     */
    public void clearRecords() {
        this.records.clear();
        this.validCount = blockInfoSize;
    }
}
