package dataWorkshop.data;

import java.util.HashMap;
import java.util.Iterator;

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
public class DataToDataMapping implements XMLSerializeable {
    
    final static String CLASS_NAME = "DataToDataMapping";
    
    final static String FROM_TAG = "from";
    final static String TO_TAG = "to";
    final static String MAP_TAG = "Map";
    final static String MAPPING_TAG = "Mapping";
    
    DataEncoding converter = DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED);
    HashMap mapping;
    long bitSize;
    
    /******************************************************************************
     *	Constructors
     */
    public DataToDataMapping() {
        this(0);
    }
    
    public DataToDataMapping(long bitSize) {
        this(bitSize, new Data[0], new Data[0]);
    }
    
    public DataToDataMapping(long bitSize, Data[] searchData, Data[] replaceData) {
        mapping = new HashMap();
        this.bitSize = bitSize;
        for (int i = 0; i < searchData.length; i++) {
            mapping.put(searchData[i], replaceData[i]);
        }
    }
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        Element node;
        XMLSerializeFactory.serialize(context, converter);
        Element mappingNode = XMLSerializeFactory.addElement(context, MAPPING_TAG);
        XMLSerializeFactory.setAttribute(mappingNode, Data.BIT_SIZE_TAG, getBitSize());
        Iterator it = mapping.keySet().iterator();
        while (it.hasNext()) {
            Data key = (Data) it.next();
            node = XMLSerializeFactory.addElement(mappingNode, MAP_TAG);
            XMLSerializeFactory.setAttribute(node, FROM_TAG, Data.serialize(key, converter));
            XMLSerializeFactory.setAttribute(node, TO_TAG, Data.serialize(getReplaceData(key), converter));
        }
    }
    
    public void deserialize(Element context) {
        converter = (DataEncoding) XMLSerializeFactory.deserializeFirst(context);
        Element mappingNode = XMLSerializeFactory.getElement(context, MAPPING_TAG);
        bitSize = XMLSerializeFactory.getAttributeAsLong(mappingNode, Data.BIT_SIZE_TAG);
        mapping = new HashMap();
        Element[] maps = XMLSerializeFactory.getChildElements(mappingNode);
        for (int i = 0; i < maps.length; i++) {
            Data sData = Data.deserialize(bitSize, XMLSerializeFactory.getAttribute(maps[i], FROM_TAG), converter);
            Data rData = Data.deserialize(bitSize, XMLSerializeFactory.getAttribute(maps[i], TO_TAG), converter);
            mapping.put(sData, rData);
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public long getBitSize() {
        return bitSize;
    }
    
    public Data getReplaceData(Data key) {
        return (Data) mapping.get(key);
    }
    
    public void add(Data search, Data replace) {
        // assert searchData.getBitSize() == getBitSize();
        // assert replaceData.getBitSize() == getBitSize();
        mapping.put(search, replace);
    }
    
     /*
      *  Should be optimized
      */
    public int hashCode() {
        return 0;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof DataToDataMapping) {
            return mapping.equals((DataToDataMapping) obj);
        }
        return false;
    }
}