package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.CellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataFrameLayout;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.MapField;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.number.UnsignedCount;
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
public class DataFrameEditor extends JPanel
    implements CellEditor, StaticDataView {
    
    DataFrame frame;
    DataViewOption viewOptions;
    JTextField labelPane;
    
    //these two values are set to false in the constructor
    // they need to be true, so the selection and focus gets painted
    // in the setFocus & setSelected Methods
    protected boolean selected = true;
    protected boolean hasFocus = true;
    
    protected Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");
    protected Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");
    protected Color borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
    protected Border focusBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, borderSelectionColor);
    protected Border noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    protected Font boldFont = (new JLabel()).getFont().deriveFont(Font.BOLD);
    
    // Is lazily created in addInformation()
    protected JTextArea textPane;
    protected JPanel headerPane;
    protected FieldEditor fieldEditor;
    
    /******************************************************************************
     *	Constructors
     */
    public DataFrameEditor(DataEncodingField field, DataViewOption viewOptions, DataModel dataModel) {
        this(field, viewOptions);
		DataFieldEditorWithEditorPopup editor = new DataFieldEditorWithEditorPopup(field, viewOptions, dataModel);
		editor.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0), BorderFactory.createEtchedBorder()));
		editor.setBackground(backgroundNonSelectionColor);
        add(editor, BorderLayout.CENTER);
        
		fieldEditor = editor;
    }
    
    public DataFrameEditor(MapField field, DataViewOption viewOptions, DataModel dataModel) {
        this(field, viewOptions);
		MapFieldEditor editor = new MapFieldEditor(field, viewOptions, dataModel);
		editor.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		editor.setBackground(backgroundNonSelectionColor);
        add(editor, BorderLayout.CENTER);
        
		fieldEditor = editor;
    }
    
    public DataFrameEditor(DataFrame frame, DataViewOption viewOptions) {
        super();
        this.frame = frame;
        this.viewOptions = viewOptions;
        DataFrameLayout frameLayout = viewOptions.createLayout(frame);
        
        headerPane = new JPanel();
        headerPane.setLayout(new BoxLayout(headerPane, BoxLayout.Y_AXIS));
        
        if (frame.getLabel().length() > 0) {
            String label = frame.getLabel();
            int arrayIndex = frame.getArrayIndex();
            if (arrayIndex != -1) {
                label += " [" + (new UnsignedCount(arrayIndex)).toString(false) + "]";
            }
            labelPane = new JTextField(label);
            labelPane.setEditable(false);
            labelPane.setFont(boldFont);
            labelPane.setBackground(backgroundNonSelectionColor);
            labelPane.setForeground(Color.black);
            labelPane.setHighlighter(null);
            labelPane.setBorder(null);
            labelPane.setAlignmentY(Component.LEFT_ALIGNMENT);
            headerPane.add(labelPane);
        }
        setLayout(new BorderLayout());
        add(headerPane, BorderLayout.NORTH);
        setBackground(backgroundNonSelectionColor);
        
        setSelected(false);
        setFocus(false);
        
        if (frameLayout.isRenderSize()) {
            addInformation("BitSize: " + (new UnsignedOffset(frame.getBitSize())).toString(false));
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public Dimension getSize() {
    	Dimension dim = super.getSize();
    	dim.setSize(dim.getWidth(), dim.getHeight() + 50);
		return dim;
    }
    
    public void startCellEditing() {
    	if (fieldEditor != null) {
			fieldEditor.startCellEditing();
    	}
    }
    
    public void addInformation(String info) {
        if (textPane == null) {
            textPane = new JTextArea();
            textPane.setEditable(false);
            textPane.setHighlighter(null);
            headerPane.add(textPane);
        }
        //Is this the first line ?
        if (textPane.getDocument().getLength() > 0) {
            textPane.insert("\n", textPane.getDocument().getLength());
        }
        textPane.insert(info, textPane.getDocument().getLength());
    }
    
    public JPanel getHeader() {
        return headerPane;
    }
    
    /*
     *  Decide if the Point p (relative to this Component) is contained in the Header
     */
    public boolean isContainedInHeader(Point p) {
        return getHeader().contains(p);
    }
    
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            if (selected) {
                if (labelPane != null) {
                    labelPane.setBackground(backgroundSelectionColor);
                }
                headerPane.setBackground(backgroundSelectionColor);
            }
            else {
                if (labelPane != null) {
                    labelPane.setBackground(backgroundNonSelectionColor);
                }
                headerPane.setBackground(backgroundNonSelectionColor);
            }
        }
        validate();
    }
    
    public void setFocus(boolean focus) {
        if (focus != hasFocus) {
            this.hasFocus = focus;
            if (hasFocus) {
                headerPane.setBorder(focusBorder);
            }
            else {
                headerPane.setBorder(noFocusBorder);
            }
        }
        validate();
    }
    
    public DataFrame getDataFrame() {
        if (fieldEditor != null) {
            return fieldEditor.getDataFrame();
        }
        return frame;
    }
    
    public void setDataFrame(DataFrame frame) {
        this.frame = frame;
        buildInfoPane();
        if (fieldEditor != null) {
			fieldEditor.setDataFrame(frame);
        }
    }
    
    public DataViewOption getDataViewOption() {
        if (fieldEditor != null) {
            return fieldEditor.getDataViewOption();
        }
        return viewOptions;
    }
    
    public void setDataViewOption(DataViewOption viewOptions) {
        this.viewOptions = viewOptions;
         if (fieldEditor != null) {
			fieldEditor.setDataViewOption(viewOptions);
        }
    }
    
    public boolean hasDataViewFocus() {
         if (fieldEditor != null) {
            return fieldEditor.hasDataViewFocus();
        }
        return false;
        
    }
    
     public BitRange getValidBitRange(BitRange bitRange) {
         if (fieldEditor != null) {
            return fieldEditor.getValidBitRange(bitRange);
         }
         return bitRange;
     }
    
    public void setDataModel(DataModel dataModel) {
         if (fieldEditor != null) {
			fieldEditor.setDataModel(dataModel);
        }
    }
    
     public DataModel getDataModel() {
          if (fieldEditor != null) {
            return fieldEditor.getDataModel();
        }
        return null;
    }
    
    public Object getCellEditorValue() {
         if (fieldEditor != null) {
            return fieldEditor.getCellEditorValue();
        }
        return null;
    }
    
    public boolean isCellEditable(EventObject event) {
          if (fieldEditor != null) {
            return fieldEditor.isCellEditable(event);
        }
        return false;
    }
    
    public boolean shouldSelectCell(EventObject event) {
          if (fieldEditor != null) {
            return fieldEditor.shouldSelectCell(event);
        }
        return false;
    }
    
    public boolean stopCellEditing() {
          if (fieldEditor != null) {
            return fieldEditor.stopCellEditing();
        }
        return false;
    }
    
    public void cancelCellEditing() {
          if (fieldEditor != null) {
			fieldEditor.cancelCellEditing();
        }
    }
    
    public void addCellEditorListener(CellEditorListener l) {
          if (fieldEditor != null) {
			fieldEditor.addCellEditorListener(l);
        }
    }
    
    public void removeCellEditorListener(CellEditorListener l) {
          if (fieldEditor != null) {
			fieldEditor.removeCellEditorListener(l);
        }
    }
    
    private void buildInfoPane() {
        DataFrameLayout frameLayout = viewOptions.createLayout(frame);
        remove(headerPane);
        /*
         * NOTE: MPA 2002-11-02
         *  We must keep the info pane reference, because we give it out
         * with getHeaderPane().
         */
        headerPane.removeAll();
        textPane = null;
        if (frame.getLabel().length() > 0) {
            labelPane = new JTextField(frame.getLabel());
            labelPane.setEditable(false);
            labelPane.setFont(boldFont);
            labelPane.setBackground(backgroundNonSelectionColor);
            labelPane.setForeground(Color.black);
            labelPane.setHighlighter(null);
            labelPane.setBorder(null);
            labelPane.setAlignmentY(Component.LEFT_ALIGNMENT);
            headerPane.add(labelPane);
        }
        add(headerPane, BorderLayout.NORTH);
        if (frameLayout.isRenderSize()) {
            addInformation("BitSize: " + (new UnsignedOffset(frame.getBitSize())).toString(false));
        }
    }
}