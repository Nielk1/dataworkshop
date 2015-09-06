package dataWorkshop.data.transformer;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataTransformer;

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
public class CRC32 extends DataTransformer {
    
    final static String CLASS_NAME = "CRC32";
    
    static final int[] CRC_TABLE = new int[256];
    static {
        int value;
        for (int i = 0; i < 256; i++) {
            value = i;
            for (int ii = 0; ii < 8; ii++) {
                if ((value & 1) == 1) {
                    value = 0xedb88320 ^ (value >>> 1);
                }
                else {
                    value = value >>> 1;
                }
            }
            CRC_TABLE[i] = value;
        }
    }
    
    /******************************************************************************
     *	Constructors
     */
    public CRC32() {
        super(8);
    }
    
      /******************************************************************************
     *	XMLSerializeable Interface
     */
     public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
  public Data transform(Data data, long bitOffset, long bitSize) {
        int result = -1;
        for (long i = 0; i < bitSize; i += getBitSize()) {
            result = CRC_TABLE[ (result ^ data.intValue(bitOffset + i, 8)) & 0xFF] ^ (result >>> 8);
        }
        
        result = result ^ 0xFFFFFFFF;
        return new Data(32, result);
    }
}