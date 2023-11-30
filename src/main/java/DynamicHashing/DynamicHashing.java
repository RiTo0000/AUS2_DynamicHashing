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
    
    private int freeMainBlockAddress;
    
    private RandomAccessFile mainFile;
    
    private Class<T> classType;
    
    public DynamicHashing(String mainFilePath, Class<T> classType, int blockingFactorMain) {
        this.Root = new ExternalNode(null);
        this.BlockingFactorMain = blockingFactorMain;
        this.freeMainBlockAddress = -1;
        try {
            this.mainFile = new RandomAccessFile(mainFilePath, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DynamicHashing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.classType = classType;
        
    }
    
    private int getFreeBlockAddress() throws IOException {
        int freeBlockAddress = 0;
        if (this.freeMainBlockAddress == -1) {
            freeBlockAddress = (int) this.mainFile.length();
        }
        else {
            freeBlockAddress = this.freeMainBlockAddress;
            this.freeMainBlockAddress = this.readFromFile(freeBlockAddress).getNextBlockAddress();
        }
        return freeBlockAddress;
    }
    
    private void addFreeBlockAddress(int Address) throws IOException {
        //TODO pozriet ci nie je na konci suboru ze by sa dalo uvolnit miesto
        Block<T> blok = new Block(Address, this.BlockingFactorMain, this.classType);
        blok.setNextBlockAddress(this.freeMainBlockAddress);
        
        this.writeToFile(Address, blok);
        
        this.freeMainBlockAddress = Address;
    }
    
    public void insert(T element) throws IOException {
        ExternalNode nodeForInsert = this.findNode(element.getHash(), true);
        
        this.insertOnNode(nodeForInsert, element);
    }
    
    public T find(T element) throws IOException {
        ExternalNode nodeForInsert = this.findNode(element.getHash(), false);
        
        Block blok = this.readFromFile(nodeForInsert.getAddress());
        
        ArrayList<T> records = blok.getRecords();
        
        for (T record : records) {
            if (record.equals(element)) {
                return record;
            }
        }
        
        //ak sa nenasiel doteraz tak nie je cize vratim null (nenajdeny objekt)
        return null;
    }
    
    public boolean delete(T element) throws IOException {
        Block blok;
        ArrayList<T> records;
        
        ExternalNode nodeForDelete = this.findNode(element.getHash(), false);
        
        if (nodeForDelete.getAddress() != -1) { //adresa je definovana
            blok = this.readFromFile(nodeForDelete.getAddress());
            
            records = blok.getRecords();
            for (T record : records) {
                if (element.equals(record)) {
                    return records.remove(record);
                }
            }
        }
        
        //ak sa do teraz nezmazal tak asi nie je 
        return false;        
    }
    
    private ExternalNode findNode(BitSet hash, boolean insertNode) throws IOException {
        InternalNode parent;
        Node actualNode = this.Root;
        int actualLvl = 0;
        boolean found = false;
        
        while(!found) {
            if (actualNode.isExternal()) { //je to externy node
                if (insertNode) {  
                    //ak hladam insertNode tak musim robit kontrolu ci je v node miesto ak nie snazim sa rozdelit 
                    if (((ExternalNode) actualNode).getCount() < this.BlockingFactorMain ) { //este sa vojde sem do nodu
                        found = true;
                    }
                    else { //nevojde sa musim delit node podla dalsieho bitu hashu
                        if (actualLvl == hash.length()) { //ak uz nemozem delit lebo som na konci hashu tak vratim posledny najdeny
                            break; //ukoncim cyklus a spodny return vrati doteraz najdeny node
                        }
                        
                        //delenie nodu na externe a presuvanie prvkov v aktualnom node
                        InternalNode newParent = new InternalNode(actualNode.getParent());
                        parent = actualNode.getParent();
                        actualNode.setParent(newParent);
                        newParent.setLeft(actualNode);
                        newParent.setRight(new ExternalNode(newParent));
                        
                        if (actualLvl == 0) { //ak sme na lvl 0 tak musime prepisat aj referenciu na Root node
                            this.Root = newParent;
                        }
                        else { //parent uz bude nastaveny
                            //potrebujeme updatnut parentovi laveho alebo praveho syna
                            if (hash.get(actualLvl - 1) == true) { //isli sme do prava lebo 1
                                parent.setRight(newParent);
                            }
                            else { //isli sme do lava lebo 0
                                parent.setLeft(newParent);
                            }
                        }

                        Block block = this.readFromFile(((ExternalNode) actualNode).getAddress());
                        ArrayList<T> records = block.getRecords();
                        ((ExternalNode) actualNode).setCount(0); //nastavime count na 0 lebo vsetky objekty sa budu na novo insertovat
                        ((ExternalNode) actualNode).setAddress(-1); //nastavime adresu na -1 (nepriradena adresa)

                        actualNode = newParent; //Nastavime si aktualnu Nodu na toho otca ktorym sme ho nahradili
                        this.addFreeBlockAddress(block.getAddress());
                        for ( T record : records) {
                            if (record.getHash().get(actualLvl) == true) { //pojde do prava lebo je 1
                                this.insertOnNode((ExternalNode) ((InternalNode) actualNode).getRight(), record);
                            }
                            else {
                                this.insertOnNode((ExternalNode) ((InternalNode) actualNode).getLeft(), record);
                            }
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
                else {
                    found = true;
                }
            }
            else { //je to interny node hladam kam sa vetvim dalej
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
    
    private void insertOnNode(ExternalNode node, T record) throws IOException {
        Block blok;
        if (node.getAddress() == -1) { //nema adresu teda nemozu tam byt ziadne elementy
            node.setAddress(this.getFreeBlockAddress());
            blok = new Block(node.getAddress(), this.BlockingFactorMain, this.classType);
        }
        else { // ma adrese mozu byt elementy treba najprv nacitat existujuce
            blok = this.readFromFile(node.getAddress());
        }
        
        blok.insert(record);
        this.writeToFile(node.getAddress(), blok);

        node.setCount(node.getCount()+1);
    }
    
    public void writeToFile(int Address, Block blok) throws IOException {
        byte b[] = blok.toByteArray(this.BlockingFactorMain);
        this.mainFile.seek(Address); 
        this.mainFile.write(b);

    }
    
    public Block readFromFile(int Address) throws IOException {    
        Block<T> block = new Block <>(Address, this.BlockingFactorMain, this.classType);
        
        byte[] b = new byte[block.getSize()];

        this.mainFile.seek(Address);
        this.mainFile.read(b, 0, b.length);
        
        block.fromByteArray(b, this.BlockingFactorMain);
        
        return block;
    }
    
    public String readWholeFile() throws IOException {
        String result = "";
        int index = 0;
        Block<T> blok;
        
        while (index < this.mainFile.length()) {            
            blok = this.readFromFile(index);
            result += blok.blockToString();
            
            index += blok.getSize();
        }
        
        
        return result;
    }
    
}
