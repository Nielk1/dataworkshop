package dataWorkshop.gui.editor;

import java.util.HashSet;

import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
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
public class StructureClipboard {
    
    private static StructureClipboard instance;
    private HashSet stateChangeListener = new HashSet();
    
	ViewDefinitionElement definitionNode;
    
    /******************************************************************************
     *	Constructors
     */
    private StructureClipboard() {
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static StructureClipboard getInstance() {
        if (instance == null) {
            instance = new StructureClipboard();
        }
        return instance;
    }
    
    public void addStateChangeListener(StateChangeListener l) {
        stateChangeListener.add(l);
    }
    
    public void removeStateChangeListener(StateChangeListener l) {
        stateChangeListener.remove(l);
    }
    
    public boolean hasCopy() {
        return definitionNode != null;
    }
    
    /*
     *  node is cloned
     */
    public void setDefinitionNode(ViewDefinitionElement node) {
        boolean stateChanged = definitionNode == null;
        definitionNode = (ViewDefinitionElement) XMLSerializeFactory.getInstance().deserialize(node.serialize(), false);
        if (stateChanged) {
            fireStateChanged();
        }
    }
    
    public ViewDefinitionElement getDefinitionNode() {
        //do a clone
        return (ViewDefinitionElement) XMLSerializeFactory.getInstance().deserialize(definitionNode.serialize(), false);
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    protected void fireStateChanged() {
        Object[] listeners = stateChangeListener.toArray();
        StateChangeEvent changeEvent = new StateChangeEvent(this);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((StateChangeListener) listeners[i]).stateChanged(changeEvent);
        }
    }
}
