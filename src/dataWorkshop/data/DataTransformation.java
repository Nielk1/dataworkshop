package dataWorkshop.data;

import java.util.Arrays;

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
public class DataTransformation implements XMLSerializeable {
    
    public final static String CLASS_NAME = "DataTransformation";
    
    final static String NAME_TAG = "name";
    final static String TRANSFORMERS_TAG = "transformers";
    final static String BIT_SIZE_TAG = "bitSize";
    
    protected String name;
    protected long bitSize;
    protected DataTransformer[] transformers;
    
    /******************************************************************************
     *	Constructors
     */
    //dummy Constructor for xmlserializing
    public DataTransformation() {
        this("null", 1);
    }
    
    public DataTransformation(String name, long bitSize) {
        this(name, bitSize, new DataTransformer[0]);
    }
    
     public DataTransformation(String name, long bitSize, DataTransformer transformer) {
        this.name = name;
        this.bitSize = bitSize;
        this.transformers = new DataTransformer[1];
        transformers[0] = transformer;
    }
    
     public DataTransformation(String name, long bitSize, DataTransformer[] transformers) {
        this.name = name;
        this.bitSize = bitSize;
        this.transformers = transformers;
    }
    
     /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, NAME_TAG, name);
        XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, bitSize);
        XMLSerializeFactory.serialize(context, TRANSFORMERS_TAG, getDataTransformers());
    }
    
    public void deserialize(Element context) {
        name = XMLSerializeFactory.getAttribute(context, NAME_TAG);
        bitSize = XMLSerializeFactory.getAttributeAsLong(context, BIT_SIZE_TAG);
        transformers = (DataTransformer[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, TRANSFORMERS_TAG)).toArray(new DataTransformer[0]);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public Data transform(Data data, long bitOffset, long bitSize) throws Exception {
        Data result = data.copy(bitOffset, bitSize);
        for (int i = 0; i < transformers.length; i++) {
            /**
             *  :REFACTORING: MPA 2002-11-10
             *
             *  Each DataTransformer clones the Data and returns the modified clone.
             *  The cloning is now uneccessary (as the DataTransformation object does this)
             *  and should be cut out.
             */
            result = transformers[i].transform(result, 0, bitSize);
        }
        return result;
    }
    
    public String toString() {
        return name + " (" + getBitSize() + "-Bit)";
    }
    
    protected void setBitSize(long bitSize) {
        this.bitSize = bitSize;
    }
    
    public long getBitSize() {
        return bitSize;
    }
    
    public DataTransformer[] getDataTransformers() {
        return transformers;
    }
}