package dataWorkshop.gui.data.structure.atomic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.atomic.SimpleModificationDefinition;
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
public class SimpleModificationPane extends JPanel 
implements LocaleStrings {
    
	NumberPane additionField;
    
    /******************************************************************************
     *	Constructors
     */
    public SimpleModificationPane(String afterModification, String beforeModification, IntegerFormat additionNumber) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(new JLabel(afterModification + " = " + beforeModification));
		add(new JLabel(" + "));
		additionField = new NumberPane(additionNumber);
		add(additionField);
		add(Box.createHorizontalGlue());
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public SimpleModificationDefinition getSimpleModificationDefinition() {
		SimpleModificationDefinition length = new SimpleModificationDefinition();
		length.setAddition(additionField.getValue());
		return length;
    }
    
    public void setSimpleModificationDefinition(SimpleModificationDefinition length) {
		additionField.setValue(length.getAddition());
    }
}
