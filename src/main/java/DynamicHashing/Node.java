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
public abstract class Node {
    private InternalNode parent;
    
    public abstract boolean isExternal();

    public InternalNode getParent() {
        return parent;
    }

    public void setParent(InternalNode parent) {
        this.parent = parent;
    }
}
