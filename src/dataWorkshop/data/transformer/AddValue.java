package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataNumber;
import dataWorkshop.data.DataTransformer;
import dataWorkshop.xml.XMLSerializeFactory;

/**
 *  value if negative is subtracted
 *  <br>
 *   value if positive is added
 *  <br>
 *   bitsize > 0
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
public class AddValue extends DataTransformer {
    
    long value;
    DataNumber dataNumber;
    
    final static String VALUE_TAG = "value";
    final static String CLASS_NAME = "AddValue";
    
    /******************************************************************************
     *	Constructors
     */
    public AddValue() {
        super();
    }
    
    public AddValue(DataNumber dataNumber, long value) {
        super(dataNumber.getBitSize());
        this.dataNumber = dataNumber;
        this.value = value;
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.setAttribute(context, VALUE_TAG, getValue());
        XMLSerializeFactory.serialize(context, getDataNumber());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setValue(XMLSerializeFactory.getAttributeAsLong(context, VALUE_TAG));
        setDataNumber((DataNumber) XMLSerializeFactory.deserializeFirst(context));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public long getValue() {
        return value;
    }
    
    public void setValue(long value) {
        this.value = value;
    }
    
    public DataNumber getDataNumber() {
        return dataNumber;
    }
    
    public void setDataNumber(DataNumber number) {
        dataNumber = number;
        setBitSize(number.getBitSize());
    }
    
    public Data transform(Data data, long bitOffset, long bitSize) {
    	Data result = new Data();
    	long max = bitOffset + bitSize;
    	for (long i = bitOffset; i < max; i += getBitSize()) {
    		result.append(dataNumber.add(data, value, i));
    	}
    	return result;
    }
}