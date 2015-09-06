package dataWorkshop.xml;

import java.awt.Font;

import org.w3c.dom.Element;

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
public class MyFont implements XMLSerializeable {
    
    public final static String CLASS_NAME = "MyFont";
    
    final static String NAME_TAG = "name";
    final static String SIZE_TAG = "size";
    
    Font font;

    /******************************************************************************
     *	Constructors
     */
    public MyFont() {
        this(null);
    }
    
    public MyFont(Font font) {
        this.font = font;
    }
        
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, NAME_TAG, font.getName());
        XMLSerializeFactory.setAttribute(context, SIZE_TAG, font.getSize());
    }
    
    public void deserialize(Element context) {
        String name =  XMLSerializeFactory.getAttribute(context, NAME_TAG);
        int size =  XMLSerializeFactory.getAttributeAsInt(context, SIZE_TAG);
        font = new Font(name, Font.PLAIN, size);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public Font getFont() {
        return font;
    }
}
    
