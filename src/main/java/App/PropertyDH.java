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
public class PropertyDH implements IRecord{
    
    private static final int descLength = 15;
    
    private int IDRegNumber; //kluc
    private int regNumber;
    private String description; //max 15 znakov
    private Area space;
    private ArrayList<Integer> lands; //max 6 zaznamov
    
    public PropertyDH() {
        
    }
    
    public PropertyDH(int IDRegNumber, Area space, int regNumber, String description) {
        this.IDRegNumber = IDRegNumber;
        this.space = space;
        this.regNumber = regNumber;
        this.description = description;
        
        this.lands = new ArrayList<>();
    }

    @Override
    public boolean equals(IRecord object) {
        PropertyDH element = (PropertyDH) object;
        return this.IDRegNumber == element.IDRegNumber;
    }

    @Override
    public BitSet getHash() {
        return BitSet.valueOf(new long[]{this.IDRegNumber});
    }

    @Override
    public int getSize() { 
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + descLength +
                //Area
                1 + Double.BYTES + 1 + Double.BYTES + //Start
                1 + Double.BYTES + 1 + Double.BYTES + //End
                Integer.BYTES + //Pocet pozemkov
                ( 6 * Integer.BYTES ) ; //Lands (pozemky)
    }

    @Override
    public byte[] toByteArray() {
        byte[] result = new byte[this.getSize()];
        byte[] tmp;
        int index = 0;
        
        tmp = ByteBuffer.allocate(4).putInt(this.IDRegNumber).array();
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        tmp = ByteBuffer.allocate(4).putInt(this.regNumber).array();
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
        
        //Lands
        tmp = ByteBuffer.allocate(4).putInt(this.lands.size()).array(); //pocet platnych zaznamov lands
        for (byte b : tmp) {
            result[index] = b;
            index++;
        }
        
        for (Integer propertyIDRegNum : this.lands) {
            tmp = ByteBuffer.allocate(4).putInt(propertyIDRegNum).array(); //ID pozemku ktory lezi pod nehnutelnostou
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
        this.IDRegNumber = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        this.regNumber = ByteBuffer.wrap(tmp).getInt();
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
        
        //Lands
        tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
        int actualPropLength = ByteBuffer.wrap(tmp).getInt();
        index += Integer.BYTES;
        
        for (int i = 0; i < actualPropLength; i++) {
            tmp = Arrays.copyOfRange(input, index, index + Integer.BYTES);
            this.lands.add(ByteBuffer.wrap(tmp).getInt());
            index += Integer.BYTES;
        }
    }

    @Override
    public IRecord createClass() {
        return new PropertyDH();
    }

    @Override
    public String recordToString() { //TODO mozno ani nebude treba
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
