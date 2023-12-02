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
    
    private long freeMainBlockAddress;
    
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
    
    private long getFreeBlockAddress() throws IOException {
        Block<T> actualFreeBlock, nextFreeBlock;
        
        long freeBlockAddress = 0;
        if (this.freeMainBlockAddress == -1) {
            freeBlockAddress = this.mainFile.length();
        }
        else {
            freeBlockAddress = this.freeMainBlockAddress;
            actualFreeBlock = this.readFromFile(freeBlockAddress);
            if (actualFreeBlock.getNextBlockAddress() != -1) { // ak ma naslednovny blok musim mu upravit predchodcu
                nextFreeBlock = this.readFromFile(actualFreeBlock.getNextBlockAddress());
                nextFreeBlock.setPreviousBlockAddress(-1); //predchodcu nema lebo je prvy v zretazeni
                this.writeToFile(nextFreeBlock);
            }
            
            this.freeMainBlockAddress = actualFreeBlock.getNextBlockAddress(); //nastavime adresu volneho bloku na nasledujucu po aktualnom volnom bloku
        }
        return freeBlockAddress;
    }
    
    private void addFreeBlockAddress(long Address) throws IOException {
        long newFileLength;
        boolean fileLengthOptimal = false;
        Block<T> hlpBlock;
        
        Block<T> lastBlock = new Block(Address, this.BlockingFactorMain, this.classType);
        
        //TODO pozriet ci nie je na konci suboru ze by sa dalo uvolnit miesto
        newFileLength = this.mainFile.length() - lastBlock.getSize();
        if (newFileLength == Address) { //odstranovane miesto je na konci suboru tak ho skratim
            
            while (!fileLengthOptimal) {                
                newFileLength -= lastBlock.getSize();
                
                if (newFileLength < 0) { //uz nie je aky blok uvolnit
                    fileLengthOptimal = true;
                    
                    newFileLength += lastBlock.getSize(); //musim naspat navysit lebo uz sa nieje kam posunut (dosiahol som zaciatok suboru)
                }
                else {//kontrola ci predchadzajuci blok tiez nie je prazdny
                    lastBlock = this.readFromFile(newFileLength);
                    if (!lastBlock.isEmpty()) {
                        fileLengthOptimal = true;

                        newFileLength += lastBlock.getSize(); //musim naspat navysit lebo posledny blok uz nebol prazdny
                    }
                    else { //opravit prepojenie blokov aby stale fungovalo
                        long prevAdr = lastBlock.getPreviousBlockAddress();
                        long nextAdr = lastBlock.getNextBlockAddress();

                        if (prevAdr != -1) {
                            hlpBlock = this.readFromFile(prevAdr);
                            hlpBlock.setNextBlockAddress(nextAdr);
                            this.writeToFile(hlpBlock);
                        }
                        else { // uvolnili sme prvy blok v zretazeni teda potrebujeme si na novo nastavit adresu prveho volneho bloku
                            this.freeMainBlockAddress = nextAdr;
                        }

                        if (nextAdr != -1) {
                            hlpBlock = this.readFromFile(nextAdr);
                            hlpBlock.setPreviousBlockAddress(prevAdr);
                            this.writeToFile(hlpBlock);
                        }
                    }
                }
            }
            
            this.mainFile.setLength(newFileLength);
            
        }
        else { //odstranovane miesto nie je na konci nemozem subor skratit
            
            Block<T> newFreeBlok = new Block(Address, this.BlockingFactorMain, this.classType);
            newFreeBlok.setPreviousBlockAddress(-1);
            newFreeBlok.setNextBlockAddress(this.freeMainBlockAddress);

            this.writeToFile(newFreeBlok);

            if (this.freeMainBlockAddress != -1) { // ak sa adresa na volny blok rovna -1 tak netreba vkladat stary volny blok lebo neexistuje
                Block<T> oldFreeBlok = this.readFromFile(this.freeMainBlockAddress);
                oldFreeBlok.setPreviousBlockAddress(Address);

                this.writeToFile(oldFreeBlok);
            }


            this.freeMainBlockAddress = Address;
        
        }
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
        Block blok = null;
        ArrayList<T> records;
        boolean result = false;
        
        ExternalNode nodeForDelete = this.findNode(element.getHash(), false);
        
        if (nodeForDelete.getAddress() != -1) { //adresa je definovana
            blok = this.readFromFile(nodeForDelete.getAddress());
            
            records = blok.getRecords();
            for (T record : records) {
                if (element.equals(record)) {
                    result = blok.removeRecord(record);
                    break;
                }
            }
            
            if (result) { //vymaz sa podaril
                if (records.isEmpty()) { //ak nema rekord treba odstranit prazdny node a nahradit ho 
                    this.clearNode(nodeForDelete); //vycistenie nodu a zaradenie adresy medzi prazdne
                }
                else { //zapisanie upraveneho bloku do suboru
                    this.writeToFile(blok);
                    nodeForDelete.setCount(nodeForDelete.getCount() - 1);
                }
            }
        }
       
        //ak sa do teraz nezmazal tak asi nie je 
        return result;        
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
                        this.clearNode((ExternalNode) actualNode); //node si vycistim

                        actualNode = newParent; //Nastavime si aktualnu Nodu na toho otca ktorym sme ho nahradili
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
            //TODO kontrola nakluc ci uz neexistuje nahodou a ak hej vyhodit chybu alebo neinsertnut
        }
        
        blok.insert(record);
        this.writeToFile(blok);

        node.setCount(node.getCount()+1);
    }
    
    public void writeToFile(Block blok) throws IOException {
        long address = blok.getAddress();
        
        byte b[] = blok.toByteArray(this.BlockingFactorMain);
        this.mainFile.seek(address); 
        this.mainFile.write(b);

    }
    
    public Block readFromFile(long Address) throws IOException {    
        Block<T> block = new Block <>(Address, this.BlockingFactorMain, this.classType);
        
        if (Address != -1) { //kontrola aby adresa nebola inicialna
            
            byte[] b = new byte[block.getSize()];

            this.mainFile.seek(Address);
            this.mainFile.read(b, 0, b.length);

            block.fromByteArray(b, this.BlockingFactorMain);
        }
        
        return block;
    }
    
    public String readWholeFile() throws IOException {
        String result = "";
        int index = 0;
        Block<T> blok;
        
        while (index < this.mainFile.length()) {
            blok = this.readFromFile(index);
            result += blok.getAddress() + ": ";
            result += blok.blockToString();
            
            index += blok.getSize();
        }
        
        return result;
    }
    
    /**
     * Metoda pre spravne vycistenie Externeho nodu
     * - prida jeho adresu medzi volne bloky
     * - resetuje adresu na danom node
     * - resetuje pocitadlo vlozenych elementov
     * @param nodeToClear node na vycistenie
     * @throws IOException 
     */
    public void clearNode(ExternalNode nodeToClear) throws IOException {
        this.addFreeBlockAddress(nodeToClear.getAddress());
        nodeToClear.setAddress(-1);
        nodeToClear.setCount(0);
        
        //TODO spravit preusporiadanie nodu a vyhodenie jedneho interneho
    }
    
}
