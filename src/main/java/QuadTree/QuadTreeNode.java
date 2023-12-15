package QuadTree;

import java.util.ArrayList;


/**
 *
 * @author namer
 * @param <T>
 */
public class QuadTreeNode <T extends QuadTreeElementKey >{
    private QuadTreeNode<T> node_NW;
    private QuadTreeNode<T> node_NE;
    private QuadTreeNode<T> node_SE;
    private QuadTreeNode<T> node_SW;
    
    private Area space;
    
    private ArrayList<T> elements = new ArrayList<T>();
    
    public QuadTreeNode(Area space){
        this.space = space;
    }

    /**
     * Metoda pre ziskanie oblasti nodu
     * @return vrati oblast nodu definovanu dvoma bodmi 
     */
    public Area getSpace() {
        return space;
    }

    /**
     * Metoda pre ziskanie prveho syna
     * @return referencia na objekt prveho syna
     */
    public QuadTreeNode<T> getNode_NW() {
        return node_NW;
    }
    
    /**
     * Metoda pre ziskanie druheho syna
     * @return referencia na objekt druheho syna
     */
    public QuadTreeNode<T> getNode_NE() {
        return node_NE;
    }
    
    /**
     * Metoda pre ziskanie tretieho syna
     * @return referencia na objekt tretieho syna
     */
    public QuadTreeNode<T> getNode_SE() {
        return node_SE;
    }
    
    /**
     * Metoda pre ziskanie stvrteho syna
     * @return referencia na objekt stvrteho syna
     */
    public QuadTreeNode<T> getNode_SW() {
        return node_SW;
    }

    /**
     * Metoda pre ziskanie elementov ulozenych priamo vo vrchole
     * @return vrati elemenety ulozene vo vrchole
     */
    public ArrayList<T> getElements() {
        return elements;
    }

    /**
     * Metoda pre nastavenie prveho syna
     * @param node_NW referencia na objekt prveho syna
     */
    public void setNode_NW(QuadTreeNode<T> node_NW) {
        this.node_NW = node_NW;
    }

    /**
     * Metoda pre nastavenie druheho syna
     * @param node_NE referencia na objekt druheho syna
     */
    public void setNode_NE(QuadTreeNode<T> node_NE) {
        this.node_NE = node_NE;
    }

    /**
     * Metoda pre nastavenie tretieho syna
     * @param node_SE referencia na objekt tretieho syna
     */
    public void setNode_SE(QuadTreeNode<T> node_SE) {
        this.node_SE = node_SE;
    }

    /**
     * Metoda pre nastavenie stvrteho syna
     * @param node_SW referencia na objekt stvrteho syna
     */
    public void setNode_SW(QuadTreeNode<T> node_SW) {
        this.node_SW = node_SW;
    }
    
    /**
     * Metoda pre pridanie elementu medzi elementy ulozene priamo vo vrchole
     * @param element 
     */
    public void addElement(T element) {
        this.elements.add(element);
    }
    
    /**
     * Metoda pre nacitanie elementu zo zoznamu elementov ulozenych vo vrchole pomocou jeho indexu
     * @param index index elementu ulozeneho vo vrchole
     * @return vrati najdeny element
     */
    public T getElementFromElements(int index) {
        return this.elements.get(index);
    }
    
    /**
     * Metoda pre odstranenie elementu zo zoznamu elementov ulozenych vo vrchole pomocou jeho indexu
     * @param index index elementu ulozeneho vo vrchole
     */
    public void removeElementFromElements(int index) {
        this.elements.remove(index);
    }
    
    /**
     * Metoda pre vycistenie zoznamu elementov ulozenych vo vrchole
     */
    protected void clearElements () {
        this.elements.clear();
    }

    /**
     * Metoda pre kontrolu ci je node prazdny (neobsahuje ziadne elementy ani referencie na synov) 
     * @return vrati true ak je node prazdny, false ak nie je prazdny
     */
    public boolean isNodeEmpty(){
        return (this.elements.isEmpty() && 
                this.node_NW == null &&
                this.node_NE == null &&
                this.node_SE == null &&
                this.node_SW == null );
    }
    
    /**
     * Metoda pre vlozenie elementu pod tento node
     * @param element element na vlozenie
     * @param maxSizeUnderNode maximalna velkost pod tymto nodom
     * @param numOfTopLvlElements referencia na pocet elementov na poslednej urovni
     * @return vrati true ak sa vlozenie elementu podarilo, false ak sa vlozenie elementu nepodarilo
     */
    protected boolean insert(T element, int maxSizeUnderNode, IntegerRef numOfTopLvlElements) {  
        QuadTreeNode actual;
        int actual_level = 0;
        boolean inserted = false;
        Area devided_space[];
        T elementToTransfer;
        
        actual = this;
        
        if (!this.space.check_if_object_fits(element.getSpace()))
            return false; //vkladany objekt sa nevojde pod tento node
        
        while (!inserted) {
            //cistenie pomocnych premennych
            devided_space = null;
            
            if (actual.isNodeEmpty()) { //node je prazdny
                actual.addElement(element);
                inserted = true;
            }
            else { //Delenie kvadrantu
                if (actual_level == maxSizeUnderNode) { //bola dosiahnuta maximalna velkost stromu
                    //dalsie delenie nie je mozne tak element vlozime do zoznamu vrchola
                    actual.addElement(element);
                    numOfTopLvlElements.add(1);
                    inserted = true;
                }
                else { //delenie kvadrantu je mozne
                    if (actual.getNode_NW() == null) { //Node este nie je podeleny
                        devided_space = actual.getSpace().devideTo4();
                        actual.setNode_NW( new QuadTreeNode<T>(devided_space[0]) );
                        actual.setNode_NE( new QuadTreeNode<T>(devided_space[1]) );
                        actual.setNode_SE( new QuadTreeNode<T>(devided_space[2]) );
                        actual.setNode_SW( new QuadTreeNode<T>(devided_space[3]) );
                        
                        elementToTransfer = (T) actual.getElementFromElements(0);
                        
                        //preradenie elementu so zoznamu vrchola k jednemu synovi
                        if ( actual.getNode_NW().getSpace().check_if_object_fits(elementToTransfer.getSpace()) ) {
                            actual.getNode_NW().addElement(elementToTransfer); // prida element k synovi
                            actual.removeElementFromElements(0); //vymaze ho zo svojho zoznamu
                        }
                        else if (actual.getNode_NE().getSpace().check_if_object_fits(elementToTransfer.getSpace())) {
                            actual.getNode_NE().addElement(elementToTransfer); // prida element k synovi
                            actual.removeElementFromElements(0); //vymaze ho zo svojho zoznamu
                        }
                        else if (actual.getNode_SE().getSpace().check_if_object_fits(elementToTransfer.getSpace())) {
                            actual.getNode_SE().addElement(elementToTransfer); // prida element k synovi
                            actual.removeElementFromElements(0); //vymaze ho zo svojho zoznamu
                        }
                        else if (actual.getNode_SW().getSpace().check_if_object_fits(elementToTransfer.getSpace())) {
                            actual.getNode_SW().addElement(elementToTransfer); // prida element k synovi
                            actual.removeElementFromElements(0); //vymaze ho zo svojho zoznamu
                        }
                        //ak sa nevojde ani do jednej podoblasti tak ho nechame v zozname kde bol
                    }
                    else { //Node je podeleny porovnavam postupne kde sa vojde
                        if ( actual.getNode_NW().getSpace().check_if_object_fits(element.getSpace()) ) {
                            actual = actual.getNode_NW();
                            actual_level++;
                        }
                        else if (actual.getNode_NE().getSpace().check_if_object_fits(element.getSpace())) {
                             actual = actual.getNode_NE();
                             actual_level++;
                        }
                        else if (actual.getNode_SE().getSpace().check_if_object_fits(element.getSpace())) {
                             actual = actual.getNode_SE();
                             actual_level++;
                        }
                        else if (actual.getNode_SW().getSpace().check_if_object_fits(element.getSpace())) {
                             actual = actual.getNode_SW();
                             actual_level++;
                        }
                        else { //Nevojde sa to cele ani do jednej podoblasti (pojde to teda do zoznamu objektov)
                            actual.addElement(element);
                            inserted = true;
                        }
                    }
                }
            }
        }
        return inserted;
    }
    
}
