package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.xml.XMLSerializeFactory;

/**
 *  The fraction encoding used is: 0.bit * 1 + 1.bit * 1/2 + 2.bit * 1/4 ...
 * <p>
 * DataWorkshop - a binary data editor 
 * <br>
 * Copyright (C) 2000, 2004  Martin Pape (martin.pape@gmx.de)
 * <br>
 * <br>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * </p>
 */
public class FractionEncoding extends NumberEncoding {
    
    public final static String CLASS_NAME = "FractionEncoding";
    public final static String NAME = "Fraction";
    
    final static String FRACTION_DIGITS_TAG = "fractionDigits";
    final static String INT_BITS_TAG = "intBits";
    final static String FRACTION_BITS_TAG = "fractionBits";
    
    final static char SEPARATOR = ',';
    
    int intBits;
    int fractionBits;
    int fractionDigits;

    transient IntegerEncoding intConverter;
    
    /******************************************************************************
     *	Constructors
     */
    public FractionEncoding() {
        this(2, 1, false, 5, 6);
    }
    
    public FractionEncoding(int radix, int intBits, boolean littleEndian, int fractionBits, int fractionDigits) {
        super(NAME, radix, intBits + fractionBits, littleEndian);
        this.intBits = intBits;
        this.fractionBits = fractionBits;
        this.fractionDigits = fractionDigits;
        recalc();
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
      public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.setAttribute(context, FRACTION_DIGITS_TAG, getFractionDigits());
        XMLSerializeFactory.setAttribute(context, INT_BITS_TAG, getIntBits());
        XMLSerializeFactory.setAttribute(context, FRACTION_BITS_TAG, getFractionBits());
    }
    
    public void deserialize(Element context) {
        removeAllProperties();
        super.deserialize(context);
        fractionDigits = XMLSerializeFactory.getAttributeAsInt(context, FRACTION_DIGITS_TAG);
        intBits = XMLSerializeFactory.getAttributeAsInt(context, INT_BITS_TAG);
        fractionBits = XMLSerializeFactory.getAttributeAsInt(context, FRACTION_BITS_TAG);
        setName(NAME);
        recalc();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void encode(Data data, long offset, char[] chars, int dot) {
        Data d = data.copy(offset, getBitSize());
        if (isLittleEndian()) {
            d.swapByteOrder();
        }
        double value = getValue(d, offset);
        System.arraycopy(encode(value), 0, chars, dot, getDotSize());
    }
    
    public Data decode(char[] chars) throws DataEncodingException {
        Data intData = intConverter.decode(chars, 0);
        double fraction = getFractionValue(chars, intConverter.getDotSize() + 1);
        double fractionBit = 1;
        Data fractionData = new Data(fractionBits);
        for (int i = 0; i < fractionBits; i++) {
            fractionBit = fractionBit / 2;
            if (fraction > fractionBit) {
                fractionData.setBit(i, true);
            }
        }
        intData.append(fractionData);
        
        if (isLittleEndian()) {
            intData.swapByteOrder();
        }
        return intData;
    }
    
    public int getFractionDigits() {
        return fractionDigits;
    }
    
    public int getFractionBits() {
        return fractionBits;
    }
    
    public int getIntBits() {
        return intBits;
    }
    
      /******************************************************************************
     *	Protected Methods
     */
    protected double getFractionValue(char[] chars, int dotOffset) throws DataEncodingException {
        double value = 0.0;
        double position = 1;
        for (int i = 0; i < getFractionDigits(); i++) {
            position = position / getRadix();
            int charValue = valueForChar[chars[i + dotOffset]];
            if (charValue == -1 || charValue > radix) {
                char[] data = new char[getDotSize()];
                System.arraycopy(chars, 0, data, 0, getDotSize());
                throw new DataEncodingException(this, data, chars[i + dotOffset]);
            }
            value += charValue * position;
        }
        return value;
    }
    
    protected int getIntValue(char[] chars, int dotOffset) throws DataEncodingException {
        return (intConverter.decode(chars, dotOffset)).intValue();
    }
    
    protected double getValue(char[] chars, int dotOffset) throws DataEncodingException {
        double value = (double) getIntValue(chars, dotOffset);
        int dot =  dotOffset + intConverter.getDotSize() + 1;
        value += getFractionValue(chars, dot);
        return value;
    }
    
    protected double getValue(Data data, long offset) {
        double value = 0;
        value += intConverter.getValue(data, offset);
        double fraction = 1;
        for (int i = intBits; i < getBitSize(); i++) {
            fraction = fraction * 0.5;
            if (data.booleanValue(i + offset)) {
                value += fraction;
            }
        }
        return value;
    }
    
    protected char[] encode(double value) {  
        char[] chars = encode();
        int intValue = (int) Math.floor(value);
        System.arraycopy(intConverter.encode(intValue), 0, chars, 0, intConverter.getDotSize());
        value -= intValue;
        double charValue = 0;
        for (int i = intConverter.getDotSize() + 1; i < getDotSize(); i++) {
            value = value * radix;
            charValue = Math.floor(value);
            chars[i] = charForValue[(int) charValue];
            value -= charValue;
        }
        return chars;
    }
    
    public boolean isInputPoint(int dot) {
        return encode()[dot] == '-';
    }
    
      public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof FractionEncoding) {
            FractionEncoding converter = (FractionEncoding) obj;
            if (this.intBits != converter.intBits) {
                return false;
            }
            if (this.fractionBits != converter.fractionBits) {
                return false;
            }
            if (this.fractionDigits != converter.fractionDigits) {
                return false;
            }
            return super.equals(obj);
        }
        return false;
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void recalc() {
        setBitSize(getIntBits() + getFractionBits());
        intConverter = new IntegerEncoding(getRadix(), getIntBits(), false, false);
        char[] empty = new char[intConverter.getDotSize() + 1 + getFractionDigits()];
        for (int i = 0; i < empty.length; i++) {
            empty[i] = '-';
        }
        empty[intConverter.getDotSize()] = SEPARATOR;
        setEmptyChars(empty);
        addProperty("Point", getIntBits() + "," + getFractionBits());
        addProperty(FRACTION_DIGITS_TAG, Integer.toString(getFractionDigits()));
    }
}