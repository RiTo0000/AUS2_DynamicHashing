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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class DynamicHashing <T extends IRecord> {
    private Node Root;
    private int BlockingFactorMain;
    private int BlockingFactorSecond;
    private String SecondFile;
    
    private Block freeMainBlock;
    
    private RandomAccessFile mainFile;
    
    public DynamicHashing(String mainFilePath) {
        this.Root = new ExternalNode(null);
        this.BlockingFactorMain = 1;
        try {
            this.mainFile = new RandomAccessFile(mainFilePath, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private int getFreeBlockAddress() throws IOException {
        int freeBlockAddress = 0;
        if (this.freeMainBlock == null) {
            freeBlockAddress = (int) this.mainFile.length();
        }
        else {
            freeBlockAddress = this.freeMainBlock.getAddress();
            this.freeMainBlock = this.freeMainBlock.getNextBlock();
        }
        return freeBlockAddress;
    }
    
    public void insert(T element) {
        
    }
    
    private ExternalNode findNode(BitSet hash) throws IOException {
        Node actualNode = this.Root;
        int actualLvl = 0;
        boolean found = false;
        
        while(!found) {
            if (actualNode.isExternal()) { //ak je externa tak sa pokusim insertnut tam 
                if (((ExternalNode) actualNode).getCount() < this.BlockingFactorMain ) { //este sa vojde sem do nodu
                    found = true;
                }
                else {
                    InternalNode newParent = new InternalNode(actualNode.getParent());
                    actualNode.setParent(newParent);
                    if (hash.get(actualLvl) == true) { //pojde do prava lebo je 1
                        newParent.setRight(actualNode);
                        newParent.setLeft(new ExternalNode(newParent));
                    }
                    else {
                        newParent.setLeft(actualNode);
                        newParent.setRight(new ExternalNode(newParent));
                    }
                    Block block = this.readFromFile(((ExternalNode) actualNode).getAddress());
                    ArrayList<T> records = block.getRecords();
                    for ( T record : records) {
                        //TODO prejst objekty a skontrolovat kam maju ist sa presuvat ci vlavo alebo vpravo podla ich hashu kluca
                    }
                    
                    //nastavim novy actualNode
                    if (hash.get(actualLvl) == true) { //pojde do prava lebo je 1
                        actualNode = ((InternalNode) actualNode).getRight();
                    }
                    else {
                        actualNode = ((InternalNode) actualNode).getLeft();
                    } 
                    actualLvl++;
                }
            }
            else { //je interna musim najst kam ist dalej
                if (hash.get(actualLvl) == true) { //pojde do prava lebo je 1
                    actualNode = ((InternalNode) actualNode).getRight();
                }
                else {
                    actualNode = ((InternalNode) actualNode).getLeft();
                } 
                actualLvl++;
            }
        }
        return (ExternalNode) actualNode;
    }
    
    public void writeToFile(T record) throws IOException {
        ExternalNode nodeForWrite = this.findNode(record.getHash());
        
        if (nodeForWrite.getAddress() == -1) {
            nodeForWrite.setAddress(this.getFreeBlockAddress());
        }
        
//        BigInteger tst = BigInteger.valueOf(2147483646);
//        try {
//            this.mainFile.seek(0);
//            this.mainFile.write(tst.toByteArray());
//        } catch (IOException ex) {
//            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        Block<T> block = new Block <>(this.BlockingFactorMain);
        block.insert(record);
        byte b[] = block.toByteArray(this.BlockingFactorMain);
        this.mainFile.seek(nodeForWrite.getAddress()); 
        this.mainFile.write(b);
        
        nodeForWrite.setCount(nodeForWrite.getCount()+1);
        
//        byte b[] = new byte[this.BlockingFactorMain * record.getSize()];
//        try {
//            this.mainFile.read(b, nodeForWrite.getAddress(), this.BlockingFactorMain * record.getSize());
//        } catch (IOException ex) {
//            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        blok.fromByteArray(b);
    }
    
    public Block readFromFile(int Address) throws IOException {
        byte[] b = new byte[this.BlockingFactorMain * 4]; //TODO konstanta zatial

        this.mainFile.seek(Address);
        this.mainFile.read(b, 0, b.length);
        
        Block<T> block = new Block <>(this.BlockingFactorMain);
        block.fromByteArray(b);
        
        return block;
            
//            this.mainFile.read(bytes);
            

    }
    
    
    
    
}
