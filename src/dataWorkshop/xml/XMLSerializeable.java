/*
 * XMLSerializeable.java
 *
 * Created on 28. Dezember 2001, 23:25
 */

package dataWorkshop.xml;

import org.w3c.dom.Element;

/**
 * The main purpose of this interface is:
 * <br>
 * - tag interface for XML Serializable Classes
 * <br>
 * - allow <code>XMLSerializableFactory</code> to do some checks if the 
 * xml data is correct (e.g version checks)
 * <br>
 * 	Constraints for XMLSerializeable classes we cannot enforce at the moment
 * 	-only abstract classes may be subclassed, not concrete ones
 *		-needs public empty constructor (checked by XMLSerializefactory)
 *		-each getClassName must be unique (checked by XMLSerializefactory)
 *		-the XMLSerializable must be known to XMLSerializefactory (in order to circumvent obfuscation)
 *
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
public interface XMLSerializeable {

     /******************************************************************************
     *	XMLSerializable Interface
     */
     /**
      * This is used as xml tag and thus some characters are not allowed
       */
	public String getClassName();
	
    public void serialize(Element context);
    
    public void deserialize(Element context);
}
