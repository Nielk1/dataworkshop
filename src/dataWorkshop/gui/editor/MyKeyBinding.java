package dataWorkshop.gui.editor;

import javax.swing.Action;
import javax.swing.KeyStroke;

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
public class MyKeyBinding implements XMLSerializeable {
    
    public final static String ACTION_TAG = "action";
    public final static String KEYCODE_TAG = "keycode";
    public final static String MODIFIER_TAG = "modifier";
    
    final static String CLASS_NAME = "MyKeyBinding";
    
    String actionName;
    KeyStroke keyStroke;
    
    /******************************************************************************
     *	Constructors
     */
    public MyKeyBinding() {
    }
   
    public MyKeyBinding(Action a) {
        this((String) a.getValue(Action.NAME), (KeyStroke) a.getValue(Action.ACCELERATOR_KEY));
    }

    public MyKeyBinding(String actionName, KeyStroke keyStroke) {
        this.actionName = actionName;
        this.keyStroke = keyStroke;
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, ACTION_TAG, getActionName());
        XMLSerializeFactory.setAttribute(context, KEYCODE_TAG, getKeyStroke().getKeyCode());
        XMLSerializeFactory.setAttribute(context, MODIFIER_TAG, getKeyStroke().getModifiers());
    }
    
    public void deserialize(Element context) {
        actionName = XMLSerializeFactory.getAttribute(context, ACTION_TAG);
        int keycode = XMLSerializeFactory.getAttributeAsInt(context, KEYCODE_TAG);
        int modifier = XMLSerializeFactory.getAttributeAsInt(context, MODIFIER_TAG);
        keyStroke = KeyStroke.getKeyStroke(keycode, modifier);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public String getActionName() {
        return actionName;
    }
    
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}
