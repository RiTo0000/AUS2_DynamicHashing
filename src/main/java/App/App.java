/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package App;

import DynamicHashing.DynamicHashing;
import QuadTree.Area;
import QuadTree.Coordinate;
import QuadTree.Direction;
import QuadTree.Point;
import QuadTree.QuadTree;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author namer
 */
public class App {
    private static final int size = 10;
    
    private QuadTree<Property> properties;
    private QuadTree<Land> lands;
    private DynamicHashing<PropertyDH> propertiesDH;
    private DynamicHashing<LandDH> landsDH;
    private Area space;
    
    private int maxPropertyID;
    private int maxLandID;
    
    public App(Area space) {
        this.space = space;
        this.properties = new QuadTree<Property>(this.size, space.getStart(), space.getEnd());
        this.lands = new QuadTree<Land>(this.size, space.getStart(), space.getEnd());
        
        try {
            this.propertiesDH = new DynamicHashing<PropertyDH>("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\properties.bin",
                    "C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\properties_second.bin", PropertyDH.class, 1, 2, 2);
            this.landsDH = new DynamicHashing<LandDH>("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\lands.bin", 
                                            "C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\lands_second.bin", LandDH.class, 1, 2, 2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.maxPropertyID = 0;
    }

    public int getMaxPropertyID() {
        return this.maxPropertyID;
    }

    public void setMaxPropertyID(int maxPropertyID) {
        this.maxPropertyID = maxPropertyID;
    }

    public int getMaxLandID() {
        return this.maxLandID;
    }

    public void setMaxLandID(int maxLandID) {
        this.maxLandID = maxLandID;
    }
    
    

    public ArrayList<Property> findProperties(Point gps) {
        Area point = new Area(gps, gps);
        return properties.findElementsInArea(point);
    }

    public ArrayList<Land> findLands(Point gps) {
        Area point = new Area(gps, gps);
        return lands.findElementsInArea(point);
    }
    
    public ArrayList<Object> findObjects(Area space) {
        ArrayList<Object> objects;
        
        objects = (ArrayList<Object>) (Object) properties.findElementsInArea(space);
        objects.addAll((ArrayList<Object>) (Object) lands.findElementsInArea(space));
        
        return objects;
    }
    
    public boolean addProperty(PropertyDH property) throws Exception {
        LandDH landUnderProp;
        ArrayList<LandDH> landsUnderPropDH = new ArrayList<>();
        
        ArrayList<Land> landsUnderProp = this.lands.findElementsInArea(property.getSpace());
        
        for (Land land : landsUnderProp) {
            landUnderProp = landsDH.find(new LandDH(land.getKey(), land.getSpace(), 0, ""));
            
            if (landUnderProp.getProperties().size() < LandDH.maxProperties) { //kontrola ci sa vojde do zoznamu nehnutelnosti na najdenom pozemku
                landUnderProp.addProperty(property.getIDRegNumber()); //pridanie nehnutelnosti na pozemok
                property.addLand(land.getKey()); //pridanie pozemku na nehnutelnost
                landsUnderPropDH.add(landUnderProp);
            }
            
            if (landsUnderPropDH.size() == PropertyDH.maxLands) { //ak uz mame dost pozemkov tak ukoncime prehladavanie zoznamu
                break;
            }
        }
        
        propertiesDH.insert(property);

        for (LandDH landDH : landsUnderPropDH) {
            landsDH.edit(landDH);
        }
        
        properties.insert(new Property(property.getSpace(), property.getIDRegNumber()), false); //zapisanie do QuadStromu
        
        return true;
    }
    
    public boolean createProperty(Area space, int regNumber, String description) throws Exception {
        this.maxPropertyID++;
        PropertyDH property = new PropertyDH(this.maxPropertyID, space, regNumber, description);
        
        return addProperty(property);
    }
    
    public boolean addLand(LandDH land) throws Exception {    
        PropertyDH propertyOnLand;
        ArrayList<PropertyDH> propertiesOnLandDH = new ArrayList<>();
        
        ArrayList<Property> propertiesOnLand = this.properties.findElementsInArea(land.getSpace());
        
        for (Property prop : propertiesOnLand) {
            propertyOnLand = propertiesDH.find(new PropertyDH(prop.getKey(), prop.getSpace(), 0, ""));
            
            if (propertyOnLand.getLands().size() < PropertyDH.maxLands) { //kontrola ci sa vojde do zoznamu pozemkov pod najdenou nehnutelnostou
                propertyOnLand.addLand(land.getIDLandNumber()); //pridanie pozemku pod nehnutelnost
                land.addProperty(prop.getKey()); //pridanie nehnutelnosti na pozemok
                propertiesOnLandDH.add(propertyOnLand);
            }
            
            if (propertiesOnLandDH.size() == LandDH.maxProperties) { //ak uz mame dost nehnutelnosti tak ukoncime prehladavanie zoznamu
                break;
            }
        }
        
        landsDH.insert(land);

        for (PropertyDH propertyDH : propertiesOnLandDH) {
            propertiesDH.edit(propertyDH);
        }
        
        lands.insert(new Land(land.getSpace(), land.getIDLandNumber()), false);  //zapisanie do QuadStromu
        
        return true;
    }
    
    public boolean createLand(Area space, int landNumber, String description) throws Exception {
        this.maxLandID++;
        LandDH land = new LandDH(this.maxLandID, space, landNumber, description);
        
        return this.addLand(land);
    }

    public ArrayList<PropertyDH> getProperties() throws IOException {
        return this.propertiesDH.readAllRecords();
    }

    public ArrayList<LandDH> getLands() throws IOException {
        return this.landsDH.readAllRecords();
    }
    
    public PropertyDH getProperty(int propertyID) throws Exception {
        return this.propertiesDH.find(new PropertyDH(propertyID, null, 0, ""));
    }
    
    public LandDH getLand(int landID) throws Exception {
        return this.landsDH.find(new LandDH(landID, null, 0, ""));
    }
    
    /**
     * Bezpecne vyradi nehnutelnost, ktoru dostal v parametri
     * @param property nehnutelnost na vyradenie
     * @return true ak sa vyradenie nehnutelnosti podarilo, false inak
     */
    public boolean removeProperty(PropertyDH property) throws Exception {
        PropertyDH removedProperty = this.propertiesDH.delete(property);
        
        if (removedProperty == null) { //pri vymaze bola chyba
            return false;
        }
        
        ArrayList<Integer> landsUnderProp = removedProperty.getLands();
        LandDH land;
        
        for (Integer landID : landsUnderProp) {
            land = this.landsDH.find(new LandDH(landID, null, 0, "")); //do noveho potrebujeme naplnit iba kluc na to aby vedel vyhladavat a porovnavat
            
            if (land != null) {
                land.removeProperty(property.getIDRegNumber());
                this.landsDH.edit(land);
            }
        }
        
        this.properties.delete(property.getSpace(), property.getIDRegNumber());
        
        return true;
    }
    
    /**
     * Bezpecne vyradi parcelu, ktoru dostal v parametri
     * @param land parcela na vyradenie
     * @return true ak sa vyradenie parcely podarilo, false inak
     */
    public boolean removeLand(LandDH land) throws Exception {
        LandDH removedLand = this.landsDH.delete(land);
        
        if (removedLand == null) { //pri vymaze bola chyba
            return false;
        }
        
        ArrayList<Integer> propertiesOnLand = removedLand.getProperties();
        PropertyDH property;
        
        for (Integer propertyID : propertiesOnLand) {
            property = this.propertiesDH.find(new PropertyDH(propertyID, null, 0, "")); //do noveho potrebujeme naplnit iba kluc na to aby vedel vyhladavat a porovnavat            
            if (property != null) {
                property.removeLand(land.getIDLandNumber());
                this.propertiesDH.edit(property);
            }
        }
        
        this.lands.delete(land.getSpace(), land.getIDLandNumber());
        
        return true;
    }
    
    /**
     * Metoda pre editaciu nehnutelnosti (vratane oblasti na ktorej lezi)
     * @param property nehnutelnost na editaciu
     * @param newSpace nova oblast na ktorej lezi
     * @param newRegNum nove registracne cislo nehnutelnosti
     * @param newDescription novy popis nehnutelnosti
     * @return true ak sa editacia podarila, false inak
     */
    public boolean editProperty(PropertyDH property, Area newSpace, int newRegNum, String newDescription) throws Exception {
        LandDH landUnderProp;
        ArrayList<LandDH> landsUnderPropDH = new ArrayList<>();
            
        if (this.properties.delete(property.getSpace(), property.getIDRegNumber())) {
            property.setSpace(newSpace);
            property.setRegNumber(newRegNum);
            property.setDescription(newDescription);
            
            //uprava udajov v DH
            //odmazanie stareho prepojenia medzi nehnutelnostou a pozemkom
            for (Integer landID : property.getLands()) {
                landUnderProp = landsDH.find(new LandDH(landID, null, 0, ""));
                landUnderProp.removeProperty(property.getIDRegNumber());
                this.landsDH.edit(landUnderProp);
            }
            property.setLands(new ArrayList<>()); //vyprazdni
            
            //pridanie nehnutelnosti na pozemky tam kde ma byt a naopak
            ArrayList<Land> landsUnderProp = this.lands.findElementsInArea(property.getSpace());

            for (Land land : landsUnderProp) {
                landUnderProp = landsDH.find(new LandDH(land.getKey(), land.getSpace(), 0, ""));

                if (landUnderProp.getProperties().size() < LandDH.maxProperties) { //kontrola ci sa vojde do zoznamu nehnutelnosti na najdenom pozemku
                    landUnderProp.addProperty(property.getIDRegNumber()); //pridanie nehnutelnosti na pozemok
                    property.addLand(land.getKey()); //pridanie pozemku na nehnutelnost
                    landsUnderPropDH.add(landUnderProp);
                }

                if (landsUnderPropDH.size() == PropertyDH.maxLands) { //ak uz mame dost pozemkov tak ukoncime prehladavanie zoznamu
                    break;
                }
            }

            propertiesDH.edit(property);

            for (LandDH landDH : landsUnderPropDH) {
                landsDH.edit(landDH);
            }

            this.properties.insert(new Property(property.getSpace(), property.getIDRegNumber()), false); //zapisanie upravej nehnutelnosti do QuadStromu
            
            return true;
            
        }
        else
            return false;
    }
    
    /**
     * Metoda pre editaciu pozemku (vratane oblasti na ktorej lezi)
     * @param land pozemok na editaciu
     * @param newSpace nova oblast na ktorej lezi
     * @param newLandNum  nove registracne cislo pozemku
     * @param newDescription novy popis pozemku
     * @return true ak sa editacia podarila, false inak
     */
    public boolean editLand(LandDH land, Area newSpace, int newLandNum, String newDescription) throws Exception {
        PropertyDH propertyOnLand;
        ArrayList<PropertyDH> propertiesOnLandDH = new ArrayList<>();
            
        if (this.lands.delete(land.getSpace(), land.getIDLandNumber())) {
            land.setSpace(newSpace);
            land.setIDLandNumber(newLandNum);
            land.setDescription(newDescription);
            
            //uprava udajov v DH
            //odmazanie stareho prepojenia medzi nehnutelnostou a pozemkom
            for (Integer propID : land.getProperties()) {
                propertyOnLand = propertiesDH.find(new PropertyDH(propID, null, 0, ""));
                propertyOnLand.removeLand(land.getIDLandNumber());
                this.propertiesDH.edit(propertyOnLand);
            }
            land.setProperties(new ArrayList<>()); //vyprazdni
            
            //pridanie nehnutelnosti na pozemky tam kde ma byt a naopak
            ArrayList<Property> propertiesOnLand = this.properties.findElementsInArea(land.getSpace());

            for (Property property : propertiesOnLand) {
                propertyOnLand = propertiesDH.find(new PropertyDH(property.getKey(), property.getSpace(), 0, ""));

                if (propertyOnLand.getLands().size() < PropertyDH.maxLands) { //kontrola ci sa vojde do zoznamu pozemkov na najdenej nehnutelnosti
                    propertyOnLand.addLand(land.getIDLandNumber()); //pridanie pozemku na nehnutelnost
                    land.addProperty(property.getKey()); //pridanie nehnutelnosti na pozemok
                    propertiesOnLandDH.add(propertyOnLand);
                }

                if (propertiesOnLandDH.size() == LandDH.maxProperties) { //ak uz mame dost nehnutelnosti tak ukoncime prehladavanie zoznamu
                    break;
                }
            }

            landsDH.edit(land);

            for (PropertyDH propDH : propertiesOnLandDH) {
                propertiesDH.edit(propDH);
            }

            lands.insert(new Land(land.getSpace(), land.getIDLandNumber()), false); //zapisanie upraveneho pozemku do QuadStromu
            
            return true;
            
        }
        else
            return false;
    }
    
    /**
     * Metoda vycisti vsetky data aplikacie (aplikacia bude v inicialnom stave)
     * @throws IOException 
     */
    public void clearAllData() throws IOException {
        this.propertiesDH.clearAllData();
        this.landsDH.clearAllData();
        
        this.lands.clearAllData();
        this.properties.clearAllData();
        
        this.maxPropertyID = 0;
        this.maxLandID = 0;
    }
    
    public void generateObject(boolean generateProperties, int count) throws Exception { 
        Random rand = new Random();
        double startXVal;
        double endXVal;
        double startYVal;
        double endYVal;
        double hlpVal;
        double xAxisRange = this.space.getEnd().getX().getRealValueOnAxis() - this.space.getStart().getX().getRealValueOnAxis();
        double yAxisRange = this.space.getEnd().getY().getRealValueOnAxis() - this.space.getStart().getY().getRealValueOnAxis();
        
        for (int i = 0; i < count; i++) {
            startXVal = (rand.nextDouble()*xAxisRange) + this.space.getStart().getX().getRealValueOnAxis();
            startYVal = (rand.nextDouble()*yAxisRange) + this.space.getStart().getY().getRealValueOnAxis();
            endXVal = (rand.nextDouble()*xAxisRange) + this.space.getStart().getX().getRealValueOnAxis();
            endYVal = (rand.nextDouble()*yAxisRange) + this.space.getStart().getY().getRealValueOnAxis();

            if (endXVal < startXVal) {
                hlpVal = startXVal;
                startXVal = endXVal;
                endXVal = hlpVal;
            }

            if (endYVal < startYVal) {
                hlpVal = startYVal;
                startYVal = endYVal;
                endYVal = hlpVal;
            }
            
            if (generateProperties) {
                //generovanie nehnutelnosti
                this.createProperty(new Area(new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, startXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, startYVal)),
                        new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, endXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, endYVal))), i, ("Pr. num.: " + i));
            }
            else {
                //generovanie parciel
                this.createLand(new Area(new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, startXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, startYVal)),
                        new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, endXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, endYVal))), i, ("L. num.: " + i));
            }
            
        }
    }
    
    public void saveToFile(String directory) throws IOException {
        this.propertiesDH.saveNodesToFile("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\nodes_propertiesDH.txt");
        this.landsDH.saveNodesToFile("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\nodes_landsDH.txt");
        
        String line;
        
        ArrayList<Property> properties = this.properties.getAllElements();
        ArrayList<Land> lands = this.lands.getAllElements();
        
        ArrayList<Land> landsUnderProperty;
        ArrayList<Property> propertiesOnLand;
        
        //App
        
        BufferedWriter fileApp = new BufferedWriter(new FileWriter( directory + "\\app.csv"));
        
        line = this.space.getStart().getX().getDirection().toString() + ";" +
                this.space.getStart().getX().getRoundedValue(4)+ ";" +
                this.space.getStart().getY().getDirection().toString() + ";" +
                this.space.getStart().getY().getRoundedValue(4) + ";" +
                this.space.getEnd().getX().getDirection().toString() + ";" +
                this.space.getEnd().getX().getRoundedValue(4) + ";" +
                this.space.getEnd().getY().getDirection().toString() + ";" +
                this.space.getEnd().getY().getRoundedValue(4) + ";" +
                this.maxPropertyID + ";" +
                this.maxLandID;
        
        line += "\n";
        
        fileApp.write(line);
        
        fileApp.flush();
        fileApp.close();        
    }
    
    public static App loadFromFile(String appFile) throws FileNotFoundException, IOException {
        
        String line;
        
        String[] app = {};
        ArrayList<String[]> properties = new ArrayList<String[]>();
        ArrayList<String[]> lands = new ArrayList<String[]>();
        
        App application;
        

        BufferedReader fileApp = new BufferedReader(new FileReader(appFile));

        if( (line = fileApp.readLine()) != null ) {
           app = line.split(";");
        }
        else {
            //dakde problem
            return null;
        }
        
        application = new App(new Area(new Point(new Coordinate(Direction.getDirectFromString(app[0]), Double.valueOf(app[1])), 
                                                new Coordinate(Direction.getDirectFromString(app[2]), Double.valueOf(app[3]))), 
                                        new Point(new Coordinate(Direction.getDirectFromString(app[4]), Double.valueOf(app[5])), 
                                                new Coordinate(Direction.getDirectFromString(app[6]), Double.valueOf(app[7])))));
        
        application.setMaxPropertyID(Integer.parseInt(app[8]));
        application.setMaxLandID(Integer.parseInt(app[9]));
        
        application.propertiesDH.loadNodesFromFile("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\nodes_propertiesDH.txt");
        application.landsDH.loadNodesFromFile("C:\\D\\Desktop\\School\\4.Rocnik\\AUS2\\Semestralka2\\Files\\nodes_landsDH.txt");
        
        //Naplnenie QuadStromu
        //Nehnutelnosti
        ArrayList<PropertyDH> propertiesQT = application.propertiesDH.readAllRecords();
        for (PropertyDH propertyDH : propertiesQT) {
            application.properties.insert(new Property(propertyDH.getSpace(), propertyDH.getIDRegNumber()), false);
        }
        
        //Pozemky
        ArrayList<LandDH> landsQT = application.landsDH.readAllRecords();
        for (LandDH landDH : landsQT) {
            application.lands.insert(new Land(landDH.getSpace(), landDH.getIDLandNumber()), false);
        }
        
        return application;
    }
    
    public void printWholeFilesToConsole() throws IOException {
//        System.out.println("MainFile (properties):");
//        System.out.println(this.propertiesDH.readWholeMainFile());
//        System.out.println("SecondFile (properties):");
//        System.out.println(this.propertiesDH.readWholeSecondFile());
        
        System.out.println("MainFile (lands):");
        System.out.println(this.landsDH.readWholeMainFile());
        System.out.println("SecondFile (lands):");
        System.out.println(this.landsDH.readWholeSecondFile());
    }
    
    public void closeApp() throws IOException {
        this.propertiesDH.closeFiles();
        this.landsDH.closeFiles();
    }
}
