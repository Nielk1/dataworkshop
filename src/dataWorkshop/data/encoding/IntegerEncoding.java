package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.xml.XMLSerializeFactory;

/**
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
public class IntegerEncoding extends NumberEncoding {
    
    public final static String CLASS_NAME = "IntegerEncoding";
    public final static String NAME = "Integer";
    
    final static String SIGNED_TAG = "signed";
    final static String BIT_SIZE_TAG = "bitSize";
   
    boolean signed;
    
    transient private long maximum;
    transient private long minimum;
	transient private String maximumString;
	transient private String minimumString;
    
    public static int BIT_SIZE_MIN = 1;
    public static int BIT_SIZE_MAX = 63;
    
    /******************************************************************************
     *	Constructors
     */
    public IntegerEncoding() {
        this(2, 1, false, false);
    }
    
    public IntegerEncoding(int radix, int bitSize, boolean littleEndian, boolean signed) {
        super(NAME, radix, bitSize, littleEndian);
        setSigned(signed);
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
        XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, getBitSize());
        XMLSerializeFactory.setAttribute(context, SIGNED_TAG, signed);
    }
    
    public void deserialize(Element context) {
        removeAllProperties();
        super.deserialize(context);
        setBitSize(XMLSerializeFactory.getAttributeAsInt(context, BIT_SIZE_TAG));
        setSigned(XMLSerializeFactory.getAttributeAsBoolean(context, SIGNED_TAG));
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
        int startDot = dot;
        if (signed) {
            if (d.booleanValue(0)) {
                d.negate(0, getBitSize());
                chars[dot] = '-';
            }
            else {
                chars[dot] = '+';
            }
            startDot++;
        }
        
        //This will only work for bitSize <= 64
        long value = d.longValue(0, getBitSize());
        for (int i = dot + getDotSize() - 1; i >= startDot; i--) {
            //System.out.println(chars.length + " " + i + " " + value % radix);
            chars[i] = (char) charForValue[(int) (value % radix)];
            value = value / radix;
        }
    }
    
    public Data decode(char[] s) throws DataEncodingException {
        if (signed) {
            if (s[0] != '+' && s[0] != '-') {
                char[] data = new char[getDotSize()];
                System.arraycopy(s, 0, data, 0, getDotSize());
                throw new DataEncodingException(this, data, s[0]);
            }
        }
        
        int dot = 0;
        if (signed) {
            dot++;
        }
        
        long finalValue = parseCharArray(s, dot);
        
        if (finalValue > maximum) {
            char[] data = new char[getDotSize()];
            System.arraycopy(s, 0, data, 0, getDotSize());
            throw new DataEncodingException(this, new String(data), minimumString, maximumString);
        }
        
        if (signed && s[0] == '-') {
            if (0 - finalValue < minimum) {
                char[] data = new char[getDotSize()];
                System.arraycopy(s, 0, data, 0, getDotSize());
                throw new DataEncodingException(this, new String(data), minimumString, maximumString);
            }
        }
        
        Data d = new Data(getBitSize(), finalValue);
        
        if (signed && s[0] == '-') {
            d.negate(0, getBitSize());
        }
        
        if (isLittleEndian()) {
            d.swapByteOrder();
        }
        
        return d;
    }
    
    public boolean isSigned() {
        return signed;
    }
    
    public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof IntegerEncoding) {
            IntegerEncoding converter = (IntegerEncoding) obj;
            if (this.signed != converter.signed) {
                return false;
            }
            if (getBitSize() != converter.getBitSize()) {
                return false;
            }
            return super.equals(obj);
        }
        return false;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    protected void setSigned(boolean signed) {
        this.signed = signed;
         if (signed) {
            addProperty(SIGNED_TAG, "Signed");
        }
    }
    
    protected char[] encode(int value) {
        return encode(new Data(getBitSize(), value));
    }
    
    protected int getValue(Data d, long bitOffset) {
        return d.intValue(bitOffset, getBitSize());
    }
    
    protected long parseCharArray(char[] chars, int dotOffset) throws DataEncodingException {
        long finalValue = 0;
        long a = 1;
        int value;
        for (int i = dotOffset + getDotSize() -1; i >= dotOffset; i--) {
            value = valueForChar[chars[i]];
            if (value == -1 || value >= getRadix()) {
                char[] data = new char[getDotSize()];
                System.arraycopy(chars, dotOffset, data, 0, getDotSize());
                throw new DataEncodingException(this, data, chars[i]);
            }
            finalValue += (value * a);
            a *= getRadix();
        }
        return finalValue;
    }
    
    protected void recalc() {
        int bitSize = getBitSize();
		Data minData = null;
		Data maxData = null;
		
        if (!signed) {
            minimum = 0;
            maximum = Data.maxUnsignedNumber(bitSize);
        	minData = new Data(getBitSize());
        	maxData = new Data(minData);
        	maxData.not();
        }
        else {
            minimum = Data.minSignedNumber(bitSize);
            maximum = Data.maxSignedNumber(bitSize);
            minData = new Data(getBitSize());
            maxData = new Data(getBitSize());
            maxData.not();
            if (minData.getBitSize() > 0) {
            	minData.setBit(0, true);
            	maxData.setBit(0, false);
            }
        }
        
        long max = Math.max(Math.abs(minimum), maximum);
        long a = 1;
        int dotSize = 0;
        //We habe to check for a > 0 in case a overflows
        while (a <= max && a > 0) {
            dotSize++;
            a *= radix;
        }
        
        if (signed) {
            dotSize++;
        }
        
        char[] e = new char[dotSize];
        for (int i = 0; i < dotSize; i++) {
            e[i] = '-';
        }
        setEmptyChars(e);
        
		minimumString = new String(encode(minData));
		maximumString = new String(encode(maxData));
		addProperty("Range", minimumString + " to " + maximumString);
    }
}