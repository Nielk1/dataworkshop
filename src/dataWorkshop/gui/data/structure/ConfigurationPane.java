package dataWorkshop.gui.data.structure;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.MapFieldDefinition;
import dataWorkshop.data.structure.RepeatStatement;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.DataEncodingFieldDefinition;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.UntilDelimiterFieldDefinition;
import dataWorkshop.data.structure.UntilDelimiterRepeatStatement;
import dataWorkshop.data.structure.UntilOffsetFieldDefinition;
import dataWorkshop.data.structure.UntilOffsetRepeatStatement;

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
public class ConfigurationPane extends JPanel
{
	protected JPanel nodePane;

	protected RootStatementStructurePane rootStatementStructurePane = new RootStatementStructurePane(new RootStatement());

	protected CaseStatementStructurePane caseStatementDefinitionPane = new CaseStatementStructurePane(new CaseStatement());
	protected RepeatStatementPane repeatStatementDefinitionPane = new RepeatStatementPane(new RepeatStatement());
	protected UntilDelimiterRepeatStatementPane untilDelimiterRepeatStatementDefinitionPane = new UntilDelimiterRepeatStatementPane(new UntilDelimiterRepeatStatement());
	protected UntilOffsetRepeatStatementPane untilOffsetRepeatStatementDefinitionPane = new UntilOffsetRepeatStatementPane(new UntilOffsetRepeatStatement());

	protected MapFieldDefinitionPane mapFieldDefinitionPane = new MapFieldDefinitionPane(new MapFieldDefinition());
	protected DataEncodingFieldDefinitionPane staticFieldDefinitionPane = new DataEncodingFieldDefinitionPane(new DataEncodingFieldDefinition());
	protected UntilDelimiterFieldDefinitionPane untilDelimiterFieldDefinitionPane = new UntilDelimiterFieldDefinitionPane(new UntilDelimiterFieldDefinition());
	protected UntilOffsetFieldDefinitionPane untilOffsetFieldDefinitionPane = new UntilOffsetFieldDefinitionPane(new UntilOffsetFieldDefinition());

	/******************************************************************************
	*	Constructors
	*/
	public ConfigurationPane()
	{
		setLayout(new BorderLayout());
		nodePane = new JPanel();
		add(nodePane, BorderLayout.CENTER);
	}

	/******************************************************************************
	*	Public Methods
	*/
	public void getDefinitionNode(ViewDefinitionElement node)
	{
		if (nodePane instanceof ViewDefinitionElementPane)
		{
			((ViewDefinitionElementPane) nodePane).getDefinitionNode(node);
		}
	}

	public void setDefinitionNode(ViewDefinitionElement node)
	{
		remove(nodePane);
		if (node == null)
		{
			nodePane = new JPanel();
		}
		else if (node instanceof CaseStatement)
		{
			caseStatementDefinitionPane.setDefinitionNode(node);
			nodePane = caseStatementDefinitionPane;
		}
		else if (node instanceof RepeatStatement)
		{
			repeatStatementDefinitionPane.setDefinitionNode(node);
			nodePane = repeatStatementDefinitionPane;
		}
		else if (node instanceof UntilDelimiterRepeatStatement)
		{
			untilDelimiterRepeatStatementDefinitionPane.setDefinitionNode(node);
			nodePane = untilDelimiterRepeatStatementDefinitionPane;
		}
		else if (node instanceof UntilOffsetRepeatStatement)
		{
			untilOffsetRepeatStatementDefinitionPane.setDefinitionNode(node);
			nodePane = untilOffsetRepeatStatementDefinitionPane;
		}
		else if (node instanceof MapFieldDefinition)
		{
			mapFieldDefinitionPane.setDefinitionNode(node);
			nodePane = mapFieldDefinitionPane;
		}
		else if (node instanceof DataEncodingFieldDefinition)
		{
			staticFieldDefinitionPane.setDefinitionNode(node);
			nodePane = staticFieldDefinitionPane;
		}
		else if (node instanceof UntilDelimiterFieldDefinition)
		{
			untilDelimiterFieldDefinitionPane.setDefinitionNode(node);
			nodePane = untilDelimiterFieldDefinitionPane;
		}
		else if (node instanceof UntilOffsetFieldDefinition)
		{
			untilOffsetFieldDefinitionPane.setDefinitionNode(node);
			nodePane = untilOffsetFieldDefinitionPane;
		}
		else if (node instanceof RootStatement) {
			rootStatementStructurePane.setDefinitionNode(node);
			nodePane = rootStatementStructurePane;
		}
		else
		{
			throw new RuntimeException("Could not configure " + node.getClassName());
		}
		add(nodePane, BorderLayout.CENTER);
		/**
		 * :KLUDGE:Martin Pape:Jun 6, 2003
		 * Why do we have to explicitly force a repaint? Swing should do this for us
		 */
		repaint();
		//revalidate();
	}
}
