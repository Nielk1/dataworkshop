package dataWorkshop.data.view;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
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
public class DataEncodingField extends DataField {
    
    public final static String CLASS_NAME = "DataEncodingField";
    protected DataEncoding converter;

    /******************************************************************************
     *	Constructors
     */
    public DataEncodingField() {
        this(DEFAULT_LABEL, 0, 0, DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_16_BIG_SIGNED));
    }
    
    public DataEncodingField(String label, long bitOffset, long bitSize, DataEncoding converter) {
        super(label, bitOffset, bitSize);
		assert (converter != null);
        this.converter = converter;
    }
       
     /******************************************************************************
     *	XML Serializeable Interface
     */
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.serialize(context, getDataConverter());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setDataConverter((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
    }
    
    public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void replace(DataFrame node) {
        super.replace(node);
        DataEncodingField field = (DataEncodingField) node;
        converter = field.converter;
    }
    
    public boolean equals(Object o) {
        if (o instanceof DataEncodingField) {
            DataEncodingField node = (DataEncodingField) o;
            if (converter != node.converter) {
                return false;
            }
            return super.equals(o);
        }
        return false;
    }
    
    public DataEncoding getDataConverter() {
        return converter;
    }
    
    public void setDataConverter(DataEncoding converter) {
        this.converter = converter;
    }
    
    public String render(Data data) {
        return data.serialize(converter, getBitRange());
    }
}