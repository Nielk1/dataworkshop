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
abstract public class ViewTemplate implements XMLSerializeable {

	final static String NAME_TAG ="name";    
    final static String STRUCTURES_TAG = "Structures";
    final static String STRUCTURE_TAG = "Structure";
    
	String name;
    String[] structures;
    
    /******************************************************************************
     *	Constructors
     */
    public ViewTemplate() {
        this(new String(), new String[0]);
    }
    
    public ViewTemplate(String description, String[] structures) {
        this.name = description;
        this.structures = structures;
    }
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, NAME_TAG, getName());
        XMLSerializeFactory.serialize(context, STRUCTURES_TAG, STRUCTURE_TAG, getStructures());
    }
    
    public void deserialize(Element context) {
        setName(XMLSerializeFactory.getAttribute(context, NAME_TAG));
        setStructures(XMLSerializeFactory.deserializeAsStringArray(context, STRUCTURES_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public String toString() {
    	return getName();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String description) {
        this.name = description;
    }
    
    public String[] getStructures() {
        return structures;
    }
    
    public void setStructures(String[] structures) {
        this.structures = structures;
    }
}