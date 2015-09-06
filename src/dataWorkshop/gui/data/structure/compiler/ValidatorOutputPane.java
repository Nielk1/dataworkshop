package dataWorkshop.gui.data.structure.compiler;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dataWorkshop.gui.MyJTable;
import dataWorkshop.number.StructureWrapper;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.compiler.ValidatorOutput;

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
public class ValidatorOutputPane extends MyJTable implements ValidatorOutput
{
	private final static String[] COLUMN_NAMES = { "Structure", "Error" };

	/******************************************************************************
	*	Constructors
	*/
	public ValidatorOutputPane()
	{
		super(new DefaultTableModel(new Object[0][0], COLUMN_NAMES)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setToolTipText(table.getValueAt(row, 1).toString());
				return c;
			}
		});
	}

	/******************************************************************************
	*	CompilerOutput Interface
	*/
	public void error(ViewDefinitionElement node, String message)
	{
		add(new Comparable[] { new StructureWrapper(node), message });
	}

	public void warning(ViewDefinitionElement node, String message)
	{
		add(new Comparable[] { new StructureWrapper(node), message });
	}

	public void info(ViewDefinitionElement node, String message)
	{
		add(new Comparable[] { new StructureWrapper(node), message });
	}

	/******************************************************************************
	*	Public Methods
	*/
	public ViewDefinitionElement getSelectedStructure()
	{
		int row = getSelectedRow();
		if (row != -1)
		{
			return ((StructureWrapper) getValueAt(row, 0)).getStructure();
		}
		return null;
	}
}
