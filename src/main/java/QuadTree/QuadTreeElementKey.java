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
public abstract class QuadTreeElementKey {
    private Area space;
    private int key; //kluc manazovany priamo Quad stromom (nepristupny uzivatelovi)
    
    public QuadTreeElementKey(Area space) {
        this.space = space;
    }
    
    public Area getSpace() {
        return this.space;
    }

    public void setSpace(Area space) {
        this.space = space;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
    
    public abstract void PrintInfo();
}
