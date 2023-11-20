/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DynamicHashing;

/**
 *
 * @author namer
 */
public class ExternalNode extends Node {
    private int Address;
    private int Count;
    
    public ExternalNode() {
        this.Count = 0;
        this.Address = -1;
    }

    public int getAddress() {
        return this.Address;
    }

    public void setAddress(int Address) {
        this.Address = Address;
    }

    public int getCount() {
        return this.Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }
    
}
