package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreeCellRenderer;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataEncodingFieldLayout;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.logging.Logger;
import dataWorkshop.number.UnsignedOffset;

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
public class DataFieldEditor extends FieldEditor implements ComponentListener, AdjustmentListener, StateChangeListener, TableCellRenderer, TreeCellRenderer, TableCellEditor
{

	protected Color backgroundSelectionColor = UIManager.getColor("Table.selectionBackground");
	protected Color backgroundNonSelectionColor = UIManager.getColor("Table.textBackground");
	protected Color borderSelectionColor = UIManager.getColor("Table.selectionBorderColor");
	protected Border focusBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, borderSelectionColor);
	protected Border noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

	static protected int clicksToStart = 2;

	boolean automaticLayout = false;
	protected HashSet cellEditorListeners = new HashSet();

	DataFieldTextPane dataPane;
	JTextArea offsetPane;
	JScrollBar scrollBar;
	DataViewOption viewOptions;

	/******************************************************************************
	 *	Constructors
	 */
	public DataFieldEditor()
	{
		this(new DataEncodingField(), new DataViewOption(), new DataModel());
	}

	public DataFieldEditor(DataEncodingField field, DataViewOption viewOptions, DataModel dataModel)
	{
		this.viewOptions = viewOptions;
		dataPane = new DataFieldTextPane(viewOptions.createLayout(field), dataModel);
		dataPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
		DataEncodingFieldLayout fieldLayout = dataPane.getDataFieldLayout();

		offsetPane = new JTextArea();
		offsetPane.setFont(Editor.getInstance().getBoldFont());
		offsetPane.setEditable(false);
		offsetPane.setHighlighter(null);

		setLayout(new BorderLayout());
		add(offsetPane, BorderLayout.WEST);
		add(dataPane, BorderLayout.CENTER);

		scrollBar = new JScrollBar(JScrollBar.VERTICAL);
		scrollBar.setFocusable(true);
		scrollBar.addAdjustmentListener(this);
		calcScrollBarDisplay(fieldLayout);
		setRenderOffset(fieldLayout.isRenderOffset());

		dataPane.addStateChangeListener(this);
		addComponentListener(this);
		/**
		 *  :TRICKY: (MPA 2002-09-09)
		 *  Simulate the JScrollPane look by using the same border
		 */
		setBorder(BorderUIResource.getEtchedBorderUIResource());
		render();
	}

	/******************************************************************************
	 *	AdjustmentListener
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		dataPane.setLine(scrollBar.getValue());
		renderOffset();
	}

	/******************************************************************************
	 *	Component Listener
	 */
	public void componentShown(ComponentEvent e)
	{
		if (automaticLayout)
		{
			//recalc MaxDataPerPage
			setDataFieldLayout(getDataFieldLayout());
		}
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentResized(ComponentEvent e)
	{
		if (automaticLayout)
		{
			//recalc MaxDataPerPage
			setDataFieldLayout(getDataFieldLayout());
		}
	}

	public void componentHidden(ComponentEvent e)
	{
	}

	/******************************************************************************
	 *	StateChangeListener
	 */
	public void stateChanged(StateChangeEvent e)
	{
		scrollBar.setValue(dataPane.getLine());
	}

	/******************************************************************************
	 *	TreeCellRenderer Interface
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		setDataModel(new DataModel((Data) value));
		setBorder(null);
		return this;
	}

	/******************************************************************************
	 *	TreeCellEditor Interface
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
	{
		setDataModel(new DataModel((Data) value));
		setSelected(isSelected);
		setBorder(null);
		return this;
	}

	/******************************************************************************
		 *	TableCellEditor Interface
		 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		setDataModel(new DataModel((Data) value));
		setSelected(isSelected);
		setBorder(null);
		return this;
	}

	/******************************************************************************
	 *	TableCellRenderer Interface
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		setDataModel(new DataModel((Data) value));
		setSelected(isSelected);
		setFocus(hasFocus);
		return this;
	}

	/******************************************************************************
	 *	CellEditor Interface
	 */
	public Object getCellEditorValue()
	{
		return getDataModel().getSelection(getDataFieldLayout().getBitRange());
	}

	public boolean isCellEditable(EventObject event)
	{
		if (event instanceof MouseEvent)
		{
			return ((MouseEvent) event).getClickCount() >= clicksToStart;
		}
		return true;
	}

	public boolean shouldSelectCell(EventObject event)
	{
		return true;
	}

	public boolean stopCellEditing()
	{
		dataPane.commitEdits();
		fireEditingStopped();
		return true;
	}

	public void cancelCellEditing()
	{
		fireEditingCanceled();
	}

	public void addCellEditorListener(CellEditorListener l)
	{
		cellEditorListeners.add(l);
	}

	public void removeCellEditorListener(CellEditorListener l)
	{
		cellEditorListeners.remove(l);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void startCellEditing()
	{
		dataPane.startEditing();
	}

	public void setSelected(boolean selected)
	{
		if (selected)
		{
			setBackground(backgroundSelectionColor);
			offsetPane.setBackground(backgroundSelectionColor);
			dataPane.setBackground(backgroundSelectionColor);
			scrollBar.setBackground(backgroundSelectionColor);
		}
		else
		{
			setBackground(backgroundNonSelectionColor);
			offsetPane.setBackground(backgroundNonSelectionColor);
			dataPane.setBackground(backgroundNonSelectionColor);
			scrollBar.setBackground(backgroundNonSelectionColor);
		}
		validate();
	}

	public void setFocus(boolean hasFocus)
	{
		if (hasFocus)
		{
			setBorder(focusBorder);
		}
		else
		{
			setBorder(noFocusBorder);
		}
		validate();
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		scrollBar.setEnabled(enabled);
		offsetPane.setEnabled(enabled);
		dataPane.setEnabled(enabled);
	}

	public void setAutomaticLayout(boolean automaticLayout)
	{
		this.automaticLayout = automaticLayout;
		if (automaticLayout)
		{
			dataPane.displayMaxDataPerPage();
		}
	}

	public void setDataModel(DataModel dataModel)
	{
		dataPane.setDataModel(dataModel);
	}

	public DataModel getDataModel()
	{
		return dataPane.getDataModel();
	}

	public boolean hasDataViewFocus()
	{
		return dataPane.hasDataViewFocus();
	}

	public BitRange getValidBitRange(BitRange bitRange)
	{
		return dataPane.getValidBitRange(bitRange);
	}

	public DataEncodingFieldLayout getDataFieldLayout()
	{
		return dataPane.getDataFieldLayout();
	}

	public void setDataFieldLayout(DataEncodingFieldLayout layout)
	{
		dataPane.setDataFieldLayout(layout);
		setRenderOffset(layout.isRenderOffset());
		renderOffset();
		calcScrollBarDisplay(getDataFieldLayout());
		if (automaticLayout)
		{
			dataPane.displayMaxDataPerPage();
			renderOffset();
			calcScrollBarDisplay(getDataFieldLayout());
		}
	}

	public void displayMaxDataInOneLine()
	{
		dataPane.displayMaxDataInOneLine();
		calcScrollBarDisplay(getDataFieldLayout());
		renderOffset();
	}

	public void render()
	{
		dataPane.renderData();
		renderOffset();
	}

	public void setDataConverter(DataEncoding converter)
	{
		DataEncodingFieldLayout layout = getDataFieldLayout();
		layout.setDataConverter(converter);
		setDataFieldLayout(layout);
	}

	public void setBitSize(long bitSize)
	{
		DataEncodingFieldLayout layout = getDataFieldLayout();
		layout.setBitSize(bitSize);
		setDataFieldLayout(layout);
	}

	public void setBitOffset(long bitOffset)
	{
		DataEncodingFieldLayout layout = getDataFieldLayout();
		layout.setBitOffset(bitOffset);
		setDataFieldLayout(layout);
	}

	public DataFrame getDataFrame()
	{
		return getDataFieldLayout().getDataField();
	}

	public void setDataFrame(DataFrame frame)
	{
		setDataFieldLayout(getDataViewOption().createLayout((DataEncodingField) frame));
	}

	public void setDataViewOption(DataViewOption viewOptions)
	{
		this.viewOptions = viewOptions;
		setDataFieldLayout(viewOptions.createLayout((DataEncodingField) getDataFrame()));
	}

	public DataViewOption getDataViewOption()
	{
		return viewOptions;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void fireEditingStopped()
	{
		Object[] listeners = cellEditorListeners.toArray();
		ChangeEvent event = new ChangeEvent(this);
		for (int i = listeners.length - 1; i >= 0; i -= 1)
		{
			((CellEditorListener) listeners[i]).editingStopped(event);
		}
	}

	private void fireEditingCanceled()
	{
		Object[] listeners = cellEditorListeners.toArray();
		ChangeEvent event = new ChangeEvent(this);
		for (int i = listeners.length - 1; i >= 0; i -= 1)
		{
			((CellEditorListener) listeners[i]).editingCanceled(event);
		}
	}

	private void renderOffset()
	{
		DataEncodingFieldLayout fieldLayout = getDataFieldLayout();
		PlainDocument doc = new PlainDocument();
		try
		{
			if (fieldLayout.isRenderOffset())
			{
				long bitsPerLine = fieldLayout.getBitsPerLine();

				int dotsPerLine = (new UnsignedOffset(0)).toString().length() + 1;
				int size = 0;
				if (bitsPerLine > 0)
				{
					size = Math.min(fieldLayout.getLinesPerPage() * dotsPerLine, (fieldLayout.getLines() * dotsPerLine) - (dataPane.getLine() * dotsPerLine));
					size = Math.max(0, size);
				}
				else
				{
					size = 0;
				}

				if (size > 0)
				{
					//Kill last NewLine
					size -= 1;
				}
				char[] renderedOffset = new char[size];
				char[] offset;

				long bitOffset = fieldLayout.dotToBitOffset(dataPane.getLine() * fieldLayout.getDotsPerLine()) - fieldLayout.getDataField().getRootFrame().getBitOffset();
				if (size > 0)
				{
					offset = (new UnsignedOffset(bitOffset)).toString().toCharArray();
					System.arraycopy(offset, 0, renderedOffset, 0, offset.length);
					bitOffset += bitsPerLine;
					for (int dot = dotsPerLine - 1; dot < size; dot += dotsPerLine)
					{
						renderedOffset[dot] = '\n';
						offset = (new UnsignedOffset(bitOffset)).toString().toCharArray();
						System.arraycopy(offset, 0, renderedOffset, dot + 1, offset.length);
						bitOffset += bitsPerLine;
					}
				}

				doc.insertString(0, new String(renderedOffset), null);
			}
		}
		catch (BadLocationException e)
		{
			Logger.getLogger(DataFieldEditor.class).warning(e);
		}
		offsetPane.setDocument(doc);
	}

	private void setRenderOffset(boolean renderOffset)
	{
		if (renderOffset)
		{
			offsetPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
		}
		else
		{
			offsetPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		}
	}

	/*
	 *  Determine if scrollbar is necessary or not
	 *  and if scrollbar is needed adjust the scrollbar model
	 *  according to fieldLayout
	 */
	private void calcScrollBarDisplay(DataEncodingFieldLayout fieldLayout)
	{
		remove(scrollBar);
		if (fieldLayout.getBitsPerPage() < fieldLayout.getBitSize())
		{
			scrollBar.setBlockIncrement(fieldLayout.getLinesPerPage());
			BoundedRangeModel model = scrollBar.getModel();
			model.setMinimum(0);
			model.setMaximum(fieldLayout.getLines());
			model.setExtent(fieldLayout.getLinesPerPage());
			scrollBar.setModel(model);
			add(scrollBar, BorderLayout.EAST);
		}
	}
}