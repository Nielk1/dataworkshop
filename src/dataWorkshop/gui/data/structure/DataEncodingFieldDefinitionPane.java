package dataWorkshop.gui.data.structure;

import javax.swing.BorderFactory;

import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.data.structure.atomic.BitSizeLengthPane;
import dataWorkshop.gui.data.structure.atomic.ConverterFieldPane;
import dataWorkshop.data.structure.DataEncodingFieldDefinition;
import dataWorkshop.data.structure.ViewDefinitionElement;

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
public class DataEncodingFieldDefinitionPane extends ViewDefinitionElementPane implements LocaleStrings
{
	ConverterFieldPane fieldPane;
	BitSizeLengthPane lengthPane;

	/******************************************************************************
	 *	Constructors
	 */
	public DataEncodingFieldDefinitionPane(DataEncodingFieldDefinition field)
	{
		super(field);

		lengthPane = new BitSizeLengthPane(BIT_SIZE);
		lengthPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BIT_SIZE), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		fieldPane = new ConverterFieldPane();
		fieldPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Data"), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		//Put panes together
		getMainPane().add(lengthPane);
		getMainPane().add(fieldPane);

		fieldPane.setConverterFieldDefinition(field.getConverterFieldDefinition());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void getDefinitionNode(ViewDefinitionElement n) {
		super.getDefinitionNode(n);
		DataEncodingFieldDefinition node = (DataEncodingFieldDefinition) n;
		node.setLengthDefinition(lengthPane.getLength());
		node.setConverterFieldDefinition(fieldPane.getConverterFieldDefinition());
	}

	public void setDefinitionNode(ViewDefinitionElement n)
	{
		super.setDefinitionNode(n);
		DataEncodingFieldDefinition node = (DataEncodingFieldDefinition) n;
		lengthPane.setLength(node.getLengthDefinition(), node.getPreviousFields());
		fieldPane.setConverterFieldDefinition(node.getConverterFieldDefinition());
	}
}
