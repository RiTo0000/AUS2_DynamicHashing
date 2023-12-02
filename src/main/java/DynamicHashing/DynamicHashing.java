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
    
    private long freeMainBlockAddress;
    private long freeSecondBlockAddress;
    
    private RandomAccessFile mainFile;
    private RandomAccessFile secondFile;
    
    private int maxDepth;
    
    private Class<T> classType;
    
    public DynamicHashing(String mainFilePath, String secondFilePath, Class<T> classType, int blockingFactorMain, int blockingFactorSecond, int maxDepth) throws FileNotFoundException {
        this.Root = new ExternalNode(null);
        this.BlockingFactorMain = blockingFactorMain;
        this.BlockingFactorSecond = blockingFactorSecond;
        this.freeMainBlockAddress = -1;
        this.freeSecondBlockAddress = -1;
        this.mainFile = new RandomAccessFile(mainFilePath, "rw");
        this.secondFile = new RandomAccessFile(secondFilePath, "rw");
        
        this.classType = classType;
        
        this.maxDepth = maxDepth;
        
    }
    
    private long getFreeMainBlockAddress() throws IOException {
        Block<T> actualFreeBlock, nextFreeBlock;
        
        long freeBlockAddress = 0;
        if (this.freeMainBlockAddress == -1) {
            freeBlockAddress = this.mainFile.length();
        }
        else {
            freeBlockAddress = this.freeMainBlockAddress;
            actualFreeBlock = this.readFromFile(freeBlockAddress, this.mainFile, this.BlockingFactorMain);
            if (actualFreeBlock.getNextBlockAddress() != -1) { // ak ma naslednovny blok musim mu upravit predchodcu
                nextFreeBlock = this.readFromFile(actualFreeBlock.getNextBlockAddress(), this.mainFile, this.BlockingFactorMain);
                nextFreeBlock.setPreviousBlockAddress(-1); //predchodcu nema lebo je prvy v zretazeni
                this.writeToFile(nextFreeBlock, this.mainFile);
            }
            
            this.freeMainBlockAddress = actualFreeBlock.getNextBlockAddress(); //nastavime adresu volneho bloku na nasledujucu po aktualnom volnom bloku
        }
        return freeBlockAddress;
    }
    
    private long getFreeSecondBlockAddress() throws IOException {
        Block<T> actualFreeBlock, nextFreeBlock;
        
        long freeBlockAddress = 0;
        if (this.freeSecondBlockAddress == -1) {
            freeBlockAddress = this.secondFile.length();
        }
        else {
            freeBlockAddress = this.freeSecondBlockAddress;
            actualFreeBlock = this.readFromFile(freeBlockAddress, this.secondFile, this.BlockingFactorSecond);
            if (actualFreeBlock.getNextBlockAddress() != -1) { // ak ma naslednovny blok musim mu upravit predchodcu
                nextFreeBlock = this.readFromFile(actualFreeBlock.getNextBlockAddress(), this.secondFile, this.BlockingFactorSecond);
                nextFreeBlock.setPreviousBlockAddress(-1); //predchodcu nema lebo je prvy v zretazeni
                this.writeToFile(nextFreeBlock, this.secondFile);
            }
            
            this.freeSecondBlockAddress = actualFreeBlock.getNextBlockAddress(); //nastavime adresu volneho bloku na nasledujucu po aktualnom volnom bloku
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
                    lastBlock = this.readFromFile(newFileLength, this.mainFile, this.BlockingFactorMain);
                    if (!lastBlock.isEmpty()) {
                        fileLengthOptimal = true;

                        newFileLength += lastBlock.getSize(); //musim naspat navysit lebo posledny blok uz nebol prazdny
                    }
                    else { //opravit prepojenie blokov aby stale fungovalo
                        long prevAdr = lastBlock.getPreviousBlockAddress();
                        long nextAdr = lastBlock.getNextBlockAddress();

                        if (prevAdr != -1) {
                            hlpBlock = this.readFromFile(prevAdr, this.mainFile, this.BlockingFactorMain);
                            hlpBlock.setNextBlockAddress(nextAdr);
                            this.writeToFile(hlpBlock, this.mainFile);
                        }
                        else { // uvolnili sme prvy blok v zretazeni teda potrebujeme si na novo nastavit adresu prveho volneho bloku
                            this.freeMainBlockAddress = nextAdr;
                        }

                        if (nextAdr != -1) {
                            hlpBlock = this.readFromFile(nextAdr, this.mainFile, this.BlockingFactorMain);
                            hlpBlock.setPreviousBlockAddress(prevAdr);
                            this.writeToFile(hlpBlock, this.mainFile);
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

            this.writeToFile(newFreeBlok, this.mainFile);

            if (this.freeMainBlockAddress != -1) { // ak sa adresa na volny blok rovna -1 tak netreba vkladat stary volny blok lebo neexistuje
                Block<T> oldFreeBlok = this.readFromFile(this.freeMainBlockAddress, this.mainFile, this.BlockingFactorMain);
                oldFreeBlok.setPreviousBlockAddress(Address);

                this.writeToFile(oldFreeBlok, this.mainFile);
            }


            this.freeMainBlockAddress = Address;
        
        }
    }
    
    public void insert(T element) throws IOException, Exception {
        ExternalNode nodeForInsert = this.findNode(element.getHash(), true);
        
        this.insertOnNode(nodeForInsert, element);
    }
    
    public T find(T element) throws IOException, Exception {
        ExternalNode nodeForInsert = this.findNode(element.getHash(), false);
        
        Block blok = this.readFromFile(nodeForInsert.getAddress(), this.mainFile, this.BlockingFactorMain);
        
        ArrayList<T> records = blok.getRecords();
        
        for (T record : records) {
            if (record.equals(element)) {
                return record;
            }
        }
        
        //ak sa nenasiel doteraz tak nie je cize vratim null (nenajdeny objekt)
        return null;
    }
    
    public boolean delete(T element) throws IOException, Exception {
        Block blok = null;
        ArrayList<T> records;
        boolean result = false;
        
        ExternalNode nodeForDelete = this.findNode(element.getHash(), false);
        
        if (nodeForDelete.getAddress() != -1) { //adresa je definovana
            blok = this.readFromFile(nodeForDelete.getAddress(), this.mainFile, this.BlockingFactorMain);
            
            records = blok.getRecords();
            for (T record : records) {
                if (element.equals(record)) {
                    result = blok.removeRecord(record);
                    break;
                }
            }
            
            if (result) { //vymaz sa podaril
                if (records.isEmpty()) { //ak nema rekord treba odstranit prazdny node a nahradit ho 
                    this.clearNode(nodeForDelete, true); //vycistenie nodu a zaradenie adresy medzi prazdne
                }
                else { //zapisanie upraveneho bloku do suboru
                    this.writeToFile(blok, this.mainFile);
                    nodeForDelete.setCount(nodeForDelete.getCount() - 1);
                }
            }
        }
       
        //ak sa do teraz nezmazal tak asi nie je 
        return result;        
    }
    
    private ExternalNode findNode(BitSet hash, boolean insertNode) throws IOException, Exception {
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
                        if (actualLvl == this.maxDepth) { //ak uz nemozem delit lebo som na maximalnom lvl tak vratim posledny najdeny
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

                        Block block = this.readFromFile(((ExternalNode) actualNode).getAddress(), this.mainFile, this.BlockingFactorMain);
                        ArrayList<T> records = block.getRecords();
                        this.clearNode((ExternalNode) actualNode, false); //node si vycistim

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
    
    private void insertOnNode(ExternalNode node, T record) throws IOException, Exception {
        boolean secondFile = false;
        ArrayList<T> oldRecords;
        Block mainBlock;
        Block secondBlock = null;
        Block tmpSecondBlock = null;
        long secondBlockAddress;
        boolean secondBlockFound = false;
        
        if (node.getAddress() == -1) { //nema adresu teda nemozu tam byt ziadne elementy
            node.setAddress(this.getFreeMainBlockAddress());
            mainBlock = new Block(node.getAddress(), this.BlockingFactorMain, this.classType);
        }
        else { // ma adrese mozu byt elementy treba najprv nacitat existujuce
            mainBlock = this.readFromFile(node.getAddress(), this.mainFile, this.BlockingFactorMain);
            oldRecords = mainBlock.getRecords();
            
            //kontrola na unikatnost kluca
            for (T oldRecord : oldRecords) {
                if (oldRecord.equals(record)) {
                    throw new Exception("Non-unique key on insert");
                }
            }
            
            if (!mainBlock.isFull()) { //vojde sa do hlavneho suboru
                   
            }
            else { //ide do preplnujuceho suboru
                secondFile = true;
                secondBlockAddress = mainBlock.getNextBlockAddress();
                if (secondBlockAddress == -1) { //este nie je blok v preplnujucom subore
                    secondBlock = new Block(this.getFreeSecondBlockAddress(), this.BlockingFactorSecond, this.classType);
                    mainBlock.setNextBlockAddress(secondBlock.getAddress());
                    this.writeToFile(mainBlock, this.mainFile); //zapisem zmeneny blok spat do suboru
                    
                    //TODO second block setPrevBlockAdd
                }
                else { //uz je blok v preplnujucom subore
                    while (!secondBlockFound) {                        
                        secondBlock = this.readFromFile(secondBlockAddress, this.secondFile, this.BlockingFactorSecond);
                        
                        oldRecords = secondBlock.getRecords();
                        //kontrola na unikatnost kluca
                        for (T oldRecord : oldRecords) {
                            if (oldRecord.equals(record)) {
                                throw new Exception("Non-unique key on insert");
                            }
                        }
                        
                        //najdenie vhodneho bloku v preplnujucom subore
                        if (secondBlock.isFull()) { //preplnovaci blok je plny
                            if (secondBlock.getNextBlockAddress() == -1) { //nema definovany nasledujuci blok
                                secondBlockAddress = this.getFreeSecondBlockAddress(); //vygenerujeme novu adresu pre nasledujuci blok
                                secondBlock.setNextBlockAddress(secondBlockAddress); //nastavime novu vygenerovanu adresu do plneho bloku ako nasledovnika
                                this.writeToFile(secondBlock, this.secondFile); //zapisem upraveny blok do preplnujuceho suboru
                                
                                //vygenerujem novy blok v preplnujucom subore
                                tmpSecondBlock = new Block(secondBlockAddress, this.BlockingFactorSecond, this.classType);
                                tmpSecondBlock.setPreviousBlockAddress(secondBlock.getAddress()); //nastavim adresu predchadzajuceho bloku
                                secondBlock = tmpSecondBlock;
                                secondBlockFound = true;
                            }
                            else { //prejdem na nasledujuci blok v preplnujucom subore
                                secondBlockAddress = secondBlock.getNextBlockAddress();
                            }
                        }
                        else { //nie je plny tak budem insertovat don
                            secondBlockFound = true;
                        }
                    }
                  
                }
            }
        }
        
        if (secondFile) {//ide do preplnujuceho suboru
            secondBlock.insert(record);
            this.writeToFile(secondBlock, this.secondFile);
        }
        else {
            mainBlock.insert(record);
            this.writeToFile(mainBlock, this.mainFile);
        }

        node.setCount(node.getCount()+1);
    }
    
    public void writeToFile(Block blok, RandomAccessFile file) throws IOException {
        long address = blok.getAddress();
        
        byte b[] = blok.toByteArray();
        file.seek(address); 
        file.write(b);

    }
    
    public Block readFromFile(long Address, RandomAccessFile file, int blockingFactor) throws IOException {    
        Block<T> block = new Block <>(Address, blockingFactor, this.classType);
        
        if (Address != -1) { //kontrola aby adresa nebola inicialna
            
            byte[] b = new byte[block.getSize()];

            file.seek(Address);
            file.read(b, 0, b.length);

            block.fromByteArray(b);
        }
        
        return block;
    }
    
    public String readWholeMainFile() throws IOException {
        return this.readWholeFile(this.mainFile, this.BlockingFactorMain);
    }
    
    public String readWholeSecondFile() throws IOException {
        return this.readWholeFile(this.secondFile, this.BlockingFactorSecond);
    }
    
    public String readWholeFile(RandomAccessFile file, int blockingFactor) throws IOException {
        String result = "";
        int index = 0;
        Block<T> blok;
        
        while (index < file.length()) {
            blok = this.readFromFile(index, file, blockingFactor);
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
     * - preusporiada nody
     * @param nodeToClear node na vycistenie
     * @param rearrangeNodes true ak chceme aj preusporiadan nody (napriklad po vymazani), false inak
     * @throws IOException 
     */
    public void clearNode(ExternalNode nodeToClear, boolean rearrangeNodes) throws IOException {
        this.addFreeBlockAddress(nodeToClear.getAddress());
        nodeToClear.setAddress(-1);
        nodeToClear.setCount(0);
        
        //preusporiadanie nodov
        if (rearrangeNodes) {
            InternalNode parent = nodeToClear.getParent();
            InternalNode superParent;
            Node newSonOfSuperParent = null;

            if (parent != null) { //ak je to Root node tak nemusi nic usporiadat
                superParent = parent.getParent();
                if (parent.getLeft() == nodeToClear) { //Node ktory cistim je vlavo
                    newSonOfSuperParent = parent.getRight();
                }
                else { //Node ktory cistim je vpravo
                    newSonOfSuperParent = parent.getLeft();
                }

                newSonOfSuperParent.setParent(superParent);

                //uprava laveho/praveho nodu rodica rodica
                if (superParent != null) {            
                    if (superParent.getLeft() == parent) { //Rodic je vlavo
                        superParent.setLeft(newSonOfSuperParent);
                    }
                    else { //Rodic je vpravo
                        superParent.setRight(newSonOfSuperParent);
                    }
                }
                else { //Treba upravit adresu Root nodu
                    this.Root = newSonOfSuperParent;
                }
            }
        }
      
    }
    
}
