/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuadTree;

/**
 *
 * @author namer
 */
public class IntegerRef {
    private int value;
    
    public IntegerRef(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public int add(int valueToAdd) {
        this.value += valueToAdd;
        return this.value;
    }
    
    public int subtract(int valueToSubtract) {
        this.value -= valueToSubtract;
        return this.value;
    }
}
