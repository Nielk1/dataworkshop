package dataWorkshop.gui.data.structure.atomic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.atomic.ModificationDefinition;
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
public class ModificationPane extends JPanel implements LocaleStrings {
  
    NumberPane multiplicationField;
    NumberPane additionField;
    
    /******************************************************************************
     *	Constructors
     */
    public ModificationPane(String modifiedValue, String value, IntegerFormat additionNumber) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        //setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        DataWorkshop options = DataWorkshop.getInstance();
        
        add(new JLabel(modifiedValue + " = ( " + value));
        add(new JLabel(" * "));
        multiplicationField = new NumberPane(options.getUnsignedCount());
        multiplicationField.setMinimum(1);
        add(multiplicationField);
        add(new JLabel(" ) + "));
        additionField = new NumberPane(additionNumber);
        add(additionField);
        add(Box.createHorizontalGlue());
        //setAlignmentX(Component.RIGHT_ALIGNMENT);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setModificationDefinition(ModificationDefinition modificationDefinition) {
        additionField.setValue(modificationDefinition.getAddition());
        multiplicationField.setValue(modificationDefinition.getMultiplication());
    }
    
    public ModificationDefinition getModificationDefinition() {
		ModificationDefinition mod = new ModificationDefinition();
		mod.setAddition(additionField.getValue());
		mod.setMultiplication(multiplicationField.getValue());
		return mod;
    }
}