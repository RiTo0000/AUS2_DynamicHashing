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
    private long Address;
    private int Count;
    
    private int numOfBlocksInExtFile;
    
    public ExternalNode(InternalNode parent) {
        this.Count = 0;
        this.Address = -1;
        this.numOfBlocksInExtFile = 0;
        super.setParent(parent);
    }

    public long getAddress() {
        return this.Address;
    }

    public void setAddress(long Address) {
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

    public int getNumOfBlocksInExtFile() {
        return this.numOfBlocksInExtFile;
    }

    public void setNumOfBlocksInExtFile(int numOfBlocksInExtFile) {
        this.numOfBlocksInExtFile = numOfBlocksInExtFile;
    }
    
    
    
}
