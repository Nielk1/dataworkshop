package dataWorkshop.gui.data.structure.compiler;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dataWorkshop.gui.MyJTable;
import dataWorkshop.gui.dialogs.NumberDialog;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.number.IntegerFormat;
import dataWorkshop.number.StructureWrapper;
import dataWorkshop.number.UnsignedOffset;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.compiler.CompilerOutput;

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
public class CompilerOutputPane extends MyJTable
	 implements CompilerOutput
{
	private final static String[] COLUMN_NAMES = { "No", "Structure", "Offset", "Description" };

	/******************************************************************************
	*	Constructors
	*/
	public CompilerOutputPane() {
		super(new DefaultTableModel(new Object[0][0], COLUMN_NAMES)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setToolTipText(table.getValueAt(row, 3).toString());
				return c;
			}
		});
	}

	/******************************************************************************
	*	CompilerOutput Interface
	*/
	public void info(ViewDefinitionElement node, long bitOffset, String message)
	{
		add(new Comparable[] {new Long(getModel().getRowCount()), new StructureWrapper(node), new UnsignedOffset(bitOffset), message});
	}
	
	public void error(ViewDefinitionElement node, long bitOffset, String message)
	{
		add(new Comparable[] {new Long(getModel().getRowCount()), new StructureWrapper(node), new UnsignedOffset(bitOffset), message});
	}
	
	public long changeValue(String message, String valueName, long value, IntegerFormat integerFormat)
	{
		NumberDialog dialog = new NumberDialog(Editor.getInstance(), message, valueName, value, integerFormat);
		dialog.setMinimum(0);
		dialog.setMaximum(value);
		dialog.setVisible(true);
		return dialog.getNumber();
	}

	/******************************************************************************
	*	Public Methods
	*/
	public ViewDefinitionElement getSelectedStructure()
	{
		int row = getSelectedRow();
		if (row != -1)
		{
			return ((StructureWrapper) getValueAt(row, 1)).getStructure();
		}
		return null;
	}
}
