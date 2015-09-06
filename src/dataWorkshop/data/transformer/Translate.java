package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataToDataMapping;
import dataWorkshop.data.DataTransformer;
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
public class Translate extends DataTransformer {
    
    final static String CLASS_NAME = "Translate";
    
    DataToDataMapping mapping;
    
    /******************************************************************************
     *	Constructors
     */
    public Translate() {
        super();
    }
    
    public Translate(DataToDataMapping mapping) {
        super(mapping.getBitSize());
       this.mapping = mapping;
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.serialize(context, mapping);
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        mapping = (DataToDataMapping) XMLSerializeFactory.deserializeFirst(context);
        setBitSize((int) mapping.getBitSize());
    }
    
    public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
  public Data transform(Data data, long bitOffset, long bitSize) {
        Data result = data.copy(bitOffset, bitSize);
        Data replace;
        long offset = 0;
        while (offset < result.getBitSize()) {
            Data key = result.copy(offset, getBitSize());
            replace = mapping.getReplaceData(key);
            if (replace != null) {
                result.replace(offset, getBitSize(), replace);
                offset += replace.getBitSize();
            }
            else {
                offset += getBitSize();
            }
        }
        return result;
    }
}