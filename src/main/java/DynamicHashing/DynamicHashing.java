/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicHashing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class DynamicHashing {
    private Node Root;
    private int BlockingFactorMain;
    private int BlockingFactorSecond;
    private String SecondFile;
    
    private RandomAccessFile mainFile;
    
    public DynamicHashing(String mainFilePath) {
        try {
            this.mainFile = new RandomAccessFile(mainFilePath, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void writeToFile() {
        BigInteger tst = BigInteger.valueOf(2147483646);
        try {
            this.mainFile.seek(0);
            this.mainFile.write(tst.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readFromFile() {
        byte[] bytes = new byte[5];
        try {
            this.mainFile.seek(0);
            
            this.mainFile.read(bytes);
            
            if (true) {
                
            }
        } catch (IOException ex) {
            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int test = ByteBuffer.wrap(bytes).getInt();
    }
    
    
    
    
}
