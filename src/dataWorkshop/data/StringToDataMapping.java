package dataWorkshop.data;

import org.w3c.dom.Element;

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
public class StringToDataMapping implements XMLSerializeable {
    
    public final static String CLASS_NAME = "StringToDataMapping";
    
    final static String BIT_SIZE_TAG = "bitSize";
    final static String MAP_TAG = "Map";
    final static String MAPPING_TAG = "Mapping";
    final static String ENCODED_DATA_TAG = "encodedData";
    final static String NAME_TAG = "name";
    
    DataEncoding converter = DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED);;
    Data[] data;
    String[] names;
    
    /******************************************************************************
     *	Constructors
     */
    public StringToDataMapping() {
        this(new Data[0], new String[0]);
    }
    
    public StringToDataMapping(Data[] data, String[] names) {
        this.data = data;
        this.names = names;
    }
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        Element node;
        String[] names = getNames();
        Data[] values = getData();
        XMLSerializeFactory.serialize(context, converter);
        Element mappingNode = XMLSerializeFactory.addElement(context, MAPPING_TAG);
        XMLSerializeFactory.setAttribute(mappingNode, BIT_SIZE_TAG, getBitSize());
        for (int i = 0; i < values.length; i++) {
            node = XMLSerializeFactory.addElement(mappingNode, MAP_TAG);
            XMLSerializeFactory.setAttribute(node, NAME_TAG, names[i]);
            XMLSerializeFactory.setAttribute(node, ENCODED_DATA_TAG, Data.serialize(values[i], converter));
        }
    }
    
    public void deserialize(Element context) {
        converter = (DataEncoding) XMLSerializeFactory.deserializeFirst(context);
        Element mappingNode = XMLSerializeFactory.getElement(context, MAPPING_TAG);
        long bitSize = XMLSerializeFactory.getAttributeAsLong(mappingNode, BIT_SIZE_TAG);
        Element[] maps = XMLSerializeFactory.getChildElements(mappingNode);
        String[] names = new String[maps.length];
        Data[] values = new Data[maps.length];
        for (int i = 0; i < maps.length; i++) {
            names[i] = XMLSerializeFactory.getAttribute(maps[i], NAME_TAG);
            values[i] = Data.deserialize(bitSize, XMLSerializeFactory.getAttribute(maps[i], ENCODED_DATA_TAG), converter);
        }
        setNames(names);
        setData(values);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public Data[] getData() {
        return data;
    }
    
    public void setData(Data[] data) {
        this.data = data;
    }
    
    public String[] getNames() {
        return names;
    }
    
    public void setNames(String[] names) {
        this.names = names;
    }
    
    public Data getData(String string) {
        for (int i = 0; i < names.length; i++) {
            if (names[i] == string) {
                return data[i];
            }
        }
        return null;
    }
    
    public String getName(Data d) {
        for (int i = 0; i < names.length; i++) {
            if (d.equals(data[i])) {
                return names[i];
            }
        }
        return null;
    }
    
    public int getMappings() {
        return data.length;
    }
    
    public long getBitSize() {
        if (data.length == 0) {
            return 0;
        }
        return data[0].getBitSize();
    }
    
     /**
      *  :TODO:Martin Pape:Jun 19, 2003: Should be optimized
      */
    public int hashCode() {
        return 0;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof StringToDataMapping) {
            StringToDataMapping mapping = (StringToDataMapping) obj;
            if (data.length != mapping.data.length) {
                return false;
            }
            for (int i = 0; i < data.length; i++) {
                if (!names[i].equals(mapping.names[i])) {
                    return false;
                }
                if (!data[i].equals(mapping.data[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}