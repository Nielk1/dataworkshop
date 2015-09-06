package dataWorkshop.gui.data.structure.atomic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.StringToDataMapping;
import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.atomic.CaseDefinition;
import dataWorkshop.data.structure.atomic.StringToDataMappingDefinition;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.ComboPane;
import dataWorkshop.gui.NumberPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.encoding.DataEncodingPane;
import dataWorkshop.gui.data.structure.DataTablePane;
import dataWorkshop.gui.data.view.DataFieldEditor;

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
public class CasePane extends JPanel implements ActionListener, LocaleStrings
{
	NumberPane bitSizePane;
	NumberPane rowsPane;
	DataTablePane dataTable;
	DataEncodingPane dataUnitBox;
	ComboPane defaultFrame;

	DataViewOption viewOptions;
	DataFieldEditor fieldEditor;
	DataFieldEditor fieldRenderer;

	/******************************************************************************
	 *	Constructors
	 */
	public CasePane()
	{
		super();

		DataWorkshop options = DataWorkshop.getInstance();

		//BitSize Pane
		bitSizePane = new NumberPane(options.getUnsignedOffsetNumber(), "Data Size");
		bitSizePane.setMinimum(0);
		bitSizePane.addActionListener(this);
		bitSizePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Data Size"), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		//Length Pane
		rowsPane = new NumberPane(options.getUnsignedCount(), "Cases");
		rowsPane.setMinimum(0);
		rowsPane.addActionListener(this);
		rowsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		rowsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0), rowsPane.getBorder()));

		//Data Unit Combo Box
		dataUnitBox = new DataEncodingPane(DATA_ENCODING);
		dataUnitBox.addActionListener(this);
		dataUnitBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		dataUnitBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0), dataUnitBox.getBorder()));

		JPanel northPane = new JPanel();
		northPane.setLayout(new BoxLayout(northPane, BoxLayout.Y_AXIS));
		northPane.add(rowsPane);
		northPane.add(Box.createRigidArea(new Dimension(0, 6)));
		northPane.add(dataUnitBox);
		northPane.add(Box.createRigidArea(new Dimension(0, 6)));

		viewOptions = new DataViewOption();
		viewOptions.setDisplayInOneLine(true);
		DataEncodingField field = new DataEncodingField();

		fieldEditor = new DataFieldEditor(field, viewOptions, new DataModel());
		fieldRenderer = new DataFieldEditor(field, viewOptions, new DataModel());

		//Value Pane
		dataTable = new DataTablePane(new String[] { "Case", "Branch" });
		dataTable.setColumnsEditable(new boolean[] { true, true });

		dataTable.setColumnEditor(0, fieldEditor);
		dataTable.setColumnRenderer(0, fieldRenderer);

		defaultFrame = new ComboPane(new Object[0], "Default Branch");

		JPanel tablePane = new JPanel(new BorderLayout());
		tablePane.add(dataTable.getTableHeader(), BorderLayout.NORTH);
		tablePane.add(dataTable, BorderLayout.CENTER);
		tablePane.add(defaultFrame, BorderLayout.SOUTH);

		JPanel dataPane = new JPanel(new BorderLayout());
		dataPane.setBorder(BorderFactory.createTitledBorder(DATA));
		dataPane.add(northPane, BorderLayout.NORTH);
		dataPane.add(tablePane, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(bitSizePane, BorderLayout.NORTH);
		add(dataPane, BorderLayout.CENTER);
	}

	/******************************************************************************
	 *	ActionListener
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == dataUnitBox)
		{
			resizeData();
		}
		else if (source == bitSizePane)
		{
			resizeData();
		}
		else if (source == rowsPane)
		{
			int oldRows = dataTable.getRowCount();
			int newRows = (int) rowsPane.getValue();
			long bitSize = bitSizePane.getValue();
			dataTable.setRowCount(newRows);
			if (newRows > oldRows)
			{
				for (int i = oldRows; i < newRows; i++)
				{
					dataTable.setValueAt(new Data(bitSize), i, 0);
					dataTable.setValueAt(CaseStatement.NONE, i, 1);
				}
			}
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setCaseDefinition(CaseDefinition caseDefinition, String[] possibleValueNames)
	{
		StringToDataMappingDefinition mappingDef = caseDefinition.getStringToDataMappingDefinition();
		StringToDataMapping mapping = mappingDef.getMapping();
		DataEncoding converter = mappingDef.getDataEncoding();
		Data[] values = mapping.getData();
		long bitSize;
		if (values.length == 0)
		{
			bitSize = 0;
		}
		else
		{
			bitSize = values[0].getBitSize();
		}

		bitSizePane.removeActionListener(this);
		bitSizePane.setValue(bitSize);
		bitSizePane.addActionListener(this);

		rowsPane.removeActionListener(this);
		rowsPane.setValue(values.length);
		rowsPane.addActionListener(this);

		dataUnitBox.removeActionListener(this);
		dataUnitBox.setDataEncoding(converter);
		dataUnitBox.addActionListener(this);

		fieldEditor.setDataConverter(converter);
		fieldEditor.setBitSize(bitSize);
		fieldRenderer.setDataConverter(converter);
		fieldRenderer.setBitSize(bitSize);

		//Value Pane
		dataTable.setRowCount(values.length);
		dataTable.setColumnData(0, values);
		dataTable.setColumnData(1, mapping.getNames());
		dataTable.setColumnEditor(1, new DefaultCellEditor(new JComboBox(possibleValueNames)));

		defaultFrame.setItems(possibleValueNames);
		defaultFrame.setSelectedItem(caseDefinition.getDefaultCase());
	}

	public CaseDefinition getCaseDefinition()
	{
		Object[] data = dataTable.getColumnData(0);
		Data[] val = new Data[data.length];
		for (int i = 0; i < data.length; i++)
		{
			val[i] = (Data) data[i];
		}

		data = dataTable.getColumnData(1);
		String[] names = new String[data.length];
		for (int i = 0; i < data.length; i++)
		{
			names[i] = (String) data[i];
		}

		CaseDefinition caseDefinition = new CaseDefinition();
		StringToDataMappingDefinition mapping = new StringToDataMappingDefinition(dataUnitBox.getDataEncoding(), new StringToDataMapping(val, names));
		caseDefinition.setDefaultCase((String) defaultFrame.getSelectedItem());
		caseDefinition.setStringToDataMappingDefinition(mapping);
		return caseDefinition;
	}

	private void resizeData()
	{
		DataEncoding converter = dataUnitBox.getDataEncoding();
		long bitSize = bitSizePane.getValue();

		fieldEditor.setDataConverter(converter);
		fieldEditor.setBitSize(bitSize);
		fieldRenderer.setDataConverter(converter);
		fieldRenderer.setBitSize(bitSize);

		Object[] data = dataTable.getColumnData(0);
		for (int i = 0; i < data.length; i++)
		{
			((Data) data[i]).setBitSize(bitSize);
		}
		dataTable.setColumnData(0, data);

		//We have to re-set the fieldEditor so the column size is recalculated
		dataTable.setColumnEditor(0, fieldEditor);
		dataTable.setColumnRenderer(0, fieldRenderer);
	}
}