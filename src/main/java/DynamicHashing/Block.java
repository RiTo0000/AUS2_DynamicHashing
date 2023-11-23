/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicHashing;

import java.nio.ByteBuffer;

/**
 *
 * @author namer
 */
public class Block {
    private IRecord [] records;
    private int validCount;
    
    public Block() {
        
    }
    
    public int getSize(){
        //TODO
        return 0;    
    } 
    
    public byte [] toByteArray() {
        //TODO 
        return null;
    }
    public void fromByteArray(byte[] input){
        
        int i = ByteBuffer.wrap(input).getInt();
        if (true) {
            
        }
    }
}
