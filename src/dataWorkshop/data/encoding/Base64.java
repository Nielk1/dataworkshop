package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;

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
public class Base64 extends DataEncoding {
    
    public final static String CLASS_NAME = "Base64Encoding";
    public final static String NAME = "Base64";
    
    final static char[] EMPTY = {'0'};
    
    static final char[][] encode = {
        {'A'},{'B'},{'C'},{'D'},{'E'},{'F'},{'G'},{'H'},
        {'I'},{'J'},{'K'},{'L'},{'M'},{'N'},{'O'},{'P'},
        {'Q'},{'R'},{'S'},{'T'},{'U'},{'V'},{'W'},{'X'},
        {'Y'},{'Z'},{'a'},{'b'},{'c'},{'d'},{'e'},{'f'},
        {'g'},{'h'},{'i'},{'j'},{'k'},{'l'},{'m'},{'n'},
        {'o'},{'p'},{'q'},{'r'},{'s'},{'t'},{'u'},{'v'},
        {'w'},{'x'},{'y'},{'z'},{'0'},{'1'},{'2'},{'3'},
        {'4'},{'5'},{'6'},{'7'},{'8'},{'9'},{'+'},{'/'}
    };
    
    public final static int[] decode = {
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,
        52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,
        -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,
        15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,
        -1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
        41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
    };
    
    /******************************************************************************
     *	Constructors
     */
    public Base64() {
        super(NAME, 6, 'A');
        setEmptyChars(EMPTY);
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
        chars[dot] = encode[data.intValue(offset, 6)][0];
    }
    
    public Data decode(char[] s) throws DataEncodingException {
        int value = decode[s[0]];
        if (value != -1) {
            return new Data(6, value);
        }
        else {
            char[] data = new char[getDotSize()];
                System.arraycopy(s, 0, data, 0, getDotSize());
            throw new DataEncodingException(this, data, s[0]);
        }
    }
    
    public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Base64) {
            return true;
        }
        return false;
    }
}