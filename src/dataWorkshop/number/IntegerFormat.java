package dataWorkshop.number;

import java.util.HashMap;

import org.w3c.dom.Element;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataNumber;
import dataWorkshop.xml.XMLSerializeable;
import dataWorkshop.xml.XMLSerializeableSingleton;

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
public class IntegerFormat implements XMLSerializeableSingleton {
     
    final static String CLASS_NAME = "IntegerFormat";
    
    final static String DATA_NUMBER_TAG = "dataNumber";
    final static String DATA_CONVERTER_TAG = "dataConverter";
    final static String POSTFIX_TAG = "postfix";
    
    DataNumber dataNumber;
    DataEncoding dataConverter;
    String postfix;
    
    static HashMap formattedNumbers = new HashMap();

    /******************************************************************************
     *	Constructors
     */
    public IntegerFormat() {
        this(null, null, ',');
    }
    
    public IntegerFormat(DataNumber dataNumber, DataEncoding dataConverter, char postfix) {
        this.dataNumber = dataNumber;
        this.dataConverter = dataConverter;
        this.postfix = (Character.valueOf(postfix)).toString();
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
    }
    
    public void deserialize(Element context) {
    }
    
    public XMLSerializeable getInstance(XMLSerializeable instance) {
        IntegerFormat number = (IntegerFormat) instance;
        return getIntegerFormat(number.getDataNumber(), number.getDataConverter(), number.getPostfix());
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static IntegerFormat getIntegerFormat(DataNumber dataNumber, DataEncoding dataConverter, char postfix) {
        String key = postfix + dataNumber.toString() + dataConverter.toString();
        IntegerFormat number = (IntegerFormat) formattedNumbers.get(key);
        if (number == null) {
            number = new IntegerFormat(dataNumber, dataConverter, postfix);
            formattedNumbers.put(key, number);
        }
        return number;
    }
    
    public DataNumber getDataNumber() {
        return dataNumber;
    }
    
    public DataEncoding getDataConverter() {
        return dataConverter;
    }
    
    public char getPostfix() {
        return postfix.charAt(0);
    }
    
    public String toString(long number, boolean padding) {
        char[] chars = dataConverter.encode(dataNumber.encode(number));
        
        if (!padding) {
            int paddingStart = 0;
            int paddingEnd = 0;
            if (dataNumber.isSigned()) {
                paddingStart++;
                paddingEnd++;
            }
            
            //We want to keep the last char even if it is a '0'
            while (chars[paddingEnd] == '0' && paddingEnd < chars.length -1) {
                paddingEnd++;
            }
            
            char[] unpaddedChars = new char[chars.length - (paddingEnd - paddingStart)];
            int index = 0;
            for (int i = 0; i < paddingStart; i++) {
                unpaddedChars[index] = chars[i];
                index++;
            }
            for (int i = paddingEnd; i < chars.length; i++) {
                unpaddedChars[index] = chars[i];
                index++;
            }
            return new String(unpaddedChars);
        }
        return new String(chars);
    }
}
