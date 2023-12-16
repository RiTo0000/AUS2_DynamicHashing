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
        
        return true;
    }
    
    public boolean createProperty(Area space, int regNumber, String description) throws Exception {
        this.maxPropertyID++;
        PropertyDH property = new PropertyDH(this.maxPropertyID, space, regNumber, description);
        
        return addProperty(property);
    }
    
//    public boolean addLand(Land land) {        
//        ArrayList<Property> propertiesOnLand = this.properties.findElementsInArea(land.getSpace());
//        land.setProperties(propertiesOnLand);
//        
//        //do vsetkych najdenych nehnutelnosti musim pridat aj ze stoja na tejto (novej) parcele
//        for (Property property : propertiesOnLand) {
//            if (!property.addLand(land)) {
//                return false; //nepodarilo sa pridat parcelu pod nehnutelnost
//            }
//        }
//        
//        return this.lands.insert(land);
//    }
    
//    public boolean createLand(Area space, int landNumber, String description) {
//        Land land = new Land(space, landNumber, description);
//        
//        return this.addLand(land);
//    }

    public QuadTree<Property> getProperties() {
        return this.properties;
    }

    public QuadTree<Land> getLands() {
        return this.lands;
    }
    
    /**
     * Bezpecne vyradi nehnutelnost, ktoru dostal v parametri
     * @param property nehnutelnost na vyradenie
     * @return true ak sa vyradenie nehnutelnosti podarilo, false inak
     */
//    public boolean removeProperty(Property property) {
//        ArrayList<Land> landsUnderProp = property.getLands();
//        
//        for (Land land : landsUnderProp) { //odstrani referencie na objekt predtym ako odstrani samotny objekt aby neboli nullptr
//            if (!land.getProperties().remove(property)) {
//                return false; //chyba pri vyradeni nehnutelnosti
//            }
//        }
//        
//        return this.properties.delete(property.getSpace(), property.getKey());
//    }
    
    /**
     * Bezpecne vyradi parcelu, ktoru dostal v parametri
     * @param land parcela na vyradenie
     * @return true ak sa vyradenie parcely podarilo, false inak
     */
//    public boolean removeLand(Land land) {
//        ArrayList<Property> propertiesOnLand = land.getProperties();
//        
//        for (Property property : propertiesOnLand) { //odstrani referencie na objekt predtym ako odstrani samotny objekt aby neboli nullptr
//            if (!property.getLands().remove(land)) {
//                return false; //chyba pri vyradeni parcely
//            }
//        }
//        
//        return this.lands.delete(land.getSpace(), land.getKey());
//    }
    
//    public void generateObject(boolean generateProperties, int count) { //TODO
//        Random rand = new Random();
//        double startXVal;
//        double endXVal;
//        double startYVal;
//        double endYVal;
//        double hlpVal;
//        double xAxisRange = this.space.getEnd().getX().getRealValueOnAxis() - this.space.getStart().getX().getRealValueOnAxis();
//        double yAxisRange = this.space.getEnd().getY().getRealValueOnAxis() - this.space.getStart().getY().getRealValueOnAxis();
//        
//        for (int i = 0; i < count; i++) {
//            startXVal = (rand.nextDouble()*xAxisRange) + this.space.getStart().getX().getRealValueOnAxis();
//            startYVal = (rand.nextDouble()*yAxisRange) + this.space.getStart().getY().getRealValueOnAxis();
//            endXVal = (rand.nextDouble()*xAxisRange) + this.space.getStart().getX().getRealValueOnAxis();
//            endYVal = (rand.nextDouble()*yAxisRange) + this.space.getStart().getY().getRealValueOnAxis();
//
//            if (endXVal < startXVal) {
//                hlpVal = startXVal;
//                startXVal = endXVal;
//                endXVal = hlpVal;
//            }
//
//            if (endYVal < startYVal) {
//                hlpVal = startYVal;
//                startYVal = endYVal;
//                endYVal = hlpVal;
//            }
//            
//            if (generateProperties) {
//                //generovanie nehnutelnosti
//                this.createProperty(new Area(new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, startXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, startYVal)),
//                        new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, endXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, endYVal))), i, ("Property number: " + i));
//            }
//            else {
//                //generovanie parciel
//                this.createLand(new Area(new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, startXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, startYVal)),
//                        new Point(Coordinate.getCoordinateFromRealValueOnAxis(true, endXVal), Coordinate.getCoordinateFromRealValueOnAxis(false, endYVal))), i, ("Land number: " + i));
//            }
//            
//        }
//    }
    
//    public void saveToFile(String directory) throws IOException {
//        String line;
//        
//        ArrayList<Property> properties = this.properties.getAllElements();
//        ArrayList<Land> lands = this.lands.getAllElements();
//        
//        ArrayList<Land> landsUnderProperty;
//        ArrayList<Property> propertiesOnLand;
//        
//        //App
//        
//        BufferedWriter fileApp = new BufferedWriter(new FileWriter( directory + "\\app.csv"));
//        
//        line = this.space.getStart().getX().getDirection().toString() + ";" +
//                this.space.getStart().getX().getRoundedValue(4)+ ";" +
//                this.space.getStart().getY().getDirection().toString() + ";" +
//                this.space.getStart().getY().getRoundedValue(4) + ";" +
//                this.space.getEnd().getX().getDirection().toString() + ";" +
//                this.space.getEnd().getX().getRoundedValue(4) + ";" +
//                this.space.getEnd().getY().getDirection().toString() + ";" +
//                this.space.getEnd().getY().getRoundedValue(4);
//        
//        line += "\n";
//        
//        fileApp.write(line);
//        
//        fileApp.flush();
//        fileApp.close();
//        
//        //Nehnutelnosti
//        
//        BufferedWriter fileProp = new BufferedWriter(new FileWriter( directory + "\\properties.csv"));
//        
//        line = this.properties.getSpace().getStart().getX().getDirection().toString() + ";" +
//                this.properties.getSpace().getStart().getX().getRoundedValue(4)+ ";" +
//                this.properties.getSpace().getStart().getY().getDirection().toString() + ";" +
//                this.properties.getSpace().getStart().getY().getRoundedValue(4) + ";" +
//                this.properties.getSpace().getEnd().getX().getDirection().toString() + ";" +
//                this.properties.getSpace().getEnd().getX().getRoundedValue(4) + ";" +
//                this.properties.getSpace().getEnd().getY().getDirection().toString() + ";" +
//                this.properties.getSpace().getEnd().getY().getRoundedValue(4) + ";" +
//                this.properties.getMaxSize() + ";" +
//                this.properties.isOptimal();
//        
//        line += "\n";
//            
//        fileProp.write(line);
//        
//        for (Property property : properties) {
//            line = property.getRegNumber() + ";" +
//                    property.getDescription() + ";" +
//                    property.getSpace().getStart().getX().getDirection().toString() + ";" +
//                    property.getSpace().getStart().getX().getRoundedValue(4)+ ";" +
//                    property.getSpace().getStart().getY().getDirection().toString() + ";" +
//                    property.getSpace().getStart().getY().getRoundedValue(4) + ";" +
//                    property.getSpace().getEnd().getX().getDirection().toString() + ";" +
//                    property.getSpace().getEnd().getX().getRoundedValue(4) + ";" +
//                    property.getSpace().getEnd().getY().getDirection().toString() + ";" +
//                    property.getSpace().getEnd().getY().getRoundedValue(4) + ";";
//            
////            landsUnderProperty = property.getLands();
////            
////            for (Land landUnderProperty : landsUnderProperty) {
////                line += landUnderProperty.getKey(); 
////            }
//            
//            line += "\n";
//            
//            fileProp.write(line);
//        }
//        
//        fileProp.flush();
//        
//        fileProp.close();
//        
//        
//        //Pozemky
//
//        BufferedWriter fileLand = new BufferedWriter(new FileWriter( directory + "\\lands.csv"));
//        
//        line = this.lands.getSpace().getStart().getX().getDirection().toString() + ";" +
//                this.lands.getSpace().getStart().getX().getRoundedValue(4)+ ";" +
//                this.lands.getSpace().getStart().getY().getDirection().toString() + ";" +
//                this.lands.getSpace().getStart().getY().getRoundedValue(4) + ";" +
//                this.lands.getSpace().getEnd().getX().getDirection().toString() + ";" +
//                this.lands.getSpace().getEnd().getX().getRoundedValue(4) + ";" +
//                this.lands.getSpace().getEnd().getY().getDirection().toString() + ";" +
//                this.lands.getSpace().getEnd().getY().getRoundedValue(4) + ";" +
//                this.lands.getMaxSize() + ";" +
//                this.lands.isOptimal();
//        
//        line += "\n";
//            
//        fileLand.write(line);
//        
//        for (Land land : lands) {
//            line = land.getLandNumber()+ ";" +
//                    land.getDescription() + ";" +
//                    land.getSpace().getStart().getX().getDirection().toString() + ";" +
//                    land.getSpace().getStart().getX().getRoundedValue(4)+ ";" +
//                    land.getSpace().getStart().getY().getDirection().toString() + ";" +
//                    land.getSpace().getStart().getY().getRoundedValue(4) + ";" +
//                    land.getSpace().getEnd().getX().getDirection().toString() + ";" +
//                    land.getSpace().getEnd().getX().getRoundedValue(4) + ";" +
//                    land.getSpace().getEnd().getY().getDirection().toString() + ";" +
//                    land.getSpace().getEnd().getY().getRoundedValue(4) + ";";
//            
////            propertiesOnLand = land.getProperties();
////            
////            for (Property propertyOnLand : propertiesOnLand) {
////                line += propertyOnLand.getKey() + ";"; 
////            }
//            
//            line += "\n";
//            
//            fileLand.write(line);
//        }
//        
//        fileLand.flush();
//        
//        fileLand.close();
//        
//    }
//    
//    public static App loadFromFile(String appFile, String propertiesFile, String landsFile) throws FileNotFoundException, IOException {
//        String line;
//        
//        String[] app = {};
//        ArrayList<String[]> properties = new ArrayList<String[]>();
//        ArrayList<String[]> lands = new ArrayList<String[]>();
//        
//        App application;
//        
////        ArrayList<Land> landsUnderProperty;
////        ArrayList<Property> propertiesOnLand;
//
//        BufferedReader fileApp = new BufferedReader(new FileReader(appFile));
//
//        if( (line = fileApp.readLine()) != null ) {
//           app = line.split(";");
//        }
//        else {
//            //dakde problem
//            return null;
//        }
//
//        
//        BufferedReader fileProp = new BufferedReader(new FileReader(propertiesFile));
//
//        while ((line = fileProp.readLine()) != null) {
//            properties.add(line.split(";"));
//        }
//        
//        BufferedReader fileLand = new BufferedReader(new FileReader(landsFile));
//
//        while ((line = fileLand.readLine()) != null) {
//            lands.add(line.split(";"));
//        }
//        
//        application = new App(new Area(new Point(new Coordinate(Direction.getDirectFromString(app[0]), Double.valueOf(app[1])), 
//                                                new Coordinate(Direction.getDirectFromString(app[2]), Double.valueOf(app[3]))), 
//                                        new Point(new Coordinate(Direction.getDirectFromString(app[4]), Double.valueOf(app[5])), 
//                                                new Coordinate(Direction.getDirectFromString(app[6]), Double.valueOf(app[7])))));
//        
//        //Nehnutelnosti
//        application.properties.setSpace(new Area(new Point(new Coordinate(Direction.getDirectFromString(properties.get(0)[0]), Double.valueOf(properties.get(0)[1])), 
//                                                            new Coordinate(Direction.getDirectFromString(properties.get(0)[2]), Double.valueOf(properties.get(0)[3]))), 
//                                                    new Point(new Coordinate(Direction.getDirectFromString(properties.get(0)[4]), Double.valueOf(properties.get(0)[5])), 
//                                                            new Coordinate(Direction.getDirectFromString(properties.get(0)[6]), Double.valueOf(properties.get(0)[7]))))); 
//        application.properties.setMaxSize(Integer.valueOf(properties.get(0)[8]));
//        
//        for (int i = 1; i < properties.size(); i++) {
//            application.addProperty(new Property(new Area(new Point(new Coordinate(Direction.getDirectFromString(properties.get(i)[2]), Double.valueOf(properties.get(i)[3])), 
//                                                            new Coordinate(Direction.getDirectFromString(properties.get(i)[4]), Double.valueOf(properties.get(i)[5]))), 
//                                                    new Point(new Coordinate(Direction.getDirectFromString(properties.get(i)[6]), Double.valueOf(properties.get(i)[7])), 
//                                                            new Coordinate(Direction.getDirectFromString(properties.get(i)[8]), Double.valueOf(properties.get(i)[9])))), 
//                                            Integer.valueOf(properties.get(i)[0]), properties.get(i)[1]));
//            
//        }
//        
//        application.properties.setOptimal(Boolean.parseBoolean(properties.get(0)[9]));
//
//        
//        //Pozemky
//        application.lands.setSpace(new Area(new Point(new Coordinate(Direction.getDirectFromString(lands.get(0)[0]), Double.valueOf(lands.get(0)[1])), 
//                                                            new Coordinate(Direction.getDirectFromString(lands.get(0)[2]), Double.valueOf(lands.get(0)[3]))), 
//                                                    new Point(new Coordinate(Direction.getDirectFromString(lands.get(0)[4]), Double.valueOf(lands.get(0)[5])), 
//                                                            new Coordinate(Direction.getDirectFromString(lands.get(0)[6]), Double.valueOf(lands.get(0)[7]))))); 
//        application.lands.setMaxSize(Integer.valueOf(lands.get(0)[8]));
//        
//        for (int i = 1; i < lands.size(); i++) {
//            application.addLand(new Land(new Area(new Point(new Coordinate(Direction.getDirectFromString(lands.get(i)[2]), Double.valueOf(lands.get(i)[3])), 
//                                                            new Coordinate(Direction.getDirectFromString(lands.get(i)[4]), Double.valueOf(lands.get(i)[5]))), 
//                                                    new Point(new Coordinate(Direction.getDirectFromString(lands.get(i)[6]), Double.valueOf(lands.get(i)[7])), 
//                                                            new Coordinate(Direction.getDirectFromString(lands.get(i)[8]), Double.valueOf(lands.get(i)[9])))), 
//                                            Integer.valueOf(lands.get(i)[0]), lands.get(i)[1]));
//            
//        }
//        
//        application.lands.setOptimal(Boolean.parseBoolean(lands.get(0)[9]));
//        
//        return application;
//    }
    
    
}
