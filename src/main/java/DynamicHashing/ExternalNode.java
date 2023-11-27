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
    
    public ExternalNode(InternalNode parent) {
        this.Count = 0;
        this.Address = -1;
        super.setParent(parent);
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

    @Override
    public boolean isExternal() {
        return true;
    }
    
}
