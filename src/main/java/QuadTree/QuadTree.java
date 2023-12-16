package QuadTree;

import java.util.ArrayList;


/**
 *
 * @author namer
 * @param <T>
 */
public class QuadTree <T extends QuadTreeElementKey > {
    private int maxSize;
    private Area space;
    private int maxKey; //aktualne najvyssie priradene cislo kluca
    
    private QuadTreeNode Root;
    
    private QuadTreeNode optimalRoot;
    private int optimalRootLevel;
    
    private int numOfElements;
    /**
     * pocet elementov ktore su na najvyssom lvl kvoli tomu, ze sa strom uz nemohol delit kvoli max velkosti
     */
    private IntegerRef numOfTopLvlElements;
    
    private boolean optimal = false;
    
    
    public QuadTree(int pSize, Point start, Point end){
        this.maxSize = pSize; 
        this.space = new Area(start, end);
        
        this.Root = new QuadTreeNode(this.space);
        this.optimalRoot = this.Root;
        this.optimalRootLevel = 0;
        
        this.numOfElements = 0;
        this.numOfTopLvlElements = new IntegerRef(0);
    }

    /**
     * Metoda pre nacitanie priestoru Quad stromu
     * @return vrati priestor Quad stromu
     */
    public Area getSpace() {
        return this.space;
    }
    
    /**
     * Metoda pre nacitanie maximalnej velkosti Quad stromu
     * @return vrati maximalnu velkost Quad stromu
     */
    public int getMaxSize() {
        return this.maxSize;
    }

    /**
     * Metoda pre nacitanie priznaku optimalizacie struktury
     * @return vrati priznak optimalizacie struktury
     */
    public boolean isOptimal() {
        return this.optimal;
    }
    
    /**
     * Metoda pre nastavenie priestoru Quad stromu
     * @param space novy priestor Quad stromu
     */
    public void setSpace(Area space) {
        this.space = space;
    }

    /**
     * Metoda pre nastavenie maximalnej velkosti
     * @param maxSize nova maximalna velkost
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    /**
     * Nastavi priznak optimalizacie struktury
     * @param optimal true - struktura bude optimalizovana
     *                false - struktura nebude optimalizovana
     */
    public void setOptimal(boolean optimal) {
        if (optimal) {
            if (!this.optimal) {
                //nastavim ako optimalnu po tom co nebola optimalna tak ju optimalizujem
                this.optimizeTree();
            } 
        }
        else { // zrusenie optimalizacie
            this.optimalRoot = this.Root;
            this.optimalRootLevel = 0;
        }
        this.optimal = optimal;
    }
    
    /**
     * Metoda pre vlozenie elementu do Quad stromu
     * @param element prvok vkladany do Quad stromu
     * @param generateNewKey true ak sa ma generovat novy kluc, false inak
     * @return vysledok vkladania true ak sa vlozenie podarilo , false - ak sa vlozenie nepodarilo
     */
    public boolean insert(T element, boolean generateNewKey) {  
        boolean inserted = false;
        
        if (generateNewKey) {
            this.maxKey++;
            element.setKey(this.maxKey);
        }
        
        if (this.optimalRoot.getSpace().check_if_object_fits(element.getSpace())) {
            if (this.optimalRoot.insert(element, (this.maxSize - this.optimalRootLevel), this.numOfTopLvlElements)) {
                this.numOfElements++;
                inserted = true;
            }
        }
        else {
            if (this.Root.insert(element, this.maxSize, this.numOfTopLvlElements)) {
                this.numOfElements++;
                // tu sa musi optimalizovat Root lebo inak by sme nemali pristup ku aktualne vlozenemu prvku
                this.optimizeTreeRoot();
                inserted = true;
            }
        }
        
        if (this.optimal) {
            this.autoOptimalization();
        }
        
        return inserted;
    }
    
    /**
     * Metoda pre odstranenie prvku z Quad stromu
     * @param space priestor na ktorom sa dany prvok nachadza
     * @param key interny kluc Quad stromu
     * @return vysledok mazania true ak sa mazanie podarilo , false - ak sa mazanie nepodarilo
     */
    public boolean delete(Area space, int key) {
        ArrayList<QuadTreeNode> rootToActual = new ArrayList<QuadTreeNode>();
        QuadTreeNode nodeBeforeActual = null;
        QuadTreeNode actual = this.optimalRoot;
        
        boolean elementRemoved = false;
        boolean quadTreeCleaned = false;
        
        while (!elementRemoved) {
        
            if (actual.getSpace().check_if_object_fits(space)) {
                for (int k = 0; k < actual.getElements().size(); k++) {
                    if (actual.getElementFromElements(k).getKey() == key) {
                        actual.removeElementFromElements(k);
                        elementRemoved = true;
                        break;
                    }
                } 
                if (!elementRemoved) {
                    if (actual.getNode_NW() != null) {
                        if (actual.getNode_NW().getSpace().check_if_object_fits(space)) {
                            rootToActual.add(actual);
                            actual = actual.getNode_NW();
                        }
                        else if (actual.getNode_NE().getSpace().check_if_object_fits(space)) {
                            rootToActual.add(actual);
                            actual = actual.getNode_NE();
                        }
                        else if (actual.getNode_SE().getSpace().check_if_object_fits(space)) {
                            rootToActual.add(actual);
                            actual = actual.getNode_SE();
                        }
                        else if (actual.getNode_SW().getSpace().check_if_object_fits(space)) {
                            rootToActual.add(actual);
                            actual = actual.getNode_SW();
                        }
                        else
                            return false;
                    }
                    else 
                        return false;
                }
                else { //element bol odstraneny treba porobit poriadok
                    quadTreeCleaned = false;
                    while (!quadTreeCleaned && !rootToActual.isEmpty()) {
                        if (actual.isNodeEmpty()) { //potrebujem ju odstranit
                            nodeBeforeActual = rootToActual.remove(rootToActual.size() - 1);
                            if (nodeBeforeActual.getNode_NW().isNodeEmpty() &&
                                    nodeBeforeActual.getNode_NE().isNodeEmpty() &&
                                    nodeBeforeActual.getNode_SE().isNodeEmpty() &&
                                    nodeBeforeActual.getNode_SW().isNodeEmpty()) {

                                //ak su vsetky 4 prazdne mozem ich odmazat (nastavit na null)
                                nodeBeforeActual.setNode_NW(null);
                                nodeBeforeActual.setNode_NE(null);
                                nodeBeforeActual.setNode_SE(null);
                                nodeBeforeActual.setNode_SW(null);

                                actual = nodeBeforeActual;
                            }
                            else
                                quadTreeCleaned = true;
                        }
                        else { //kontrola na presun prvku o level vyssie (ak je posledny)
                            if (actual.getElements().size() == 1 && 
                                    actual.getNode_NW() == null &&
                                    actual.getNode_NE() == null &&
                                    actual.getNode_SE() == null &&
                                    actual.getNode_SW() == null) {
                                nodeBeforeActual = rootToActual.remove(rootToActual.size() - 1);
                                if (nodeBeforeActual.getElements().isEmpty() &&
                                        (nodeBeforeActual.getNode_NW().isNodeEmpty() || nodeBeforeActual.getNode_NW().equals(actual)) &&
                                        (nodeBeforeActual.getNode_NE().isNodeEmpty() || nodeBeforeActual.getNode_NE().equals(actual)) &&
                                        (nodeBeforeActual.getNode_SE().isNodeEmpty() || nodeBeforeActual.getNode_SE().equals(actual)) &&
                                        (nodeBeforeActual.getNode_SW().isNodeEmpty() || nodeBeforeActual.getNode_SW().equals(actual))) {
                                    nodeBeforeActual.addElement(actual.getElementFromElements(0)); // prida element k otcovi
                                    actual.removeElementFromElements(0); //vymaze ho zo svojho zoznamu
                                    
                                    //odstrani referencie na prazdne nody
                                    nodeBeforeActual.setNode_NW(null);
                                    nodeBeforeActual.setNode_NE(null);
                                    nodeBeforeActual.setNode_SE(null);
                                    nodeBeforeActual.setNode_SW(null);
                                    
                                    actual = nodeBeforeActual;
                                }
                                else
                                    quadTreeCleaned = true;
                            }
                            else
                                quadTreeCleaned = true;
                        }
                    }
                }
            }
            else //nevojde sa do aktualneho tak asi neexistuje (neda sa vymazat) 
                break;
        }
        
        if (elementRemoved) {
            this.numOfElements--;
            
            if (this.optimal) {
                //skontrolujem ci sa oplati optimalizovat a ak ano optimalizujem
                this.autoOptimalization();
            }
        }
        
        return elementRemoved;
    }
    
    /**
     * Metoda pre automaticku optimalizaciu
     * Vola sa po operacii vlozenia a mazania a zistuje sa zdravie struktury
     * Ak je zdravie mensie ako 75% tak sa automaticky optimalizuje
     */
    public void autoOptimalization() {
        //ak je zdravie mensie ako 75% automaticky spustim optimalizaciu
        if(this.checkHealth() < 0.75) {
            this.optimizeTree();
        }
    }
    
    /**
     * Metoda pre zistenie zdravia struktury Quad stromu
     * @return vrati celkove zdravie struktury (hodnota od 0 po 1)
     */
    public double checkHealth() {
        double health = (this.checkHealthRootPosition() + this.checkHealthMaxSize()) / 2;

        return health;
    }
    
    /**
     * Metoda pre zistenie zdravia maximalnej velkosti struktury
     * @return vrati zdravie struktury z pohladu maximalnej velkosti (hodnota od 0 po 1)
     */
    public double checkHealthMaxSize() {
        double health = 1.0 - ( Double.valueOf(this.numOfTopLvlElements.getValue()) / Double.valueOf(this.numOfElements));

        return health;
    }
    
    /**
     * Metoda pre zistenie zdravia struktury Quad stromu z pohladu rychlosti pristupu k prvkom 
     * @return vrati zdravie struktury z pohladu rychlosti pristupu k prvkom (hodnota od 0 po 1)
     */
    public double checkHealthRootPosition() {
        double health = 1.0 - ( Double.valueOf(this.checkOptimalRootLvl() - this.optimalRootLevel) / Double.valueOf(this.maxSize));

        return health;
    }
    
    /**
     * Metoda pre optimalizaciu Quad stromu
     */
    public void optimizeTree() {
        double hlpNum;
        double newMaxSizeD;
        int newMaxSize;

        //zistim ci je strom optimalny (pocet prvkov ktore na poslednom lvl su a nemuseli by byt)
        if (this.numOfTopLvlElements.getValue() > 0) {
            // vypocet poctu elementov kotre by sa pri najhorsom mohli nachadzat na poslednej vrstve
            hlpNum = (this.numOfTopLvlElements.getValue() + Math.pow(4, (this.maxSize - this.optimalRootLevel))) * 1.5; // pridam 50% rezervu 
            // log (poctu elementov na poslednom lvl) so zakladom 4             
            newMaxSizeD = Math.log(hlpNum) / Math.log(4);
            
            newMaxSize = (int) Math.ceil(newMaxSizeD); // nova vyska ale vyratana az od akt. optimalneho lvl
            newMaxSize += this.optimalRootLevel; // nova optimalna vyska vyratana od lvl 0
            
            this.resizeTree(newMaxSize);
        }
        
        //optimalizacia korena
        this.optimizeTreeRoot();
    }
    
    /**
     * Metoda optimalizuje koren stromu (vyberie optimalny prvok ako koren stromu) 
     */
    public void optimizeTreeRoot() {
        QuadTreeNode<T> actual;
        int numOfEmptyNodes;
        int actualLvl;
        
        actual = this.Root;
        
        for (actualLvl = 0; actualLvl < this.maxSize; actualLvl++) {
            numOfEmptyNodes = 0; // vynulujem si pocet prazdnych nodov
            // kontrola aby node nebol prazdny ale taktiez neobsahoval ziadne elementy
            if (!actual.isNodeEmpty() && actual.getElements().isEmpty()) {
                if (actual.getNode_NW().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_NE().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_SE().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_SW().isNodeEmpty()) 
                    numOfEmptyNodes++;
                
                if (numOfEmptyNodes == 3) {
                    if (!actual.getNode_NW().isNodeEmpty()) 
                        actual = actual.getNode_NW();
                    else if (!actual.getNode_NE().isNodeEmpty()) 
                        actual = actual.getNode_NE();
                    else if (!actual.getNode_SE().isNodeEmpty()) 
                        actual = actual.getNode_SE();
                    else if (!actual.getNode_SW().isNodeEmpty()) 
                        actual = actual.getNode_SW();
                }
                else 
                    // nasli sme optimalny root (actual)
                    break;
            }
            else
                // nasli sme optimalny root (actual)
                break;
            
        }
        
        //nastavime optimalny root
        this.optimalRoot = actual;
        this.optimalRootLevel = actualLvl;
        
    }
    
    /**
     * Metoda pre zistenie optimalnej urovne Root prvku 
     * @return vrati optimalnu uroven Root prvku
     */
    public int checkOptimalRootLvl() {
        QuadTreeNode<T> actual;
        int numOfEmptyNodes;
        int actualLvl;
        
        actual = this.optimalRoot;
        
        for (actualLvl = this.optimalRootLevel; actualLvl < this.maxSize; actualLvl++) {
            numOfEmptyNodes = 0; // vynulujem si pocet prazdnych nodov
            // kontrola aby node nebol prazdny ale taktiez neobsahoval ziadne elementy
            if (!actual.isNodeEmpty() && actual.getElements().isEmpty()) {
                if (actual.getNode_NW().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_NE().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_SE().isNodeEmpty()) 
                    numOfEmptyNodes++;
                if (actual.getNode_SW().isNodeEmpty()) 
                    numOfEmptyNodes++;
                
                if (numOfEmptyNodes == 3) {
                    if (!actual.getNode_NW().isNodeEmpty()) 
                        actual = actual.getNode_NW();
                    else if (!actual.getNode_NE().isNodeEmpty()) 
                        actual = actual.getNode_NE();
                    else if (!actual.getNode_SE().isNodeEmpty()) 
                        actual = actual.getNode_SE();
                    else if (!actual.getNode_SW().isNodeEmpty()) 
                        actual = actual.getNode_SW();
                }
                else 
                    // nasli sme optimalny root (actual)
                    break;
            }
            else
                // nasli sme optimalny root (actual)
                break;
            
        }
        return actualLvl;
    }
    
    /**
     * Metoda pre zmenu maximalne velkosti stromu
     * @param newMaxSize nova maximalna velkost stromu
     */
    public void resizeTree(int newMaxSize) {
        int actualLevel;
        int actualMaxSize;
        ArrayList<QuadTreeNode> newFinalLvl = new ArrayList<QuadTreeNode>();
        
        ArrayList<QuadTreeNode> actualLvl = new ArrayList<QuadTreeNode>();
        ArrayList<QuadTreeNode> nextLvl = new ArrayList<QuadTreeNode>();
        
        ArrayList<T> elementsToTransfer = new ArrayList<T>();
        
        
        if (this.maxSize  == newMaxSize) 
            return; //ak sa velkost nezmenila nie je potrebne nic robit
        
        if (this.maxSize > newMaxSize) { //zmensenie stromu
            if (newMaxSize < this.optimalRootLevel) {
                //musime zacat od rootu
                actualLevel = 0;
                actualLvl.add(this.Root);
            }
            else {
                //vystacime si s optimalnym rootom
                actualLevel = this.optimalRootLevel;
                actualLvl.add(this.optimalRoot);
            }
            
            while (this.maxSize >= actualLevel &&
                    !actualLvl.isEmpty()) {               
                
                for (QuadTreeNode actual : actualLvl) {
                    //pridam synov do zoznamu nespracovanych
                    if (actual.getNode_NW() != null) 
                        nextLvl.add(actual.getNode_NW());
                    if (actual.getNode_NE() != null) 
                        nextLvl.add(actual.getNode_NE());
                    if (actual.getNode_SE() != null) 
                        nextLvl.add(actual.getNode_SE());
                    if (actual.getNode_SW() != null) 
                        nextLvl.add(actual.getNode_SW());
                    
                    if (actualLevel > newMaxSize)
                        //vsetky elementy v aktualnom node zapisem do zoznamu elementov na spracovanie
                        elementsToTransfer.addAll(actual.getElements());
                }
                
                if (actualLevel == newMaxSize) {
                    newFinalLvl.addAll(actualLvl);
                }
                
                //ked som spracoval vsetky nody na tomto levli tak pokracujem na dalsi
                actualLvl.clear();
                actualLvl.addAll(nextLvl);
                nextLvl.clear();
                
                actualLevel++;
            }
            
            for (T element : elementsToTransfer) { //prejde vsetky elementy a priradi ich postupne na node kam patria
                for (QuadTreeNode finalLvlNode : newFinalLvl ) { //prejde vsetky nody na poslednom levli
                    
                    if (finalLvlNode.getNode_NW() != null ||
                        finalLvlNode.getNode_NE() != null ||
                        finalLvlNode.getNode_SE() != null ||
                        finalLvlNode.getNode_SW() != null ) {                    
                        //odmazem im referencie na synov
                        finalLvlNode.setNode_NW(null);
                        finalLvlNode.setNode_NE(null);
                        finalLvlNode.setNode_SE(null);
                        finalLvlNode.setNode_SW(null);
                    }
                    
                    if (finalLvlNode.getSpace().check_if_object_fits(element.getSpace())) {
                        finalLvlNode.addElement(element);
                        break; //element bol umiestneny mozme prejst na dalsi
                    }
                }
            }
            // ak me nova maximalna velkost nizsia ako lvl optimalneho rootu treba ho optimalizovat
            if (newMaxSize < this.optimalRootLevel) 
                this.optimizeTreeRoot();
        }
        else { //zvacsenie stromu
            actualLevel = this.optimalRootLevel;
            actualLvl.add(this.optimalRoot);
            
            while (this.maxSize >= actualLevel &&
                    !actualLvl.isEmpty()) {               
                
                for (QuadTreeNode actual : actualLvl) {
                    //pridam synov do zoznamu nespracovanych
                    if (actual.getNode_NW() != null) 
                        nextLvl.add(actual.getNode_NW());
                    if (actual.getNode_NE() != null) 
                        nextLvl.add(actual.getNode_NE());
                    if (actual.getNode_SE() != null) 
                        nextLvl.add(actual.getNode_SE());
                    if (actual.getNode_SW() != null) 
                        nextLvl.add(actual.getNode_SW());
                }
                
                if (nextLvl.isEmpty()) {
                    break;//nasiel som posledny lvl
                }
                
                //ked som spracoval vsetky nody na tomto levli tak pokracujem na dalsi
                actualLvl.clear();
                actualLvl.addAll(nextLvl);
                nextLvl.clear();
                
                actualLevel++;
            }
            
            if (actualLevel == this.maxSize) {
                this.numOfTopLvlElements.setValue(0);
                actualMaxSize = newMaxSize - actualLevel;
                //treba robit daco s prvkami na poslednej vrstve
                for (QuadTreeNode node : actualLvl) {
                    elementsToTransfer.clear(); 
                    elementsToTransfer.addAll(node.getElements()); //ulozi si elementy na presun
                    node.clearElements(); // vycisti elementy aby sa neduplikovali
                    for (T t : elementsToTransfer) { //postupne vlozi kazdy element pod aktualny node
                        node.insert(t, actualMaxSize, this.numOfTopLvlElements);
                    }
                }
                
            }
            //inak nemusim nic upravovat kedze sme nedosiahli potencial stromu aj tak
        }
        
        this.maxSize = newMaxSize;
    }
    
    /**
     * Metoda najde vsetky objekty ktore co len trocha zasahuju do zadaneho priestoru 
     * 
     * @param space priestor v ktorom sa maju objekty vyhladavat
     * @return vrati ArrayList objektov, ktore zasahuju do zadaneho priestoru
     */
    public ArrayList<T> findElementsInArea(Area space) {
        ArrayList<QuadTreeNode> unprocessedNodesLVL1 = new ArrayList<QuadTreeNode>();
        ArrayList<QuadTreeNode> unprocessedNodesLVL2 = new ArrayList<QuadTreeNode>();
        
        ArrayList<T> result = new ArrayList<T>();
        T element;
        QuadTreeNode actual = this.optimalRoot;
        
        if (actual.getSpace().check_if_object_overlaps(space)) {
        
            unprocessedNodesLVL1.add(actual);

            for (int i = this.optimalRootLevel; i <= this.maxSize; i++) { //spracovava kazdy lvl
                if (unprocessedNodesLVL1.isEmpty()) {
                    //nemusime pokracovat v spracovavani lebo nie je co spracovavat
                    break;
                }
                for (int j = 0; j < unprocessedNodesLVL1.size(); j++) { //postupne prechadza nespracovane nody
                    actual = unprocessedNodesLVL1.get(j);

                    for (int k = 0; k < actual.getElements().size(); k++) { //prejde vsetky elementy vrchola
                        element = (T) actual.getElementFromElements(k); 
                        
                        if (element.getSpace().check_if_object_overlaps(space))//ak element spada do hladaneho priestoru pridame ho do vystupu
                            result.add(element);
                    }
                    //pridam synov do zoznamu nespracovanych
                    if (actual.getNode_NW() != null) {
                        if (actual.getNode_NW().getSpace().check_if_object_overlaps(space)) 
                            unprocessedNodesLVL2.add(actual.getNode_NW());
                    }
                    if (actual.getNode_NE() != null) {
                        if (actual.getNode_NE().getSpace().check_if_object_overlaps(space))
                            unprocessedNodesLVL2.add(actual.getNode_NE());
                    }
                    if (actual.getNode_SE() != null) {
                        if (actual.getNode_SE().getSpace().check_if_object_overlaps(space))
                            unprocessedNodesLVL2.add(actual.getNode_SE());
                    }
                    if (actual.getNode_SW() != null) {
                        if (actual.getNode_SW().getSpace().check_if_object_overlaps(space))
                            unprocessedNodesLVL2.add(actual.getNode_SW());
                    }
                }
                //ked som spracoval vsetky nody na tomto levli tak pokracujem na dalsi
                unprocessedNodesLVL1.clear();
                unprocessedNodesLVL1.addAll(unprocessedNodesLVL2);
                unprocessedNodesLVL2.clear();
            }
        }
        
        return result;
    }
    
    /**
     * Metoda pre ziskanie vsetkych elementov ktore sa nachadzaju v Quad strome
     * @return elementy nachadzajuce sa v Quad strome
     */
    public ArrayList<T> getAllElements() {
        ArrayList<T> elements = new ArrayList<T>();
        
        ArrayList<QuadTreeNode> unprocessedNodesLVL1 = new ArrayList<QuadTreeNode>();
        ArrayList<QuadTreeNode> unprocessedNodesLVL2 = new ArrayList<QuadTreeNode>();
        T element;
        QuadTreeNode actual = this.optimalRoot;
        
        unprocessedNodesLVL1.add(actual);
        
        for (int i = this.optimalRootLevel; i <= this.maxSize; i++) {
            if (unprocessedNodesLVL1.isEmpty()) {
                //nemusime pokracovat v spracovavani lebo nie je co spracovavat
                break;
            }
            
            for (int j = 0; j < unprocessedNodesLVL1.size(); j++) {
                actual = unprocessedNodesLVL1.get(j);
                for (int k = 0; k < actual.getElements().size(); k++) { //vyberie elementy ulozene vo vrchole
                    element = (T) actual.getElementFromElements(k); 
                    elements.add(element); //vlozi ho do ArrayListu vsetkych elementov
                }
                //pridam synov do zoznamu nespracovanych
                if (actual.getNode_NW() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_NW());
                }
                if (actual.getNode_NE() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_NE());
                }
                if (actual.getNode_SE() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_SE());
                }
                if (actual.getNode_SW() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_SW());
                }
            }
            //ked som spracoval vsetky nody na tomto levli tak pokracujem na dalsi
            unprocessedNodesLVL1.clear();
            unprocessedNodesLVL1.addAll(unprocessedNodesLVL2);
            unprocessedNodesLVL2.clear();
        }
        
        return elements;
    }
    
    /**
     * Metoda pre vypis celeho stromu do konzoly
     */
    public void printWholeTree() {
        ArrayList<QuadTreeNode> unprocessedNodesLVL1 = new ArrayList<QuadTreeNode>();
        ArrayList<QuadTreeNode> unprocessedNodesLVL2 = new ArrayList<QuadTreeNode>();
        T element;
//        QuadTreeNode actual = this.Root;
        QuadTreeNode actual = this.optimalRoot;
        
        int elementNumOnLvl;
        
        unprocessedNodesLVL1.add(actual);
        
        for (int i = this.optimalRootLevel; i <= this.maxSize; i++) {
            if (unprocessedNodesLVL1.isEmpty()) {
                //nemusime pokracovat v spracovavani lebo nie je co spracovavat
                break;
            }
            System.out.println("Level " + i + " :" );
            elementNumOnLvl = 0;
            for (int j = 0; j < unprocessedNodesLVL1.size(); j++) {
                actual = unprocessedNodesLVL1.get(j);
                for (int k = 0; k < actual.getElements().size(); k++) { //vypise elementy ulozene vo vrchole
                    System.out.println("Element num: " + elementNumOnLvl++ );
                    element = (T) actual.getElementFromElements(k); 
                    element.PrintInfo();
                }
                //pridam synov do zoznamu nespracovanych
                if (actual.getNode_NW() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_NW());
                }
                if (actual.getNode_NE() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_NE());
                }
                if (actual.getNode_SE() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_SE());
                }
                if (actual.getNode_SW() != null) {
                    unprocessedNodesLVL2.add(actual.getNode_SW());
                }
            }
            //ked som spracoval vsetky nody na tomto levli tak pokracujem na dalsi
            unprocessedNodesLVL1.clear();
            unprocessedNodesLVL1.addAll(unprocessedNodesLVL2);
            unprocessedNodesLVL2.clear();
        }
    }
    
    /**
     * Metoda vycisti data QuadStromu
     */
    public void clearAllData() {
        this.Root = new QuadTreeNode(this.space);
        this.optimalRoot = this.Root;
        this.optimalRootLevel = 0;
        
        this.numOfElements = 0;
        this.numOfTopLvlElements.setValue(0);
    }
}
