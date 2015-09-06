package dataWorkshop.gui.data.structure.atomic;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.DataFieldDefinition;
import dataWorkshop.data.structure.atomic.PointerLengthDefinition;
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
public class PointerLengthPane extends JPanel 
implements LocaleStrings {
    
	PointerPane pointerPane;
	ModificationPane modPane;
    
    /******************************************************************************
     *	Constructors
     */
    public PointerLengthPane(IntegerFormat integerFormat, String label) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		pointerPane = new PointerPane(EVALUTE_FIELD);
		modPane = new ModificationPane(label, FIELD_VALUE, integerFormat);
		
		add(pointerPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(modPane);
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public PointerLengthDefinition getPointerLength() {
		PointerLengthDefinition pointerLength = new PointerLengthDefinition();
		pointerLength.setModificationDefinition(modPane.getModificationDefinition());
		pointerLength.setPointerDefinition(pointerPane.getPointerDefinition());
		return pointerLength;
    }
    
    public void setPointerLength(PointerLengthDefinition pointerLength, DataFieldDefinition[] possiblePointers) {
		modPane.setModificationDefinition(pointerLength.getModificationDefinition());
		pointerPane.setPointerDefinition(pointerLength.getPointerDefinition(), possiblePointers);
    }
}
