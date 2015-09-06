package dataWorkshop.gui.data.encoding;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JComboBox;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.encoding.IEEE754;
import dataWorkshop.data.encoding.IEEE754Decoded;
import dataWorkshop.gui.NumberPane;

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
public class IEEE754DecodedConfigurationPane extends NumberConfigurationPane {
    
    JComboBox precisionBox;
    NumberPane mantisseDigits;
    
    public IEEE754DecodedConfigurationPane() {
        super();
        precisionBox = new JComboBox(IEEE754.NAMES);
        precisionBox.setMaximumSize(precisionBox.getMinimumSize());
        precisionBox.addActionListener(this);
        mantisseDigits = new NumberPane(DataWorkshop.getInstance().getIntegerFormatFactory().getUnsignedCount(8), "Mantisse Digits");
        mantisseDigits.setMinimum(IEEE754Decoded.MANTISSE_DIGITS_MIN);
        mantisseDigits.setMaximum(IEEE754Decoded.MANTISSE_DIGITS_MAX);
        mantisseDigits.addActionListener(this);
        add(precisionBox);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(mantisseDigits);
    }
    
    public DataEncoding getDataEncoding() {
        return DataEncodingFactory.getDataEncoding(new IEEE754Decoded(getRadix(), (String) precisionBox.getSelectedItem(), isLittleEndian(), (int) mantisseDigits.getValue()));
    }
    
    public void setDataEncoding(DataEncoding converter) {
        super.setDataEncoding(converter);
        precisionBox.setSelectedItem(((IEEE754Decoded) converter).getPrecision());
        mantisseDigits.setValue(((IEEE754Decoded) converter).getMantisseDigits());
    }
}


