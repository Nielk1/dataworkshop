package dataWorkshop.gui.data.structure.atomic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.gui.NumberPane;
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
public class StaticLengthPane extends JPanel {
    
    NumberPane numberPane;
    
    /******************************************************************************
     *	Constructors
     */
    public StaticLengthPane(IntegerFormat integerFormat, String label) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		numberPane = new NumberPane(integerFormat, label);
		
		add(numberPane);
		add(Box.createHorizontalGlue());
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public StaticLengthDefinition getStaticLength() {
        return new StaticLengthDefinition(numberPane.getValue());
    }
    
    public void setStaticLength(StaticLengthDefinition length) {
		numberPane.setValue(length.getLength());
    }
}
