package dataWorkshop.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.DataNumber;

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
public class DataNumberPane extends ActionPane
implements LocaleStrings {
    
    NumberPane bitSizePane;
    JCheckBox signedBox;
    JCheckBox littleEndianBox;
    
    /******************************************************************************
     *	Constructors
     */
    public DataNumberPane(DataNumber number) {
        this(number, null);
    }
    
    public DataNumberPane(DataNumber number, String label) {
        super();
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        DataWorkshop options = DataWorkshop.getInstance();
        bitSizePane = new NumberPane(options.getUnsignedOffsetNumber(), BIT_SIZE);
        signedBox = new JCheckBox("Signed");
        signedBox.setHorizontalTextPosition(SwingConstants.LEADING);
        littleEndianBox = new JCheckBox("LittleEndian");
        littleEndianBox.setHorizontalTextPosition(SwingConstants.LEADING);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (label != null) {
            add(new JLabel(label));
            add(Box.createRigidArea(new Dimension(12, 0)));
        }
        add(bitSizePane);
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(signedBox);
        add(Box.createRigidArea(new Dimension(12, 0)));
        add(littleEndianBox);
        setDataNumber(number);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public DataNumber getDataNumber() {
        return DataNumber.getDataNumber((int) bitSizePane.getValue(), littleEndianBox.isSelected(), signedBox.isSelected());
    }
    
    public void setDataNumber(DataNumber number) {
        signedBox.setSelected(number.isSigned());
        littleEndianBox.setSelected(number.isLittleEndian());
        bitSizePane.setValue(number.getBitSize());
    }
}