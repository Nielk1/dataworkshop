package dataWorkshop.gui.data.encoding;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JComboBox;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.encoding.NumberEncoding;
import dataWorkshop.number.IntegerFormat;

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
public abstract class NumberConfigurationPane extends AbstractNumberConfigurationPane {
    
    //radix - 2
    final static String nameForRadix[] = {
        "Binary", "Ternary", "Quaternary", "Quinary", "Senary", "Septenary", "Octal", "Nonary",
        "Decimal", "Undenary", "Duodecimal", "Base 13", "Base 14", "Base 15", "Hexadecimal", "Base 17", "Base 18", "Base 19"
    };
    
    JComboBox radixBox;
    IntegerFormat radixFormat;
    
    public NumberConfigurationPane() {
        radixBox = new JComboBox(nameForRadix);
        radixBox.setMaximumSize(radixBox.getMinimumSize());
        radixBox.setSelectedIndex(14);
        radixBox.addActionListener(this);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(radixBox);
    }
    
     public void setEnabled(boolean enabled) {
     	super.setEnabled(enabled);
        radixBox.setEnabled(enabled);
    }
    
    public int getRadix() {
        return radixBox.getSelectedIndex() + 2;
    }
    
    public void setDataEncoding(DataEncoding converter) {
		super.setDataEncoding(converter);
        radixBox.setSelectedIndex(((NumberEncoding) converter).getRadix() - 2);
    }
}
