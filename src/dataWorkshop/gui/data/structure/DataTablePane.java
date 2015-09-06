package dataWorkshop.gui.data.structure;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import dataWorkshop.data.Data;
import dataWorkshop.gui.MyJTable;

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
public class DataTablePane extends MyJTable
{

	DefaultTableCellRenderer CELL_RENDERER = new DefaultTableCellRenderer();
	/**
	 *  :KLUDGE: (MPA 2002-09-20)
	 *
	 *  We need this to do DATA.getClass(). We cannot use Data.class because
	 *  this would fuck up the obfuscator
	 */
	static Data DATA = new Data();
	private Logger logger;

	/******************************************************************************
	 *	Constructors
	 */
	public DataTablePane(String[] names)
	{
		super(new DataTableModel(names));
		logger = Logger.getLogger(this.getClass().getName());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setRowCount(int rowCount)
	{
		((DataTableModel) getModel()).setRowCount(rowCount);
	}

	public void setColumnsEditable(boolean[] editable)
	{
		((DataTableModel) getModel()).setColumnsEditable(editable);
	}

	public void setColumnRenderer(int col, TableCellRenderer renderer)
	{
		setDefaultRenderer(getModel().getColumnClass(col), renderer);
		TableColumn column = getColumnModel().getColumn(col);
		if (getColumnClass(col) == DATA.getClass())
		{
			Component comp = CELL_RENDERER.getTableCellRendererComponent(this, column.getHeaderValue(), false, false, 0, 0);
			int headerWidth = (int) comp.getMinimumSize().getWidth();
			comp = renderer.getTableCellRendererComponent(this, new Data(), false, false, 0, 0);
			int width = Math.max((int) comp.getPreferredSize().getWidth(), headerWidth);
			column.setMinWidth(width);
			column.setPreferredWidth(width);
		}
		else
		{
			Component comp = CELL_RENDERER.getTableCellRendererComponent(this, column.getHeaderValue(), false, false, 0, 0);
			column.setMinWidth((int) comp.getPreferredSize().getWidth());
		}
		((DataTableModel) getModel()).fireTableDataChanged();
	}

	public void setColumnEditor(int col, TableCellEditor editor)
	{
		getDefaultEditor(getModel().getColumnClass(col)).cancelCellEditing();
		setDefaultEditor(getModel().getColumnClass(col), editor);
	}

	public void setColumnData(int col, Object[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			getModel().setValueAt(data[i], i, col);
		}
	}

	public Object[] getColumnData(int col)
	{
		/**
		 *   KLUDGE:Martin Pape:Apr 4, 2003 
		 *
		 *  There seems to be a bug in JTable.stopCellEditing(). If the DefaultEditor
		 *  is a String editor and editing was not started before calling stopCellEditing()
		 *  a Null Pointer Execption occurs.
		 */
		try
		{
			getDefaultEditor(getModel().getColumnClass(col)).stopCellEditing();
		}
		catch (Exception e)
		{
			logger.finest(e.toString());
		}
		Object[] o = new Object[getRowCount()];
		for (int i = 0; i < o.length; i++)
		{
			o[i] = getValueAt(i, col);
		}
		return o;
	}
}