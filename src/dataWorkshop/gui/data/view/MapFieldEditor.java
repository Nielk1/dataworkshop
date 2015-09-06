package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.MapField;
import dataWorkshop.data.view.MapFieldLayout;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
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
public class MapFieldEditor extends FieldEditor
implements ActionListener, CellEditor, DataModelListener, FocusListener {
    
    protected MapFieldLayout fieldLayout;
    protected JTextArea offsetPane;
    protected DataModel dataModel;
    protected DefaultCellEditor cellEditor;
    protected JComboBox comboBox;
    protected DataViewOption viewOptions;
    
    /******************************************************************************
     *	Constructors
     */
    public MapFieldEditor(MapField mapField, DataViewOption viewOptions, DataModel dataModel) {
        this.fieldLayout = viewOptions.createLayout(mapField);
        this.viewOptions = viewOptions;
        this.dataModel = dataModel;
        
        offsetPane = new JTextArea();
        offsetPane.setFont(Editor.getInstance().getBoldFont());
        offsetPane.setEditable(false);
        offsetPane.setHighlighter(null);
        
        comboBox = new JComboBox(fieldLayout.getMapField().getPossibleValueNames());
        comboBox.addActionListener(this);
        comboBox.addFocusListener(this);
        cellEditor = new DefaultCellEditor(comboBox);
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        setLayout(new BorderLayout());
        add(offsetPane, BorderLayout.WEST);
        add(comboBox, BorderLayout.CENTER);
        
        dataModel.addDataModelListener(this);
        setRenderOffset(fieldLayout.isRenderOffset());
        render();
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        Data d = getMapField().getValue((String) comboBox.getSelectedItem());
        if (d != null) {
            if (!dataModel.getSelection().equals(d)) {
                dataModel.paste(d);
            }
        }
        else {
            comboBox.setSelectedItem(getMapField().render(dataModel.getData()));
        }
    }
    
    /******************************************************************************
     *	DataModelListener Interface
     */
    public void selectionChanged(DataSelectionEvent e) {
    }
    
    public void dataChanged(DataChangeEvent e) {
        rerender(e.getBitRange());
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void focusGained(FocusEvent e) {
        requestDataViewFocus();
        dataModel.setBitRange(fieldLayout.getMapField().getBitRange());
    }
    
    public void focusLost(FocusEvent e) {
    }
    
    /******************************************************************************
     *	TreeCellRenderer Interface
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
    boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setDataModel(new DataModel((Data) value));
        return this;
    }
    
    /******************************************************************************
     *	TableCellRenderer Interface
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
    boolean isSelected, boolean hasFocus, int row, int column) {
        setDataModel(new DataModel((Data) value));
        return this;
    }
    
    /******************************************************************************
     *	TableCellEditor Interface
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
    boolean isSelected, int row, int column) {
        setDataModel(new DataModel((Data) value));
        return this;
    }
    
    /******************************************************************************
     *	TreeCellEditor Interface
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
    boolean isSelected, boolean expanded, boolean leaf, int row) {
        setDataModel(new DataModel((Data) value));
        return this;
    }
    
    public Object getCellEditorValue() {
        return dataModel.getSelection(fieldLayout.getMapField().getBitRange());
    }
    
    public boolean isCellEditable(EventObject event) {
        return true;
    }
    
    public boolean shouldSelectCell(EventObject event) {
        return cellEditor.shouldSelectCell(event);
    }
    
    public boolean stopCellEditing() {
        return cellEditor.stopCellEditing();
    }
    
    public void cancelCellEditing() {
        cellEditor.cancelCellEditing();
    }
    
    public void addCellEditorListener(CellEditorListener l) {
        cellEditor.addCellEditorListener(l);
    }
    
    public void removeCellEditorListener(CellEditorListener l) {
        cellEditor.removeCellEditorListener(l);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void startCellEditing() {
        comboBox.requestFocus();
        comboBox.showPopup();
        requestDataViewFocus();
    }
    
    public void setDataFrame(DataFrame frame) {
        setMapFieldLayout(getDataViewOption().createLayout((MapField) frame));
    }
    
    public void setMapFieldLayout(MapFieldLayout layout) {
        setRenderOffset(layout.isRenderOffset());
        renderOffset();
    }
    
    public void setDataViewOption(DataViewOption viewOptions) {
        this.viewOptions = viewOptions;
        setMapFieldLayout(viewOptions.createLayout((MapField) getDataFrame()));
    }
    
    public DataViewOption getDataViewOption() {
        return viewOptions;
    }
    
    public MapField getMapField() {
        return fieldLayout.getMapField();
    }
    
    public DataFrame getDataFrame() {
        return getMapField();
    }
    
    public boolean hasDataViewFocus() {
        return dataModel.hasDataViewFocus(this);
    }
    
     public BitRange getValidBitRange(BitRange bitRange) {
        return bitRange;
     }
    
    public void requestDataViewFocus() {
        dataModel.requestDataViewFocus(this);
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
    
    public void setDataModel(DataModel model) {
        dataModel.removeDataModelListener(this);
        dataModel = model;
        dataModel.addDataModelListener(this);
        renderData();
    }
    
    public void render() {
        renderData();
        renderOffset();
    }
    
    public void renderData() {
        comboBox.removeActionListener(this);
        comboBox.removeAllItems();
        String[] names = getMapField().getPossibleValueNames();
        for (int i = 0; i < names.length; i++) {
            comboBox.addItem(names[i]);
        }
        comboBox.setSelectedItem(getMapField().render(dataModel.getData()));
        comboBox.addActionListener(this);
    }
    
    public void rerender(BitRange bitRange) {
        if (fieldLayout.getBitRange().hasIntersection(bitRange)) {
            comboBox.removeActionListener(this);
            comboBox.setSelectedItem(getMapField().render(dataModel.getData()));
            comboBox.addActionListener(this);
        }
    }
    
    protected void renderOffset() {
        PlainDocument doc = (PlainDocument) offsetPane.getDocument();
        try {
            doc.remove(0, doc.getLength());
            if (fieldLayout.getBitSize() > 0 && fieldLayout.isRenderOffset()) {
                doc.insertString(0, (new UnsignedOffset(fieldLayout.getBitOffset() - fieldLayout.getMapField().getRootFrame().getBitOffset())).toString(), null);
            }
        } catch (BadLocationException e) {
        	Logger.getLogger(this.getClass()).severe(e);
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    private void setRenderOffset(boolean renderOffset) {
        if (renderOffset) {
            offsetPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        }
        else {
            offsetPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }
    }
}
