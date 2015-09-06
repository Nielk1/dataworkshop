package dataWorkshop.data;

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
public class DefaultTemplate extends ViewTemplate {
    
    final static String CLASS_NAME = "DefaultTemplate";
    
    /******************************************************************************
     *	Constructors
     */
    public DefaultTemplate() {
        this(new String(), new String[0]);
    }
    
    public DefaultTemplate(String description, String[] structures) {
    	super(description, structures);
    }
    
    
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
      public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
}