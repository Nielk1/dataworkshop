package dataWorkshop.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * adds sorting by clicking on table header and some other convenience methods
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
public class MyJTable extends JTable 
	implements MouseListener
{
	/******************************************************************************
	*	Constructors
	*/
	public MyJTable(TableModel model)
	{
		super(model);
		getTableHeader().addMouseListener(this);
	}

	/******************************************************************************
	*	MouseListener
	*/
	public void mouseClicked(MouseEvent e)
	{
		TableColumnModel columnModel = getColumnModel();
		int viewColumn = columnModel.getColumnIndexAtX(e.getX());
		int column = convertColumnIndexToModel(viewColumn);
		if (e.getClickCount() == 1 && column != -1)
		{
			sortColumn(column, !e.isShiftDown());
		}
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	/******************************************************************************
	*	Public Methods
	*/
	public void clear()
	{
		DefaultTableModel model = (DefaultTableModel) getModel();
		while (model.getRowCount() != 0)
		{
			model.removeRow(0);
		}
	}

	/******************************************************************************
	*	Private Methods
	*/
	protected void add(Comparable[] row)
	{
		DefaultTableModel model = (DefaultTableModel) getModel();
		model.addRow(row);
	}
	
	private void sortColumn(int sortingColumn, boolean natural)
	{
		ArrayList tableRows = new ArrayList();
		for (int row = 0; row < getRowCount(); row++)
		{
			ArrayList l = new ArrayList();
			for (int col = 0; col < getColumnCount(); col++)
			{
				if (col != sortingColumn)
				{
					l.add(getValueAt(row, col));
				}
			}
			tableRows.add(new TableRow((Comparable) getValueAt(row, sortingColumn), l));
		}

		Collections.sort(tableRows);
		if (!natural)
		{
			Collections.reverse(tableRows);
		}

		for (int row = 0; row < getRowCount(); row++)
		{
			TableRow element = (TableRow) tableRows.get(row);
			setValueAt((Object) element.getSortingColumn(), row, sortingColumn);
			ArrayList otherElements = (ArrayList) element.getOtherColumns();
			for (int col = 0; col < getColumnCount(); col++)
			{
				if (col != sortingColumn)
				{
					setValueAt(otherElements.remove(0), row, col);
				}
			}
		}
	}

	public class TableRow implements Comparable
	{

		Comparable sortingColumn;
		Object otherColumns;

		public TableRow(Comparable sortingColumn, ArrayList otherColumns)
		{
			this.sortingColumn = sortingColumn;
			this.otherColumns = otherColumns;
		}

		public Comparable getSortingColumn()
		{
			return sortingColumn;
		}

		public Object getOtherColumns()
		{
			return otherColumns;
		}

		public int compareTo(Object o)
		{
			return sortingColumn.compareTo(((TableRow) o).getSortingColumn());
		}
	}
}
