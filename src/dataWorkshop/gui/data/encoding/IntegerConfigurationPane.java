package dataWorkshop.gui.data.encoding;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.encoding.IntegerEncoding;
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
public class IntegerConfigurationPane extends NumberConfigurationPane {
    
    NumberPane bitSizePane;
    JCheckBox signedBox;
    
    public IntegerConfigurationPane() {
        super();
        bitSizePane = new NumberPane(DataWorkshop.getInstance().getIntegerFormatFactory().getUnsignedOffset(6), "Bits");
        bitSizePane.setMinimum(IntegerEncoding.BIT_SIZE_MIN);
        bitSizePane.setMaximum(IntegerEncoding.BIT_SIZE_MAX);
        bitSizePane.setValue(8);
        bitSizePane.addActionListener(this);
        signedBox = new JCheckBox("Sign");
        signedBox.setHorizontalTextPosition(SwingConstants.LEADING);
        signedBox.addActionListener(this);
        add(signedBox);
        add(bitSizePane);
    }
    
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        if (e.getSource() == bitSizePane) {
            long bitSize = bitSizePane.getValue();
            if (bitSize > 8 && bitSize % 8 == 0) {
                littleEndianBox.setEnabled(true);
            }
            else {
                littleEndianBox.setEnabled(false);
            }
        }
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        bitSizePane.setEnabled(enabled);
        signedBox.setEnabled(enabled);
    }
    
    public DataEncoding getDataEncoding() {
        return DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), (int) bitSizePane.getValue(), isLittleEndian(), signedBox.isSelected()));
    }
    
    public void setDataEncoding(DataEncoding converter) {
        super.setDataEncoding(converter);
        signedBox.setSelected(((IntegerEncoding) converter).isSigned());
        bitSizePane.setValue(((IntegerEncoding) converter).getBitSize());
    }
}

