package dataWorkshop.gui.data.structure;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.UntilDelimiterRepeatStatement;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.gui.data.structure.atomic.DelimiterPane;
import dataWorkshop.gui.data.structure.atomic.SimpleModificationPane;

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
public class UntilDelimiterRepeatStatementPane extends ViewDefinitionElementPane implements LocaleStrings
{
	DelimiterPane searchPane;
	SimpleModificationPane modPane;

	/******************************************************************************
	 *	Constructors
	 */
	public UntilDelimiterRepeatStatementPane(UntilDelimiterRepeatStatement structure)
	{
		super(structure);

		JPanel lengthPane = new JPanel();
		lengthPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(REPEAT), BorderFactory.createEmptyBorder(6, 6, 6, 6)));
		lengthPane.setLayout(new BorderLayout());

		searchPane = new DelimiterPane(DELIMITER_ENCODING);
		modPane = new SimpleModificationPane(BIT_SIZE, BIT_SIZE, DataWorkshop.getInstance().getSignedOffsetNumber());
		modPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0), modPane.getBorder()));
		lengthPane.add(searchPane, BorderLayout.CENTER);
		lengthPane.add(modPane, BorderLayout.SOUTH);

		searchPane.setDelimiterDefinition(structure.getDelimiterDefinition());
		modPane.setSimpleModificationDefinition(structure.getSimpleModificationDefinition());

		getMainPane().add(lengthPane);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void getDefinitionNode(ViewDefinitionElement n) {
		super.getDefinitionNode(n);
		UntilDelimiterRepeatStatement node = (UntilDelimiterRepeatStatement) n;
		node.setDelimiterDefinition(searchPane.getDelimiterDefinition());
		node.setSimpleModificationDefinition(modPane.getSimpleModificationDefinition());
	}

	public void setDefinitionNode(ViewDefinitionElement n)
	{
		super.setDefinitionNode(n);
		UntilDelimiterRepeatStatement node = (UntilDelimiterRepeatStatement) n;
		searchPane.setDelimiterDefinition(node.getDelimiterDefinition());
		modPane.setSimpleModificationDefinition(node.getSimpleModificationDefinition());
	}
}
