package dataWorkshop.gui.data.encoding;

import javax.swing.JComboBox;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.encoding.IEEE754;
import dataWorkshop.data.encoding.IEEE754Raw;

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
public class IEEE754RawConfigurationPane extends NumberConfigurationPane {
    
    JComboBox precisionBox;
    
    public IEEE754RawConfigurationPane() {
        super();
        precisionBox = new JComboBox(IEEE754.NAMES);
        precisionBox.setMaximumSize(precisionBox.getMinimumSize());
        precisionBox.addActionListener(this);
        add(precisionBox);
    }
    
    public DataEncoding getDataEncoding() {
        return DataEncodingFactory.getDataEncoding(new IEEE754Raw(getRadix(), (String) precisionBox.getSelectedItem(), isLittleEndian()));
    }
    
    public void setDataEncoding(DataEncoding converter) {
        super.setDataEncoding(converter);
        precisionBox.setSelectedItem(((IEEE754Raw) converter).getPrecision());
    }
}


