package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataTransformer;
import dataWorkshop.xml.XMLSerializeFactory;

/**
 *  if bits are positive rotate right, else rotate left
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
public class Rotate extends DataTransformer {
    
    int bits = 0;
    
    final static String BITS_TAG = "bits";
    final static String CLASS_NAME = "Rotate";
    
    /******************************************************************************
     *	Constructors
     */
    public Rotate() {
        this(1);
    }
    
    public Rotate(int bits) {
        super(bits);
        this.bits = bits;
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
     public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.setAttribute(context, BITS_TAG, getBits());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setBits(XMLSerializeFactory.getAttributeAsInt(context, BITS_TAG));
    }
    
     public String getClassName() {
        return CLASS_NAME;
    }
     
    /******************************************************************************
     *	Public Methods
     */
    public int getBits() {
        return bits;
    }
    
    public void setBits(int bits) {
        this.bits = bits;
    }
     
   public Data transform(Data data, long bitOffset, long bitSize) {
		//If the bits is more than the bitsize we only rotate by the modulo
        int shiftedBits = (int) (Math.abs(bits) % bitSize);
        //rotate right
        if (bits > 0) {
             Data result = data.copy(bitOffset + (bitSize - shiftedBits), shiftedBits);
             result.append(data.copy(bitOffset, bitSize - shiftedBits));
             return result;
        }
        //rotate left
        else {
             Data result = data.copy(bitOffset + shiftedBits, bitSize - shiftedBits);
             result.append(data.copy(bitOffset, shiftedBits));
             return result;
        }
    }
}