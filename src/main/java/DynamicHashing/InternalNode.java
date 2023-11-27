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
public class InternalNode extends Node{
    private Node Left;
    private Node Right;
    
    public InternalNode(InternalNode parent) {
        super.setParent(parent);
    }

    public Node getLeft() {
        return this.Left;
    }

    public void setLeft(Node Left) {
        this.Left = Left;
    }

    public Node getRight() {
        return this.Right;
    }

    public void setRight(Node Right) {
        this.Right = Right;
    }

    @Override
    public boolean isExternal() {
        return false;
    }
    
    
}
