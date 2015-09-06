package dataWorkshop.gui.data.structure;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.data.structure.atomic.BitSizeLengthPane;
import dataWorkshop.gui.data.structure.atomic.ConverterFieldPane;
import dataWorkshop.gui.data.structure.atomic.LocationPane;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.UntilOffsetFieldDefinition;

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
public class UntilOffsetFieldDefinitionPane extends ViewDefinitionElementPane implements LocaleStrings
{
	LocationPane locationPane;
	ConverterFieldPane fieldPane;
	BitSizeLengthPane lengthPane;

	/******************************************************************************
	 *	Constructors
	 */
	public UntilOffsetFieldDefinitionPane(UntilOffsetFieldDefinition field)
	{
		super(field);
		locationPane = new LocationPane(ALIGN_RELATIVE_TO);
		
		lengthPane = new BitSizeLengthPane(ALIGNMENT_BIT_SIZE);

		fieldPane = new ConverterFieldPane();
		fieldPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Data"), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		JPanel repeatPane = new JPanel();
		repeatPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BIT_SIZE), BorderFactory.createEmptyBorder(6, 6, 6, 6)));
		repeatPane.setLayout(new BoxLayout(repeatPane, BoxLayout.Y_AXIS));
		repeatPane.add(lengthPane);
		repeatPane.add(Box.createRigidArea(new Dimension(0, 12)));
		repeatPane.add(locationPane);

		//Put panes together
		getMainPane().add(repeatPane);
		getMainPane().add(fieldPane);

		fieldPane.setConverterFieldDefinition(field.getConverterFieldDefinition());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void getDefinitionNode(ViewDefinitionElement n) {
		super.getDefinitionNode(n);
		UntilOffsetFieldDefinition node = (UntilOffsetFieldDefinition) n;
		node.setConverterFieldDefinition(fieldPane.getConverterFieldDefinition());
		node.setLengthDefinition(lengthPane.getLength());
		node.setLocationDefinition(locationPane.getLocationDefinition());
	}

	public void setDefinitionNode(ViewDefinitionElement n)
	{
		super.setDefinitionNode(n);
		UntilOffsetFieldDefinition node = (UntilOffsetFieldDefinition) n;
		fieldPane.setConverterFieldDefinition(node.getConverterFieldDefinition());
		lengthPane.setLength(node.getLengthDefinition(), node.getPreviousFields());
		locationPane.setLocationDefinition(node.getLocationDefinition(), node.getPreviousDefinitionNodes());
	}
}
