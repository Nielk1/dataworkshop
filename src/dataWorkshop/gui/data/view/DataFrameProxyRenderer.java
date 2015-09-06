package dataWorkshop.gui.data.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.MapField;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;

/**
 * Proxy to creates and cache the Gui elements used to build the treeView
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
public class DataFrameProxyRenderer extends AbstractCellEditor
implements DataModelListener, MouseListener, TreeCellEditor, TreeCellRenderer {
    
    protected DataModel dataModel;
    protected TreeView treeView;
    
    HashMap dataFrameEditorCache = new HashMap();
    
    private int CLICKS_TO_START_EDITING = 1;
    
    private DataFrameEditor activeEditor;
    
    /******************************************************************************
     *	Constructors
     */
    public DataFrameProxyRenderer(TreeView treeView, DataModel dataModel) {
        this.dataModel = dataModel;
        this.treeView = treeView;
        dataModel.addDataModelListener(this);
        activeEditor = new DataFrameEditor(new DataFrame(), treeView.getDataViewOption());
    }
    
    /******************************************************************************
     *	DataModelListener Interface
     */
    public void selectionChanged(DataSelectionEvent e) {
        /*
         *  PENDING MPA 2002-08-27
         *
         *  Force a redraw of the tree in case some rendered TreeNodes may have changed
         *  If they did change then the DataSelectionEvent must have reached them before it is
         *  processed here because they already should have updated their selection so they
         *  are painted correctly.
         *  I dont know why this is the case here, but at the moment it works?
         *
         */
        //force a redraw cause some rendered TreeNodes may have changed their selection
        treeView.getJTree().treeDidChange();
        /*
        ViewStructure structure = (ViewStructure) treeView.getModel();
        Field field = structure.getField(e.getOldBitRange());
        if (field != null && !hasDataViewFocus(field)) {
            //remove(field);
            //structure.nodeChanged(field);
        }
         
        field = structure.getField(e.getNewBitRange());
        if (field != null && !hasDataViewFocus(field)) {
            //remove(field);
            //structure.nodeChanged(field);
        }
        treeView.treeDidChange();
         */
    }
    
    public void dataChanged(DataChangeEvent e) {
        treeView.getJTree().treeDidChange();
        /*
        ViewStructure structure = (ViewStructure) treeView.getModel();
        DataFrame[] frames = structure.getIntersectingFrames(e.getBitRange());
        for (int i = 0; i < frames.length; i++) {
            if (!hasDataViewFocus(frames[i])) {
                remove(frames[i]);
                structure.nodeChanged(frames[i]);
            }
        }*/
    }
    
    /******************************************************************************
     *	MouseListener Interface
     */
    public void mouseClicked(MouseEvent e) {
        treeView.selectDataFrame(((DataFrameEditor) ((JPanel) e.getSource()).getParent()).getDataFrame());
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    /******************************************************************************
     *	TreeCellEditor Interface
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
    boolean isSelected, boolean expanded, boolean leaf, int row) {
         /**
          * :TRICKY: MPA 2002-08-06
          *  When a node is edited the corresponding editor has a selection and caret.
          *  So we set isSelected & hasFocus both to false, as the caret and the selection is
          *  enough to indicate having focus
          */
        activeEditor = (DataFrameEditor) getTreeCellRendererComponent(tree, value, false, expanded, leaf, row, false);
        activeEditor.setSelected(true);
        return activeEditor;
    }
    
    public Object getCellEditorValue() {
        return activeEditor.getCellEditorValue();
    }
    
    public boolean isCellEditable(EventObject event) {
        if (event == null) {
            return ((DataFrame) treeView.getJTree().getSelectionPath().getLastPathComponent()).isField();
        }
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            TreePath path = treeView.getJTree().getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            Rectangle rec = treeView.getJTree().getPathBounds(path);
            Point p = mouseEvent.getPoint();
            //Translate the point into Editor Coordinates
            p.translate(-((int) rec.getX()), -((int) rec.getY()));
            DataFrame frame = (DataFrame) path.getLastPathComponent();
            DataFrameEditor editor = (DataFrameEditor) dataFrameEditorCache.get(frame);
            if (!editor.isContainedInHeader(p) && frame.isField() && ((MouseEvent) event).getClickCount() >= CLICKS_TO_START_EDITING) {
                return true;
            }
        }
        return false;
    }
    
    public boolean shouldSelectCell(EventObject event) {
        return false;
        //return activeEditor.getDataFrameEditor().shouldSelectCell(event);
    }
    
    /******************************************************************************
     *	TreeCellRenderer Interface
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
    boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DataFrame node = (DataFrame) value;
        DataViewOption viewOptions = treeView.getDataViewOption();
        DataFrameEditor pane = (DataFrameEditor) dataFrameEditorCache.get(node);
        if (pane == null) {
            if (node instanceof DataEncodingField) {
                pane = new DataFrameEditor((DataEncodingField) node, viewOptions, dataModel);
                pane.setToolTipText(((DataEncodingField) node).getDataConverter().toString());
            }
            else if (node instanceof MapField) {
                pane = new DataFrameEditor((MapField) node, viewOptions, dataModel);
            }
            else if (node instanceof DataFrame) {
                pane = new DataFrameEditor((DataFrame) node, viewOptions);
            }
            //addListeners(pane.getDataFrameEditor());
            dataFrameEditorCache.put(node, pane);
            pane.getHeader().addMouseListener(this);
        }
        /**
         * :TRICKY:Martin Pape:Sep 20, 2003
		 * If the pane hasFocus it must be selected (tree navigation)
		 * If it does not have focus than it cannot be selected.
         */
        pane.setSelected(hasFocus);
        pane.setFocus(hasFocus);
        pane.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 0));
        /**
         *  :REFACTORING: MPA-2002-08-25
         *
         *  if we dont do this the component is sized too small. Only happens when the rendering
         *  component is used and not the editor component
         */
        pane.revalidate();
        //pane.setSize(pane.getPreferredSize());
        return pane;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void startCellEditing() {
        activeEditor.startCellEditing();
    }
    
    public boolean stopCellEditing() {
    	return activeEditor.stopCellEditing();
    }
    
    public boolean hasDataViewFocus() {
        return activeEditor.hasDataViewFocus();
    }
    
    public BitRange getValidBitRange(BitRange bitRange) {
        return activeEditor.getValidBitRange(bitRange);
    }
    
    public void setDataModel(DataModel s) {
        dataModel.removeDataModelListener(this);
        dataModel = s;
        dataModel.addDataModelListener(this);
        //Clear cache
        rerender();
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
    
    public void rerender() {
        treeView.getJTree().stopEditing();
        //if (treeView.isEditing()) {
        //stopCellEditing();
        //}
        DataFrame[] frames = (DataFrame[]) dataFrameEditorCache.keySet().toArray(new DataFrame[0]);
        for (int i = 0; i < frames.length; i++) {
            remove(frames[i]);
        }
    }
    
    /**
     *  Remove frame from cache so it is reinstanciated next time it need to be rendered
     */
    public void remove(DataFrame node) {
        //In case the node was currently being edited we have to stop the editing,
        // because JTree will not rerender the editor for the node
        //if (treeView.isEditing() && treeView.getEditingPath().getLastPathComponent() == node) {
        if (treeView.getJTree().isEditing() && activeEditor.getDataFrame() == node) {
            treeView.getJTree().stopEditing();
        }
        // }
        DataFrameEditor pane = (DataFrameEditor) dataFrameEditorCache.remove(node);
        if (pane != null) {
            pane.setDataModel(new DataModel());
            pane.getHeader().removeMouseListener(this);
        }
    }
    
    /**
     *  Remove node and all child nodes from cache.
     *  Dereference DataModel from editors;
     */
    public void removeRecursive(DataFrame node) {
        remove(node);
        DataFrame[] children = node.getChildren();
        for (int i = 0; i < children.length; i++) {
            remove(children[i]);
        }
    }
}
