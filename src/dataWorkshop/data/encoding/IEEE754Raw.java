package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.data.DataEncodingFactory;

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
public class IEEE754Raw extends IEEE754 {
    
    public final static String CLASS_NAME = "IEEE754RawEncoding";
    public final static String NAME = "Real-Raw";
    
    DataEncoding exponentConverter;
    DataEncoding mantisseConverter;
    
    /******************************************************************************
     *	Constructors
     */
    public IEEE754Raw() {
        this(10, IEEE754.SHORT_REAL, true);
    }
    
    public IEEE754Raw(int radix, String precision, boolean littleEndian) {
        this(radix, ((Integer) NAME_TO_TYPE_MAPPING.get(precision)).intValue(), littleEndian);
    }
    
    public IEEE754Raw(int radix, int precision, boolean littleEndian) {
        super(NAME, radix, precision, littleEndian);
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
    }
    
    public void deserialize(Element context) {
        removeAllProperties();
        super.deserialize(context);
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
        
        System.arraycopy(encode(), 0, chars, dot, getDotSize());
        if (d.booleanValue(0)) {
            chars[dot] = '-';
        }
        else {
            chars[dot] = '+';
        }
        dot++;
        
        mantisseConverter.encode(d, 1 + getExponentBitSize(), chars, dot);
        dot += mantisseConverter.getDotSize();
        //skip 'e'
        dot++;
        exponentConverter.encode(d, 1, chars, dot);
    }
    
    public Data decode(char[] s) throws DataEncodingException {
        Data d = new Data();
        try {
            d.append(new Data(1, decodeSign(s, 0)));
            Data mantisseData = mantisseConverter.decode(s, 1);
            d.append(exponentConverter.decode(s, 1 + mantisseConverter.getDotSize() + 1));
            d.append(mantisseData);
        } catch (DataEncodingException e) {
            //create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
            char[] data = new char[getDotSize()];
            System.arraycopy(s, 0, data, 0, getDotSize());
			throw new DataEncodingException(this, data, e);
        }
        if (isLittleEndian()) {
            d.swapByteOrder();
        }
        return d;
    }
    
    public boolean isInputPoint(int dot) {
        if (encode()[dot] == '-') {
            return true;
        }
        return false;
    }
    
      public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof IEEE754Raw) {
            return super.equals(obj);
        }
        return false;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    private void recalc() {
        exponentConverter = DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), getExponentBitSize(), false, false));
        mantisseConverter = DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), getMantisseBitSize(), false, false));
        char[] empty = new char[1 + mantisseConverter.getDotSize() + 1 + exponentConverter.getDotSize()];
        for (int i = 0; i < empty.length; i++) {
            empty[i] = '-';
        }
        empty[1 + mantisseConverter.getDotSize()] = 'e';
        setEmptyChars(empty);
    }
}