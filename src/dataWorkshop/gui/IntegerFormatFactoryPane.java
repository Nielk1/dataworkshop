package dataWorkshop.gui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.number.IntegerFormatFactory;

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
public class IntegerFormatFactoryPane extends JPanel {
    
    ComboPane offsetBox;
    ComboPane baseBox;
    
    /******************************************************************************
     *	Constructors
     */
    public IntegerFormatFactoryPane() {
        super();

        offsetBox = new ComboPane(IntegerFormatFactory.validOffsetRules.toArray(), "Display Offset");
        baseBox = new ComboPane(IntegerFormatFactory.validBases.toArray(), "Number Base");
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(offsetBox);
        add(baseBox);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setIntegerFormatFactory(IntegerFormatFactory factory) {
        offsetBox.setSelectedItem(factory.getOffsetRule());
        baseBox.setSelectedItem(factory.getBase());
    }
    
    public IntegerFormatFactory getIntegerFormatFactory() {
        return new IntegerFormatFactory((String) baseBox.getSelectedItem(), (String) offsetBox.getSelectedItem());
    }
}