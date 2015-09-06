package dataWorkshop.data;

import org.w3c.dom.Element;

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
public abstract class DataTransformer
implements XMLSerializeable {
    
    transient private long bitSize;
    
    /******************************************************************************
     *	Constructors
     */
    //dummy Constructor for xmlserializing
    public DataTransformer() {
        this(1);
    }
    
    public DataTransformer(long bitSize) {
        this.bitSize = bitSize;
    }
    
    /******************************************************************************
     *	XMLSerializeable Interface
     */
    public void serialize(Element context) {
    }
    
    public void deserialize(Element context) {
    }
    
    /******************************************************************************
     *	Public Methods
     */
    /**
     * :TODO:Martin Pape:May 6, 2003
     * At the moment we just throw a Exception but later, when the exeception requirements for
     * the data transformation are clearer, the design should be tightened (e.g. a DataTransformation Exception, or
     * a TransformationResult object with error information.
     */
    public abstract Data transform(Data data, long bitOffset, long bitSize) throws Exception;
    
    public String toString() {
        return getClassName() + " (" + getBitSize() + "-Bit)";
    }
    
    protected void setBitSize(long bitSize) {
        this.bitSize = bitSize;
    }
    
    public long getBitSize() {
        return bitSize;
    }
}