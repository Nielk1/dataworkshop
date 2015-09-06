package dataWorkshop.gui.data.structure;

import javax.swing.BorderFactory;

import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.gui.data.structure.atomic.*;

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
public class CaseStatementStructurePane extends ViewDefinitionElementPane
{

	CasePane conditionalPane;
	LocationPane locationPane;

	/******************************************************************************
	 *	Constructors
	 */
	public CaseStatementStructurePane(CaseStatement frame)
	{
		super(frame);

		locationPane = new LocationPane("Data Offset");
		locationPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Data Offset"), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		conditionalPane = new CasePane();
		getMainPane().add(locationPane);
		getMainPane().add(conditionalPane);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void getDefinitionNode(ViewDefinitionElement n) {
		super.getDefinitionNode(n);
		CaseStatement node = (CaseStatement) n;
		node.setLocationDefintion(locationPane.getLocationDefinition());
		node.setCaseDefinition(conditionalPane.getCaseDefinition());
	}
	
	public void setDefinitionNode(ViewDefinitionElement n)
	{
		super.setDefinitionNode(n);
		CaseStatement node = (CaseStatement) n;
		locationPane.setLocationDefinition(node.getLocationDefintion(), node.getPreviousDefinitionNodes());
		conditionalPane.setCaseDefinition(node.getCaseDefinition(), node.getPossibleValueNames());
	}
}
