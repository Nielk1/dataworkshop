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
public class USAscii extends DataEncoding {
    
    public final static String CLASS_NAME = "USAsciiEncoding";
    public final static String NAME = "Ascii";
    final static char[] EMPTY = {'-'}; 
    
    /******************************************************************************
     *	Constructors
     */
    public USAscii() {
        super(NAME, 8, ' ');
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
        chars[dot] = US_ASCII[data.intValue(offset, 8)];
    }
    
    public Data decode(char[] s) throws DataEncodingException {
        if (s[0] >= 0 && s[0] <= 255) {
            return new Data(8, s[0]);
        }
        else {
			char[] data = new char[0];
			data[0] = s[0];
			throw new DataEncodingException(this, data, s[0]);
        }
    }
    
      public int hashCode() {
        return NAME.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof USAscii) {
            return true;
        }
        return false;
    }
    
    public final static char[] US_ASCII = {
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        ' ','!','"','#','$','%','&','\'','(',')','*','+',',','-','.','/',
        '0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?',
        '@','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
        'P','Q','R','S','T','U','V','W','X','Y','Z','[','\\',']','^','_',
        '`','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
        'p','q','r','s','t','u','v','w','x','y','z','{','|','}','~','.',
        '.','?','.','.','.','.','.','.','.','.','.','.','.','?','?','?',
        '?','.','.','.','.','.','.','.','.','.','.','.','.','?','?','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
        '.','.','.','.','.','.','.','.','.','.','.','.','.','.','.','.',
    };
}