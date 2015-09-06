package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;

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
public class MSDOSTimeDate extends DataEncoding {
    
    public final static String CLASS_NAME = "MSDOSTimeDateEncoding";
    public final static String NAME = "MSDOS Time-Date";
    
    boolean[] isValidDot = {true,true,true,true,false,true,true,false,true,true,false,true,true,false,true,true,false,true,true};
    
    public final static char[] decLow = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };
    
    public final static char[] decHigh = {
        '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
        '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
        '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
        '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
        '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
        '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
        '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
        '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };
    
     public final static int[] decValue = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1 ,-1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
    };
    
    /******************************************************************************
     *	Constructors
     */
    public MSDOSTimeDate() {
        super(NAME, 32, '0');
        setEmptyChars(new char[] {'-','-','-','-','/','-','-','/','-','-',' ','-','-',':','-','-',':','-','-'});
    }
    
     /******************************************************************************
     *	XMLSerializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
    }
    
    public void deserialize(Element context) {
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void encode(Data data, long offset, char[] chars, int dot) {
        System.arraycopy(encode(), 0, chars, dot, getDotSize());
        Data d = data.copy(offset, getBitSize());
        d.swapByteOrder();

        //Year
        int value = d.intValue(0, 7);
        value += 1980;
        chars[dot + 0] = (char) decHigh[value / 100];
        chars[dot + 1] = (char)decLow[value / 100];
        chars[dot + 2] = (char)decHigh[value % 100];
        chars[dot + 3] = (char)decLow[value % 100];
        
        //Month
        value = d.intValue(7, 4);
        chars[dot + 5] = (char)decHigh[value];
        chars[dot + 6] = (char)decLow[value];
        
        //Day
        value = d.intValue(11, 5);
        chars[dot + 8] = (char)decHigh[value];
        chars[dot + 9] = (char)decLow[value];
        
        //Hour
        value = d.intValue(16, 5);
        chars[dot + 11] = (char)decHigh[value];
        chars[dot + 12] = (char)decLow[value];
        
        //Minute
        value = d.intValue(21, 6);
        chars[dot + 14] = (char)decHigh[value];
        chars[dot + 15] = (char)decLow[value];
        
        //Second
        value = d.intValue(27, 5);
        value = value << 1;
        chars[dot + 17] = (char)decHigh[value];
        chars[dot + 18] = (char)decLow[value];
    }
    
    public Data decode(char[] s) {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
        
        int value;
        int i = decValue[s[0]];
        if (i == -1) return null;
        year = i;
        i = decValue[s[1]];
        if (i == -1) return null;
        year = (year * 10) + i;
        i = decValue[s[2]];
        if (i == -1) return null;
        year = (year * 10) + i;
        i = decValue[s[3]];
        if (i == -1) return null;
        year = (year * 10) + i;
        year -= 1980;
        if (year < 0 | year > 127) return null;
        
        i = decValue[s[5]];
        if (i == -1) return null;
        month = i;
        i = decValue[s[6]];
        if (i == -1) return null;
        month = (month * 10) + i;
        if (month > 15) return null;
        
        i = decValue[s[8]];
        if (i == -1) return null;
        day = i;
        i = decValue[s[9]];
        if (i == -1) return null;
        day = (day * 10) + i;
        if (day > 31) return null;
        
        i = decValue[s[11]];
        if (i == -1) return null;
        hour = i;
        i = decValue[s[12]];
        if (i == -1) return null;
        hour = (hour * 10) + i;
        if (hour > 31) return null;
        
        i = decValue[s[14]];
        if (i == -1) return null;
        minute = i;
        i = decValue[s[15]];
        if (i == -1) return null;
        minute = (minute * 10) + i;
        if (minute > 63) return null;
        
        i = decValue[s[17]];
        if (i == -1) return null;
        second = i;
        i = decValue[s[18]];
        if (i == -1) return null;
        second = (second * 10) + i;
        second = second >>> 1;
        if (second > 31) return null;
        
        value = (year << 25) + (month << 21) + (day << 16) + (hour << 11) + (minute << 5) + second;
        
        Data d = new Data(32, value);
        d.swapByteOrder();
        return d;
    }
    
    public boolean isInputPoint(int dot) {
         if (encode()[dot] == '-') {
            return true;
        }
        return false;
    }
    
      public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof MSDOSTimeDate) {
            return super.equals(obj);
        }
        return false;
    }
}