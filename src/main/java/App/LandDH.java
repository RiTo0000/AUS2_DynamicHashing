/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import DynamicHashing.IRecord;
import QuadTree.Area;
import QuadTree.Coordinate;
import QuadTree.Direction;
import QuadTree.Point;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/**
 *
 * @author namer
 */
public class LandDH implements IRecord {
    
    private static final int descLength = 11;
    public static final int maxProperties = 5;
    
    private int IDLandNumber; //kluc
    private int landNumber;
    private String description; //max 11 znakov
    private Area space;
    private ArrayList<Integer> properties; //max 5 zaznamov
    
    public LandDH() {
        this.IDLandNumber = 0;
        this.space = null;
        this.landNumber = 0;
        this.description = "";
        
        this.properties = new ArrayList<>();
    }
    
    public LandDH(int IDLandNumber, Area space, int landNumber, String description) {
        this.IDLandNumber = IDLandNumber;
        this.space = space;
        this.landNumber = landNumber;
        this.description = setDescWithProperLength(description);
        
        this.properties = new ArrayList<>();
    }
    
    public void addProperty(int IDProperty) {
        this.properties.add(IDProperty);
    }
    
    public boolean removeProperty(Integer propertyID) {
        return this.properties.remove(propertyID);
    }

    public int getIDLandNumber() {
        return this.IDLandNumber;
    }

    public void setIDLandNumber(int IDLandNumber) {
        this.IDLandNumber = IDLandNumber;
    }

    public int getLandNumber() {
        return this.landNumber;
    }

    public void setLandNumber(int landNumber) {
        this.landNumber = landNumber;
    }

    public Area getSpace() {
        return this.space;
    }

    public void setSpace(Area space) {
        this.space = space;
    }

    public ArrayList<Integer> getProperties() {
        return this.properties;
    }

    public void setProperties(ArrayList<Integer> properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = setDescWithProperLength(description);
        
    }
    
    /**
     * nastavi poznamku s maximalnou velkostou ak by nahodou presiahla
     * @param description poznamka
     * @return 
     */
    private static String setDescWithProperLength( String description) {
        if (description.length() > descLength) {
            return description.substring(0, descLength);
        }
        else {
            return description;
        }
    }

    @Override
    public boolean equals(IRecord object) {
        LandDH element = (LandDH) object;
        return this.IDLandNumber == element.IDLandNumber;
    }

    @Override
    public BitSet getHash() {
        return BitSet.valueOf(new long[]{this.IDLandNumber});
    }

    @Override
    public int getSize() { 
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + descLength +
                //Area
                1 + Double.BYTES + 1 + Double.BYTES + //Start
                1 + Double.BYTES + 1 + Double.BYTES + //End
                Integer.BYTES + //Pocet nehnutelnosti
                ( maxProperties * Integer.BYTES ) ; //Properties (nehnutelnosti)
    }

    @Override
    public byte[] toByteArray() {
        byte[] result = new byte[this.getSize()];
        byte[] tmp;
        int index = 0;
        
        tmp = ByteBuffer.allocate(4).putInt(this.IDLandNumber).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        tmp = ByteBuffer.allocate(4).putInt(this.landNumber).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        tmp = ByteBuffer.allocate(4).putInt(description.length()).array(); //pocet platnych bitov description
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        tmp = this.description.getBytes();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        if (tmp.length < descLength) { //navysenie indexu podla potreby a aktualnej dlzky description
            index += descLength - tmp.length;
        }
        
        //AREA
        //Start
        //X
        result[index] = this.space.getStart().getX().getDirection().toString().getBytes()[0]; //mozem brat nulty lebo viem ze bude iba jeden
        index++;
        tmp = ByteBuffer.allocate(Double.BYTES).putDouble(this.space.getStart().getX().getValue()).array(); //pocet platnych bitov description
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        //Y
        result[index] = this.space.getStart().getY().getDirection().toString().getBytes()[0]; //mozem brat nulty lebo viem ze bude iba jeden
        index++;
        tmp = ByteBuffer.allocate(Double.BYTES).putDouble(this.space.getStart().getY().getValue()).array(); //pocet platnych bitov description
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        //End
        //X
        result[index] = this.space.getEnd().getX().getDirection().toString().getBytes()[0]; //mozem brat nulty lebo viem ze bude iba jeden
        index++;
        tmp = ByteBuffer.allocate(Double.BYTES).putDouble(this.space.getEnd().getX().getValue()).array(); //pocet platnych bitov description
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        //Y
        result[index] = this.space.getEnd().getY().getDirection().toString().getBytes()[0]; //mozem brat nulty lebo viem ze bude iba jeden
        index++;
        tmp = ByteBuffer.allocate(Double.BYTES).putDouble(this.space.getEnd().getY().getValue()).array(); //pocet platnych bitov description
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        //Properties
        tmp = ByteBuffer.allocate(4).putInt(this.properties.size()).array(); //pocet platnych zaznamov properties
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        for (Integer propertyIDRegNum : this.properties) {
            tmp = ByteBuffer.allocate(4).putInt(propertyIDRegNum).array(); //ID nehnutelnosti ktora lezi na pozemku
            for (byte b : tmp) {
                result[index] = b;
                index++;
            }
        }
        
        
        return result;
    }

    @Override
    public void fromByteArray(byte[] input) {
        int index = 0;
        
        byte[] tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        this.IDLandNumber = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        this.landNumber = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        int actualDescLength = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        tmp = Arrays.copyOfRange(input, index, index + actualDescLength);
        this.description = new String(tmp); //opak string.getBytes()
        index += descLength; //navisim o maximalny descLength 
        
        //AREA
        //Start
        //X
        Direction startXDir = Direction.getDirectFromString(new String(Arrays.copyOfRange(input, index, index + 1)));
        index++;
        tmp = Arrays.copyOfRange(input, index, index + Double.BYTES);
        double startXVal = ByteBuffer.wrap(tmp).getDouble();
        index += Double.BYTES;
        //Y
        Direction startYDir = Direction.getDirectFromString(new String(Arrays.copyOfRange(input, index, index + 1)));
        index++;
        tmp = Arrays.copyOfRange(input, index, index + Double.BYTES);
        double startYVal = ByteBuffer.wrap(tmp).getDouble();
        index += Double.BYTES;
        //End
        //X
        Direction endXDir = Direction.getDirectFromString(new String(Arrays.copyOfRange(input, index, index + 1)));
        index++;
        tmp = Arrays.copyOfRange(input, index, index + Double.BYTES);
        double endXVal = ByteBuffer.wrap(tmp).getDouble();
        index += Double.BYTES;
        //Y
        Direction endYDir = Direction.getDirectFromString(new String(Arrays.copyOfRange(input, index, index + 1)));
        index++;
        tmp = Arrays.copyOfRange(input, index, index + Double.BYTES);
        double endYVal = ByteBuffer.wrap(tmp).getDouble();
        index += Double.BYTES;
        
        //nastavenie space
        this.space = new Area(new Point(new Coordinate(startXDir, startXVal), new Coordinate(startYDir, startYVal)),
                                new Point(new Coordinate(endXDir, endXVal), new Coordinate(endYDir, endYVal)));
        
        //Properties
        tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        int actualPropLength = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        for (int i = 0; i < actualPropLength; i++) {
            tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
            this.properties.add(ByteBuffer.wrap(tmp).getInt());
            index += Integer.BYTES;
        }
        
    }

    @Override
    public IRecord createClass() {
        return new LandDH();
    }

    @Override
    public String recordToString() { //TODO mozno ani nebude treba
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public String[] getInfo() {
        int hlpNum;
        double startX;
        double endX;
        double startY;
        double endY;
        
        hlpNum = (int) (this.getSpace().getStart().getX().getValue() * 100.00);
        startX = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getStart().getY().getValue() * 100.00);
        startY = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getEnd().getX().getValue() * 100.00);
        endX = ((double)hlpNum)/100.00;
        hlpNum = (int) (this.getSpace().getEnd().getY().getValue() * 100.00);
        endY = ((double)hlpNum)/100.00;
        
        String[] info = new String[5];
        
        info[0] = Integer.toString(this.IDLandNumber);
        info[1] = Integer.toString(this.landNumber);
        info[2] = this.description;
        info[3] = "X: " + this.getSpace().getStart().getX().getDirection() + " " + startX + 
                  " Y: " + this.getSpace().getStart().getY().getDirection() + " " + startY;
        info[4] = "X: " + this.getSpace().getEnd().getX().getDirection() + " " + endX + 
                  " Y: " + this.getSpace().getEnd().getY().getDirection() + " " + endY;
        
        return info;
    }
    
}
