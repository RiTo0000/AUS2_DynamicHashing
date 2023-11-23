/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicHashing;

import java.util.BitSet;

/**
 *
 * @author namer
 */
public interface IRecord {
    
    public boolean equals(IRecord object);
    public BitSet getHash();
    public int getSize();
    public byte [] toByteArray();
    public void fromByteArray(byte[] input);
}
