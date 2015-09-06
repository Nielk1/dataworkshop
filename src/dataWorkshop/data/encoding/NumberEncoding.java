package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

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
public abstract class NumberEncoding extends AbstractNumberEncoding {
    
    final static String RADIX_TAG = "radix";
    int radix;
    
    final static String nameForRadix[] = {
        null, null, "Binary", "Ternary", "Quaternary", "Quinary", "Senary", "Septenary", "Octal", "Nonary",
        "Decimal", "Undenary", "Duodecimal", "Base 13", "Base 14", "Base 15", "Hexadecimal", "Base 17", "Base 18", "Base 19"
    };
    
      public final static char[] charForValue = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
        'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z'
    };
    
    public final static int[] valueForChar = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15 ,16, 17, 18, 19, 20, 21, 22, 23, 24,
        25, 26, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    
    public static int RADIX_MIN = 1;
    public static int RADIX_MAX = nameForRadix.length - 1;
    
    /******************************************************************************
     *	Constructors
     */
    public NumberEncoding(String name, int radix, int bitSize, boolean littleEndian) {
        super(name, bitSize, littleEndian);
        setRadix(radix);
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public void serialize(Element context) {
    	super.serialize(context);
        XMLSerializeFactory.setAttribute(context, RADIX_TAG, getRadix());
    }
    
    public void deserialize(Element context) {
    	super.deserialize(context);
        setRadix(XMLSerializeFactory.getAttributeAsInt(context, RADIX_TAG));
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public int getRadix() {
        return radix;
        
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof NumberEncoding) {
            NumberEncoding converter = (NumberEncoding) obj;
            if (this.radix != converter.radix) {
                return false;
            }
            return super.equals(obj);
        }
        return false;
    }
    
     /******************************************************************************
     *	Protected Methods
     */
    protected void setRadix(int radix) {
        XMLSerializeFactory.checkBounds(radix, 2, nameForRadix.length, RADIX_TAG);
        this.radix = radix;
        addProperty(RADIX_TAG, nameForRadix[getRadix()]);
    }
    
    protected boolean decodeSign(char[] chars, int dot) throws DataEncodingException {
        if (chars[dot] == '+') {
            return false;
         }
         else if (chars[dot] == '-') {
            return true;
         }
         else {
            char[] data = new char[1];
            data[0] = chars[dot];
            throw new DataEncodingException(this, data, chars[dot]);
         }
    }
}