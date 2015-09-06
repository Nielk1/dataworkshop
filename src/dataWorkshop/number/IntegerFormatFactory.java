package dataWorkshop.number;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;

import dataWorkshop.EnumerationError;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.DataNumber;
import dataWorkshop.data.encoding.GroupedIntegerEncoding;
import dataWorkshop.data.encoding.IntegerEncoding;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

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
public class IntegerFormatFactory implements XMLSerializeable {
    
    public final static String CLASS_NAME = "IntegerFormatFactory";
    
    public final static String BASE_TAG = "base";
    public final static String OFFSET_TAG = "offset";
    
    public final static String DECIMAL = "Decimal";
    public final static String BINARY = "Binary";
    public final static String HEXADECIMAL = "Hexadecimal";
    
    public final static HashMap BASENAME_TO_RADIX_MAP = new HashMap();
    static {
        BASENAME_TO_RADIX_MAP.put(BINARY, new java.lang.Integer(2));
        BASENAME_TO_RADIX_MAP.put(DECIMAL, new java.lang.Integer(10));
        BASENAME_TO_RADIX_MAP.put(HEXADECIMAL, new java.lang.Integer(16));
    }
    
    public final static HashMap BASENAME_TO_POSTFIX_MAP = new HashMap();
    static {
        BASENAME_TO_POSTFIX_MAP.put(BINARY, new Character('b'));
        BASENAME_TO_POSTFIX_MAP.put(DECIMAL, new Character('d'));
        BASENAME_TO_POSTFIX_MAP.put(HEXADECIMAL, new Character('h'));
    }
    
    public final static String BIT = "Bits";
    public final static String BYTES_BITS = "Bytes.Bits";
    public final static String MBYTES_KBYTES_BYTES_BITS = "MB.KB.Bytes.Bits";
    
    /*
     *  These are not to final bitGroups used for GroupedIntegerConverter.
     *  The final ones are calculated in getGroupedIntegerConverter using the
     *  complete bitsize of the converter.
     */
    public final static HashMap OFFSET_RULE_TO_GROUPING_MAP = new HashMap();
    static {
        OFFSET_RULE_TO_GROUPING_MAP.put(BIT, new int[0]);
        OFFSET_RULE_TO_GROUPING_MAP.put(BYTES_BITS, new int[]{3});
        OFFSET_RULE_TO_GROUPING_MAP.put(MBYTES_KBYTES_BYTES_BITS, new int[]{8,8,3});
    }
    
    public final static ArrayList validBases = new ArrayList();
    static {
        validBases.add(BINARY);
        validBases.add(DECIMAL);
        validBases.add(HEXADECIMAL);
    }
    
    public final static ArrayList validOffsetRules = new ArrayList();
    static {
        validOffsetRules.add(BIT);
        validOffsetRules.add(BYTES_BITS);
        validOffsetRules.add(MBYTES_KBYTES_BYTES_BITS);
    }
    
    public String offsetRule;
    public String base;
    
    /******************************************************************************
     *	Constructors
     */
    public IntegerFormatFactory() {
        this(HEXADECIMAL, BYTES_BITS);
    }
    
    public IntegerFormatFactory(String base, String offsetRule) {
        if (!validBases.contains(base)) {
            throw new EnumerationError(base, validBases.toArray());
        }
        if (!validOffsetRules.contains(offsetRule)) {
            throw new EnumerationError(offsetRule, validOffsetRules.toArray());
        }
        this.offsetRule = offsetRule;
        this.base = base;
    }
    
    /******************************************************************************
     *	XMLSerializable
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, BASE_TAG, getBase());
        XMLSerializeFactory.setAttribute(context, OFFSET_TAG, getOffsetRule());
    }
    
    public void deserialize(Element context) {
        setBase(XMLSerializeFactory.getAttribute(context, BASE_TAG));
        setOffsetRule(XMLSerializeFactory.getAttribute(context, OFFSET_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public IntegerFormat getUnsignedOffset() {
        return getUnsignedOffset(35);
    }
    
    public IntegerFormat getUnsignedOffset(int bitSize) {
        DataNumber number = DataNumber.getDataNumber(bitSize, false, false);
        DataEncoding converter = getGroupedIntegerConverter(getRadix(), getGrouping(), false, bitSize);
        return new IntegerFormat(number, converter, getPostfix());
    }
    
    public IntegerFormat getSignedOffset() {
        return getSignedOffset(35);
    }
    
    public IntegerFormat getSignedOffset(int bitSize) {
        DataNumber number = DataNumber.getDataNumber(bitSize, false, true);
        DataEncoding converter = getGroupedIntegerConverter(getRadix(), getGrouping(), true, bitSize);
        return new IntegerFormat(number, converter, getPostfix());
    }
    
    public IntegerFormat getUnsignedCount() {
        return getUnsignedCount(35);
    }
    
    public IntegerFormat getUnsignedCount(int bitSize) {
        DataNumber number = DataNumber.getDataNumber(bitSize, false, false);
        DataEncoding converter = DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), bitSize, false, false));
        return new IntegerFormat(number, converter, getPostfix());
    }
    
    public IntegerFormat getSignedCount() {
        return getSignedCount(35);
    }
    
    public IntegerFormat getSignedCount(int bitSize) {
        DataNumber number = DataNumber.getDataNumber(bitSize, false, true);
        DataEncoding converter = DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), bitSize, false, true));
        return new IntegerFormat(number, converter, getPostfix());
    }
    
    public String getBase() {
        return base;
    }
    
    public void setBase(String base) {
        this.base = base;
    }
    
    public String getOffsetRule() {
        return offsetRule;
    }
    
    public void setOffsetRule(String offsetRule) {
        this.offsetRule = offsetRule;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    private int[] getGrouping() {
        return (int[]) OFFSET_RULE_TO_GROUPING_MAP.get(offsetRule);
    }
    
    private int getRadix() {
        return ((java.lang.Integer)BASENAME_TO_RADIX_MAP.get(base)).intValue();
    }
    
    private char getPostfix() {
        return ((Character)BASENAME_TO_POSTFIX_MAP.get(base)).charValue();
    }
    
    private DataEncoding getGroupedIntegerConverter(int radix, int[] bitGroups, boolean signed, int bitSize) {
        int bitGroupSize = 0;
        for (int i = 0; i < bitGroups.length; i++) {
            bitGroupSize += bitGroups[i];
        }
        
        int[] calculatedBitGroups;
        
        if (bitGroupSize == bitSize) {
            calculatedBitGroups = bitGroups;
        }
        else if (bitGroupSize < bitSize) {
            calculatedBitGroups = new int[bitGroups.length + 1];
            calculatedBitGroups[0] = bitSize - bitGroupSize;
            System.arraycopy(bitGroups, 0, calculatedBitGroups, 1, bitGroups.length);
        }
        // (bitGroupSize > bitSize)
        else  {
            int firstGroup = 0;
            while (bitGroupSize > bitSize) {
                bitGroupSize -= bitGroups[firstGroup];
                firstGroup++;
            }
            firstGroup--;
            calculatedBitGroups = new int[bitGroups.length - firstGroup];
            calculatedBitGroups[0] = bitSize - bitGroupSize;
            System.arraycopy(bitGroups, firstGroup + 1, calculatedBitGroups, 1, calculatedBitGroups.length - 1);
        }
        
        return DataEncodingFactory.getDataEncoding(new GroupedIntegerEncoding(radix, calculatedBitGroups, false, signed));
    }
}