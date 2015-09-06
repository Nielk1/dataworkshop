package dataWorkshop.gui.data.structure;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import dataWorkshop.data.Data;

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
public class DataTableModel extends AbstractTableModel {
    
    String[] names;
    boolean[] editable;
    ArrayList data = new ArrayList();
    int columnNo;
    
    public DataTableModel(String[] n) {
        super();
        names = n;
        columnNo = names.length;
        editable = new boolean[columnNo];
        Object[] row0 = new Object[columnNo];
        row0[0] = new Data();
        for (int i = 1; i < columnNo; i++) {
            row0[i] = new String();
        }
        data.add(row0);
    }
    
    /******************************************************************************
     *	Public Methods (overrides AbstractTableMethods)
     */
    public int getColumnCount() {
        return columnNo;
    }
    
    public int getRowCount() {
        return data.size();
    }
    
    public Object getValueAt(int row, int col) {
        Object[] value = (Object[]) data.get(row);
        return value[col];
    }
    
    public String getColumnName(int col) {
        return names[col];
    }
    
    public boolean isCellEditable(int row, int col) {
        return editable[col];
    }
    
    public void setValueAt(Object o, int row, int col) {
        Object[] value = (Object[]) data.get(row);
        value[col] = o;
        data.set(row, value);
    }
    
    public Class getColumnClass(int col) {
        if (col == 0) {
            return (new Data()).getClass();
        }
        else {
            return (new String()).getClass();
        }
    }
    
    /******************************************************************************
     *	Public Methods (new)
     */
    public void setRowCount(int rows) {
        if (rows > data.size()) {
            int newRows = rows - data.size();
            for (int i = 0; i < newRows; i++) {
                Object[] o = new Object[2];
                o[0] = new Data();
                o[1] = new String();
                data.add(o);
            }
        }
        else {
            int delRows = data.size() - rows;
            for (int i = 0; i < delRows; i++) {
                data.remove(rows);
            }
        }
        fireTableDataChanged();
    }
    
    public void setData(ArrayList d) {
        data = d;
        fireTableDataChanged();
    }
    
    public ArrayList getData() {
        return data;
    }
    
    public void setColumnNames(String[] s) {
        names = s;
        fireTableStructureChanged();
    }
    
    public void setColumnsEditable(boolean[] b) {
        editable = b;
    }
}
