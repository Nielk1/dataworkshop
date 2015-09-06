package dataWorkshop.data.encoding;

import java.util.HashMap;

import org.w3c.dom.Element;

import dataWorkshop.EnumerationError;
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
public abstract class IEEE754 extends NumberEncoding {
    
    final static String PRECISION_TAG = "precision";
    
    public final static int SHORT_REAL = 0;
    public final static int LONG_REAL = 1;
    public final static int TEMPORARY_REAL = 2;
    public final static String[] NAMES = {
        "Short",
        "Long",
        "Temporary",
    };
    static HashMap NAME_TO_TYPE_MAPPING = new HashMap();
    static {
        NAME_TO_TYPE_MAPPING.put(NAMES[0], new Integer(0));
        NAME_TO_TYPE_MAPPING.put(NAMES[1], new Integer(1));
        NAME_TO_TYPE_MAPPING.put(NAMES[2], new Integer(2));
    }
    final static int[] BIT_SIZE = {32, 64, 80};
    final static int[] MANTISSE_BIT_SIZE = {23, 52, 63};
    final static int[] EXPONENT_BIT_SIZE = {8, 11, 15};
    
    static String NAME = "Real (IEEE-754)";
    
    transient int mantisseBitSize;
    transient int exponentBitSize;
    transient int type = 0;
    
    /******************************************************************************
     *	Constructors
     */
    public IEEE754(String name, int radix, int precision, boolean littleEndian) {
        super(name, radix, BIT_SIZE[precision], littleEndian);
        setPrecision(NAMES[precision]);
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.setAttribute(context, PRECISION_TAG, getPrecision());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setPrecision(XMLSerializeFactory.getAttribute(context, PRECISION_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public String getPrecision() {
        return NAMES[getRealType()];
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof IEEE754) {
            IEEE754 converter = (IEEE754) obj;
            if (this.type != converter.type) {
                return false;
            }
            return super.equals(obj);
        }
        return false;
    }
    
    /******************************************************************************
     *	Protected Methods
     */
     protected int getMantisseBitSize() {
        return mantisseBitSize;
    }
    
    protected int getExponentBitSize() {
        return exponentBitSize;
    }
    
    protected int getRealType() {
        return type;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    private void setPrecision(String name) {
        if (!NAME_TO_TYPE_MAPPING.containsKey(name)) {
            throw new EnumerationError(name, NAME_TO_TYPE_MAPPING.keySet().toArray());
        }
        type = ((Integer) NAME_TO_TYPE_MAPPING.get(name)).intValue();
        setBitSize(BIT_SIZE[type]);
        mantisseBitSize = MANTISSE_BIT_SIZE[type];
        exponentBitSize = EXPONENT_BIT_SIZE[type];
    }
}