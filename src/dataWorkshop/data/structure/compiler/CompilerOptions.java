package dataWorkshop.data.structure.compiler;

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
public class CompilerOptions implements XMLSerializeable {
    
    public final static String CLASS_NAME = "DefinitionNodeCompilerOptions";
    
    final static String POINTER_THRESHOLD_TAG = "pointerStructureThreshold";
    final static String POINTER_STRUCTURE_WARNING_TAG = "pointerStructureWarning";
    
    long pointerThreshold = 0;
    boolean pounterThresholdEnabled = false;
    
    /******************************************************************************
     *	Constructors
     */
    public CompilerOptions() {
    }
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
      public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, POINTER_STRUCTURE_WARNING_TAG, isPointerStructureThresholdEnabled());
        XMLSerializeFactory.setAttribute(context, POINTER_THRESHOLD_TAG, getPointerStructureThreshold());
    }
    
    public void deserialize(Element context) {
        setPointerStructureThresholdEnabled(XMLSerializeFactory.getAttributeAsBoolean(context, POINTER_STRUCTURE_WARNING_TAG));
        setPointerStructureThreshold(XMLSerializeFactory.getAttributeAsLong(context, POINTER_THRESHOLD_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public long getPointerStructureThreshold() {
        return pointerThreshold;
    }
    
    public void setPointerStructureThreshold(long threshold) {
        this.pointerThreshold = threshold;
    }
    
    public boolean isPointerStructureThresholdEnabled() {
        return pounterThresholdEnabled;
    }
    
    public void setPointerStructureThresholdEnabled(boolean enabled) {
        this.pounterThresholdEnabled = enabled;
    }
}