package dataWorkshop.data;

import dataWorkshop.number.UnsignedOffset;

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
public class BitRange {
	
    private long bitStart;
    private long bitSize;
    
    /******************************************************************************
     *	Constructors
     */
    public BitRange(long bitStart, long bitSize) {
        this.bitStart = bitStart;
        this.bitSize = bitSize;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public long getStart() {
        return bitStart;
    }
    
    public long getEnd() {
        return bitStart + bitSize;
    }
    
    public long getSize() {
        return bitSize;
    }
    
    public void addToSize(long size) {
        this.bitSize += size;
    }
    
    public void setStart(long start) {
        this.bitStart = start;
    }
    
    public void setSize(long size) {
        this.bitSize = size;
    }
    
    public boolean hasIntersection(BitRange bitRange) {
        if (getEnd() > bitRange.getStart() & getStart() < bitRange.getEnd()) {
            return true;
        }
        return false;
    }
    
    public BitRange intersection(BitRange bitRange) {
        long bitStart = Math.max(getStart(), bitRange.getStart());
        long bitEnd = Math.min(getEnd(), bitRange.getEnd());
        return new BitRange(bitStart, bitEnd - bitStart);
    }
    
    public boolean contains(BitRange bitRange) {
        return (getStart() <= bitRange.getStart() && bitRange.getEnd() <= getEnd());
    }
    
    public boolean equals(Object o) {
        if (o instanceof BitRange) {
            BitRange bitRange = (BitRange) o;
            if (bitRange.getStart() == getStart() && bitRange.getSize() == getSize()) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        String s = new String();
        s += new UnsignedOffset(getStart()) + " - " + new UnsignedOffset(getEnd()) + " ," + new UnsignedOffset(getSize()) + " bits";
        return s;
    }
}