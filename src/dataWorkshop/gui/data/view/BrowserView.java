/**
 *		@version 1.0 00/07/29
 * 	@author martin.pape@gmx.de
 *
 *      BrowserView allows the user to select a subview which is then displayed
 *      either in a TreeView (if DataFrame) or in a DataFrameEditor (if Field).
 *      The DataViewNodes are shared between the BrowserView and the subview
 *      for performance reasons (? is this correct) and to keep
 *      selection (Caret if currently editing) in the subview consistent even if nodes change
 */

package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.MapField;
import dataWorkshop.gui.MyPopupMenu;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.editor.Editor;

/**
 *  BrowserView allows the user to select a subview which is then displayed
 *  either in a TreeView (if DataFrame) or in a DataFrameEditor (if Field).
 *  The DataViewNodes are shared between the BrowserView and the subview
 *   for performance reasons (? is this correct) and to keep
 *   selection (Caret if currently editing) in the subview consistent even if nodes change
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
public class BrowserView extends JPanel implements FocusListener, StaticDataView, TreeSelectionListener, TreeModelListener
{
	DataViewOption dataViewOption;
	JTree selectionPane;
	StaticDataView currentStaticDataView;
	DataFieldEditor editorPane;
	TreeView selectionViewPane;
	JSplitPane complexViewPane;

	/******************************************************************************
	 *	Constructors
	 */
	public BrowserView(DataFrame root, DataModel dataModel, DataViewOption dataViewOption)
	{
		this.dataViewOption = dataViewOption;

		selectionPane = new JTree(new DefaultTreeModel(root));
		selectionPane.setRootVisible(true);
		selectionPane.setEditable(false);
		selectionPane.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectionPane.addFocusListener(this);

		selectionViewPane = new TreeView(root, dataModel, dataViewOption);

		MyPopupMenu popupMenu = new MyPopupMenu(Editor.getInstance().getEditorPopup());
		popupMenu.addComponent(this);
		popupMenu.addComponent(selectionPane);

		complexViewPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		complexViewPane.setLeftComponent(new JScrollPane(selectionPane));

		complexViewPane.setRightComponent(selectionViewPane);

		currentStaticDataView = selectionViewPane;

		setLayout(new BorderLayout());
		add(complexViewPane, BorderLayout.CENTER);

		selectRootNode();
		selectionPane.addTreeSelectionListener(this);
		selectionPane.getModel().addTreeModelListener(this);
	}

	/******************************************************************************
	*	FocusListener Interface
	*/
	public void focusGained(FocusEvent e)
	{
		DataModel model = currentStaticDataView.getDataModel();
		model.requestDataViewFocus(this);
		DataFrame frame = (DataFrame) selectionPane.getSelectionPath().getLastPathComponent();
		/**
		 * :NOTE:Martin Pape:Jun 25, 2003
		 *	Is this check necessary
		 */
		if (frame != null)
		{
			model.setBitRange(frame.getBitRange());
		}
	}

	public void focusLost(FocusEvent e)
	{
	}

	/******************************************************************************
	 *	TreeSelection Listener
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		if (e.getNewLeadSelectionPath() != null)
		{
			DataFrame node = (DataFrame) e.getNewLeadSelectionPath().getLastPathComponent();
			int oldDividerLocation = 0;

			oldDividerLocation = complexViewPane.getDividerLocation();
			complexViewPane.remove((Component) currentStaticDataView);

			DataModel dataModel = currentStaticDataView.getDataModel();
			//Dereference DataModel to make object accessable for garbage collection
			currentStaticDataView.setDataModel(new DataModel());
			if (node.isField())
			{
				if (node instanceof DataEncodingField)
				{
					DataFieldEditor editorPane = new DataFieldEditorWithEditorPopup((DataEncodingField) node, getDataViewOption(), dataModel);
					editorPane.setAutomaticLayout(true);
					currentStaticDataView = editorPane;
					complexViewPane.setRightComponent(editorPane);
				}
				else
				{
					MapFieldEditor editorPane = new MapFieldEditor((MapField) node, getDataViewOption(), dataModel);
					currentStaticDataView = editorPane;
					complexViewPane.setRightComponent(editorPane);
				}
			}
			else
			{
				TreeView treeView = new TreeView(node, dataModel, dataViewOption);
				currentStaticDataView = treeView;
				complexViewPane.setRightComponent(treeView);
			}

			complexViewPane.setDividerLocation(oldDividerLocation);
			if (dataModel.hasDataViewFocus(this))
			{
				dataModel.setBitRange(node.getBitRange());
			}
		}
	}

	/******************************************************************************
	 *	TreeModelListener
	 */
	/**
	 *  We need to notify the treePane whenever we change the nodes.
	 *  TreePane uses the same nodes as selectionPane but a different TreeModel
	 */
	public void treeNodesChanged(TreeModelEvent e)
	{
		TreePath changedPath = e.getTreePath();
		int[] childIndices = e.getChildIndices();

		if (childIndices == null || childIndices.length == 0)
		{
			if (selectionPane.getSelectionPath() != null && selectionPane.getSelectionPath().isDescendant(changedPath))
			{
				DataFrame parent = (DataFrame) changedPath.getLastPathComponent();
				if (isTreeView())
				{
					((TreeView) currentStaticDataView).dataFrameChanged(parent);
				}
				else
				{
					currentStaticDataView.setDataFrame(parent);
				}
			}
		}
		else
		{
			for (int i = 0; i < childIndices.length; i++)
			{
				DataFrame child = (DataFrame) ((TreeNode) changedPath.getLastPathComponent()).getChildAt(childIndices[i]);
				TreePath childPath = child.getPathToRoot();
				if (selectionPane.getSelectionPath() != null && selectionPane.getSelectionPath().isDescendant(childPath))
				{
					if (isTreeView())
					{
						((TreeView) currentStaticDataView).dataFrameChanged(child);
					}
					else
					{
						currentStaticDataView.setDataFrame(child);
					}
				}
			}
		}
	}

	public void treeNodesInserted(TreeModelEvent e)
	{
		if (isTreeView())
		{
			TreePath changedPath = e.getTreePath();
			if (selectionPane.getSelectionPath() != null && selectionPane.getSelectionPath().isDescendant(changedPath))
			{
				((TreeView) currentStaticDataView).dataFramesWereInserted((DataFrame) e.getTreePath().getLastPathComponent(), e.getChildIndices());
			}
		}
	}

	public void treeNodesRemoved(TreeModelEvent e)
	{
		if (isTreeView())
		{
			TreePath changedPath = e.getTreePath();
			if (selectionPane.getSelectionPath() != null && selectionPane.getSelectionPath().isDescendant(changedPath))
			{
				((TreeView) currentStaticDataView).dataFramesWereRemoved((DataFrame) e.getTreePath().getLastPathComponent(), e.getChildIndices(), 
					(DataFrame[]) Arrays.asList(e.getChildren()).toArray(new DataFrame[0]));
			}
		}

		/**
		 * if the removed node is above our currently selected node we have lost our selection 
		 * so we default to the root node 
		 */
		if (selectionPane.getSelectionPath().isDescendant(e.getTreePath()))
		{
			selectRootNode();
		}
	}

	public void treeStructureChanged(TreeModelEvent e)
	{
		/*TreeNode node = (TreeNode) e.getTreePath().getLastPathComponent();
		if (isShared(node)) {
		    treePane.treeStructureChanged(e);
		}*/
		//Root has changed
		//if (e.getPath().length == 1) {
			throw new RuntimeException("Placehoplder");		
		//}
		//else if (selectionPane.getSelectionPath().isDescendant(e.getTreePath()))
		//{
		//	((TreeView) currentStaticDataView).treeStructureChanged(e);
			//selectRootNode();
		//}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		//currentStaticDataView.setEnabled(enabled);
		selectionPane.setEnabled(enabled);
	}

	public boolean isTreeView()
	{
		return currentStaticDataView instanceof TreeView;
	}

	public void dataFrameChanged(DataFrame frame)
	{
		((DefaultTreeModel) selectionPane.getModel()).nodeChanged(frame);
	}

	public DataViewOption getDataViewOption()
	{
		return dataViewOption;
	}

	public void setDataViewOption(DataViewOption options)
	{
		this.dataViewOption = options;
		currentStaticDataView.setDataViewOption(options);
		//Redraw broken gui
		revalidate();
	}

	public boolean hasDataViewFocus()
	{
		return currentStaticDataView.getDataModel().hasDataViewFocus(this) | currentStaticDataView.hasDataViewFocus();
	}

	public BitRange getValidBitRange(BitRange bitRange)
	{
		return currentStaticDataView.getValidBitRange(bitRange);
	}

	public void setDataModel(DataModel s)
	{
		currentStaticDataView.setDataModel(s);
		//Rerender all Nodes
		setDataFrame(getDataFrame());
	}

	public DataModel getDataModel()
	{
		return currentStaticDataView.getDataModel();
	}

	public DataFrame getDataFrame()
	{
		return (DataFrame) selectionPane.getModel().getRoot();
	}

	public void setDataFrame(DataFrame root)
	{
		DataFrame[] oldNodes = new DataFrame[1];
		oldNodes[0] = getDataFrame();
		DataFrame[] newNodes = new DataFrame[1];
		newNodes[0] = root;
		if (oldNodes[0].getClassName() == newNodes[0].getClassName())
		{
			oldNodes[0].replace(newNodes[0]);
			ArrayList changedNodes = new ArrayList();
			changedNodes.add(oldNodes[0]);
			replaceTextNodes(oldNodes[0], oldNodes[0].getChildren(), newNodes[0].getChildren(), changedNodes);
			for (int ii = 0; ii < changedNodes.size(); ii++)
			{
				dataFrameChanged((DataFrame) changedNodes.get(ii));
			}
		}
		else
		{
			selectionPane.removeTreeSelectionListener(this);
			selectionPane.getModel().removeTreeModelListener(this);
			selectionPane.setModel(new DefaultTreeModel(root));
			selectionPane.getModel().addTreeModelListener(this);
			selectionPane.addTreeSelectionListener(this);
			selectRootNode();
		}
	}

	/******************************************************************************
	 *	Private Methods
	 */
	/**
	 *  We compare each new node against the old node and if the match we keep the old node.
	 *  We do this to keep as much of the oldTreeModel as possible so the selection (DataViewFocus, selectionPane-nodeselection) stays the same
	 */
	private void replaceTextNodes(MutableTreeNode parent, DataFrame[] textNodes, DataFrame[] newNodes, ArrayList changedNodes)
	{
		int max = Math.min(textNodes.length, newNodes.length);
		if (max > 0)
		{
			for (int i = 0; i < max; i++)
			{
				//Try to replace as many nodes as we can
				if (textNodes[i].getClassName() == newNodes[i].getClassName())
				{
					if (!textNodes[i].equals(newNodes[i]))
					{
						textNodes[i].replace(newNodes[i]);
						changedNodes.add(textNodes[i]);
					}
					replaceTextNodes(textNodes[i], textNodes[i].getChildren(), newNodes[i].getChildren(), changedNodes);
				}
				else
				{
					((DefaultTreeModel) selectionPane.getModel()).removeNodeFromParent(textNodes[i]);
					((DefaultTreeModel) selectionPane.getModel()).insertNodeInto(newNodes[i], (MutableTreeNode) parent, i);
				}
			}
		}
		if (textNodes.length > newNodes.length)
		{
			for (int i = max; i < textNodes.length; i++)
			{
				((DefaultTreeModel) selectionPane.getModel()).removeNodeFromParent(textNodes[i]);
			}
		}
		else if (textNodes.length < newNodes.length)
		{
			for (int i = max; i < newNodes.length; i++)
			{
				((DefaultTreeModel) selectionPane.getModel()).insertNodeInto(newNodes[i], (MutableTreeNode) parent, i);
			}
		}
	}

	private void selectRootNode()
	{
		DataFrame root = (DataFrame) selectionPane.getModel().getRoot();
		selectionPane.setSelectionPath(root.getPathToRoot());
	}
}