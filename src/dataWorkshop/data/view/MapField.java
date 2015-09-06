package dataWorkshop.data.view;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.StringToDataMapping;
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
public class MapField extends DataField {
    
    public final static String CLASS_NAME = "MapField";
    
    final static String UNDEFINED = "Undefined";
    
    private StringToDataMapping mapping = new StringToDataMapping();
    
    /******************************************************************************
     *	Constructors
     */
    public MapField() {
        super();
    }
    
     /******************************************************************************
     *	XML Serializeable Interface
     */
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.serialize(context, getMapping());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setMapping((StringToDataMapping) XMLSerializeFactory.deserializeFirst(context));
    }
    
    public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
     public void replace(DataFrame node) {
        super.replace(node);
        MapField field = (MapField) node;
        mapping = field.mapping;
    }
    
    public StringToDataMapping getMapping() {
        return mapping;
    }
    
    public void setMapping(StringToDataMapping mapping) {
        this.mapping = mapping;
    }
     
    /*
     *  Should be optimized
     */
    public int hashCode() {
        return 0;
    }
    
    public boolean equals(Object o) {
        if (o instanceof MapField) {
            MapField node = (MapField) o;
            if (!mapping.equals(node.mapping)) {
                return false;
            }
            return super.equals(o);
        }
        return false;
    }
    
    public String[] getValueNames() {
        return mapping.getNames();
    }
    
    public String[] getPossibleValueNames() {
        String[] names = mapping.getNames();
        String[] possibleValueNames = new String[names.length + 1];
        System.arraycopy(names, 0, possibleValueNames, 0, names.length);
        possibleValueNames[possibleValueNames.length -1] = UNDEFINED;
        return possibleValueNames;
    }
    
    public Data[] getValues() {
        return mapping.getData();
    }
    
    public Data getValue(String string) {
        return mapping.getData(string);
    }
    
    public String render(Data data) {
        Data value;
        if (getBitRange().getEnd() > data.getBitSize()) {
            value = new Data();
        }
        else {
            value = data.copy(getBitOffset(), getBitSize());
        }
        
        String name = mapping.getName(value);
        if (name == null) {
            name = UNDEFINED;
        }
        return name;
    }
}