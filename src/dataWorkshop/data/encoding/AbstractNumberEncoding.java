package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.logging.Logger;
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
public abstract class AbstractNumberEncoding extends DataEncoding {
    
    final static String LITTLE_ENDIAN_TAG = "littleEndian";
    
    boolean littleEndian;
    
    /******************************************************************************
     *	Constructors
     */
    public AbstractNumberEncoding(String name, int bitSize, boolean littleEndian) {
        super(name, bitSize, '0');
        setLittleEndian(littleEndian);
		/**
		 * :TRICKY:Martin Pape:May 4, 2003
		 * littleEndian is only valid if bitSize > 8 and a mulitple of 8, if this is not the case
		 * we use littleEndian=false as default value;
		 */
        if (bitSize == 8 || bitSize % 8 != 0) {
            if (littleEndian) {
            	Logger logger =Logger.getLogger(this.getClass());
               	logger.warning("LittleEndian was set to true, although bitsize = " + bitSize);
            	littleEndian = false;
            }
        }
		setLittleEndian(littleEndian);
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, LITTLE_ENDIAN_TAG, isLittleEndian());
    }
    
    public void deserialize(Element context) {
        setLittleEndian(XMLSerializeFactory.getAttributeAsBoolean(context, LITTLE_ENDIAN_TAG));
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public boolean isLittleEndian() {
        return littleEndian;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof AbstractNumberEncoding) {
            AbstractNumberEncoding converter = (AbstractNumberEncoding) obj;
            if (this.littleEndian != converter.littleEndian) {
                return false;
            }
            return true;
        }
        return false;
    }
    
     /******************************************************************************
     *	Protected Methods
     */
    protected void setBitSize(int bitSize) {
        super.setBitSize(bitSize);
        //recalc littleEndian property
        setLittleEndian(isLittleEndian());
    }
    
    protected void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
        removeProperty(LITTLE_ENDIAN_TAG);
        if (getBitSize() % 8 == 0 && getBitSize() > 8) {
            if (littleEndian) {
                addProperty(LITTLE_ENDIAN_TAG, "LittleEndian");
            }
            else {
                addProperty(LITTLE_ENDIAN_TAG, "BigEndian");
            }
        }
    }
}