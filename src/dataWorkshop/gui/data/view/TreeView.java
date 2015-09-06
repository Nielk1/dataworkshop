package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.MyPopupMenu;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.editor.MyKeyBinding;

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
public class TreeView extends JPanel implements StaticDataView, TreeSelectionListener
{

	DataViewOption dataViewOptions;
	DataModel dataModel;
	DataFrameProxyRenderer proxyRenderer;
	JTree tree;

	public static String START_OR_STOP_EDITING_ACTION_NAME = "start-or-stop-editing";

	static MyKeyBinding[] DEFAULT_KEY_BINDINGS = { new MyKeyBinding(START_OR_STOP_EDITING_ACTION_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)), };

	static HashMap actions = new HashMap();
	static {
		Action a;
		a = new AbstractAction(START_OR_STOP_EDITING_ACTION_NAME)
		{
			public void actionPerformed(ActionEvent e)
			{
				TreeView treeView = (TreeView) e.getSource();
				JTree tree = treeView.getJTree();
				if (tree.isEditing())
				{
					DataFrame frame = (DataFrame) tree.getEditingPath().getLastPathComponent();
					treeView.selectDataFrame(frame);
				}
				else
				{
					tree.startEditingAtPath(tree.getSelectionPath());
					((DataFrameProxyRenderer) tree.getCellEditor()).startCellEditing();
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);
	}
	static {
		setKeyBindings(DEFAULT_KEY_BINDINGS);
	}

	/******************************************************************************
	 *	Constructors
	 */
	public TreeView(DataFrame root, DataModel dataModel, DataViewOption dataViewOption)
	{
		super();
		this.dataModel = dataModel;
		this.dataViewOptions = dataViewOption;

		tree = new JTree(new DefaultTreeModel(root));
		/**
		 * :TRICKY:Martin Pape:Sep 24, 2003
		 * 
		 * Our TreeCells have different height so we have to force the tree to query
		 * the tree cell renderer for the size of each cell. Using the Metal LaF this
		 * seems to work by default but not on the other LaF
		 */
		tree.setRowHeight(-1);
		ToolTipManager.sharedInstance().registerComponent(tree);

		//setRootVisible(false);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		proxyRenderer = new DataFrameProxyRenderer(this, dataModel);
		tree.setCellRenderer(proxyRenderer);
		tree.setCellEditor(proxyRenderer);
		tree.addTreeSelectionListener(this);
		tree.setSelectionModel(new MyDefaultTreeSelectionModel());

		MyPopupMenu popupMenu = new MyPopupMenu(Editor.getInstance().getEditorPopup());
		popupMenu.addComponent(this);
		popupMenu.addComponent(tree);

		checkRootHandleVisibility();

		Iterator it = actions.values().iterator();
		while (it.hasNext())
		{
			registerAction((Action) it.next());
		}

		setLayout(new BorderLayout());
		add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	/******************************************************************************
	 *	TreeSelection Listener
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		DataFrame node = (DataFrame) e.getPath().getLastPathComponent();
		/**
		 *   If Editing was not started we select the whole node
		 */
		if (!tree.isEditing())
		{
			dataModel.requestDataViewFocus(this);
			dataModel.setSelection(node.getBitOffset(), node.getBitSize());
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	/*
		public void treeStructureChanged(TreeModelEvent e)
		{
			Object[] o = e.getChildren();
			for (int i = 0; i < o.length; i++)
			{
				proxyRenderer.removeRecursive((DataFrame) o[i]);
			}
			checkRootHandleVisibility();
		}
		*/

	public void dataFrameChanged(DataFrame frame)
	{
		proxyRenderer.remove(frame);
		((DefaultTreeModel) tree.getModel()).nodeChanged(frame);
	}

	public void dataFramesWereInserted(DataFrame parent, int[] childIndices)
	{
		((DefaultTreeModel) tree.getModel()).nodesWereInserted(parent, childIndices);
		//checkRootHandleVisibility();
	}

	public void dataFramesWereRemoved(DataFrame parent, int[] childIndices, DataFrame[] removedChildren)
	{
		((DefaultTreeModel) tree.getModel()).nodesWereRemoved(parent, childIndices, removedChildren);
		for (int i = 0; i < removedChildren.length; i++)
		{
			proxyRenderer.removeRecursive(removedChildren[i]);
		}
		//checkRootHandleVisibility();
	}

	/**
	 * :REFACTORING:  MPA 2002-11-02
	 * The casting done here is quite insecure.
	 * :NOTE: MPA 2002-11-02
	 *  We always want a valueChanged Event to occur even if the selection and the new selection
	 *  is the same.
	 */
	public void selectDataFrame(DataFrame frame)
	{
		if (tree.isEditing())
		{
			tree.stopEditing();
		}
		tree.getSelectionModel().clearSelection();
		tree.setSelectionPath(new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(frame)));
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		tree.setEnabled(enabled);
	}

	public static MyKeyBinding[] getKeyBindings()
	{
		Action[] a = (Action[]) (new ArrayList(actions.values())).toArray(new Action[0]);
		MyKeyBinding[] bindings = new MyKeyBinding[a.length];
		for (int i = 0; i < a.length; i++)
		{
			bindings[i] = new MyKeyBinding(a[i]);
		}
		return bindings;
	}

	public static void setKeyBindings(MyKeyBinding[] bindings)
	{
		for (int i = 0; i < bindings.length; i++)
		{
			Action a = (Action) actions.get(bindings[i].getActionName());
			if (a != null)
			{
				a.putValue(Action.ACCELERATOR_KEY, bindings[i].getKeyStroke());
			}
		}
	}

	public JTree getJTree()
	{
		return tree;
	}

	public void registerAction(Action a)
	{
		InputMap keyMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap map = getActionMap();
		keyMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), (String) a.getValue(Action.NAME));
		map.put((String) a.getValue(Action.NAME), a);
	}

	public DataViewOption getDataViewOption()
	{
		return dataViewOptions;
	}

	public void setDataViewOption(DataViewOption options)
	{
		this.dataViewOptions = options;
		//Rerender all Nodes
		setDataFrame(getDataFrame());
	}

	public boolean hasDataViewFocus()
	{
		if (!dataModel.hasDataViewFocus(this))
		{
			return proxyRenderer.hasDataViewFocus();
		}
		return true;
	}

	public BitRange getValidBitRange(BitRange bitRange)
	{
		return proxyRenderer.getValidBitRange(bitRange);
	}

	public void setDataModel(DataModel s)
	{
		dataModel = s;
		proxyRenderer.setDataModel(dataModel);
		//Rerender all Nodes
		setDataFrame(getDataFrame());
	}

	public DataModel getDataModel()
	{
		return dataModel;
	}

	public DataFrame getDataFrame()
	{
		return (DataFrame) tree.getModel().getRoot();
	}

	public void setDataFrame(DataFrame root)
	{
		proxyRenderer.rerender();
		tree.setModel(new DefaultTreeModel(root));
		checkRootHandleVisibility();
	}

	/******************************************************************************
	 *	Private Methods
	 */
	/*
	 *  We only display the root handles if there is a root child which is not a field
	 */
	private void checkRootHandleVisibility()
	{
		boolean showRoot = true;
		boolean showRootHandle = true;
		DataFrame root = (DataFrame) tree.getModel().getRoot();
		if (root.isField())
		{
			showRoot = true;
			showRootHandle = false;
		}
		else
		{
			showRoot = false;
			showRootHandle = true;
			/*
			DataFrame[] children = root.getChildren();
			for (int i = 0; i < children.length; i++) {
			    if (!children[i].isField()) {
			        showRootHandle = true;
			    }
			}*/
		}

		tree.setShowsRootHandles(showRootHandle);
		tree.setRootVisible(showRoot);
	}

	/******************************************************************************
	 *	MyDefaultSelectionModel
	 *
	 *  :KLUDGE: (MPA 2002-01-29)
	 *  shortly after calling fireValueChanged(TreeSelectionEvent e) after
	 *  completing editing a null pointer Exception occurs and I dont know why.
	 *  This is a quick fix. We catch the Exception and resume.
	 */
	public class MyDefaultTreeSelectionModel extends DefaultTreeSelectionModel
	{

		public MyDefaultTreeSelectionModel()
		{
			super();
		}

		protected void fireValueChanged(TreeSelectionEvent e)
		{
			try
			{
				super.fireValueChanged(e);
			}
			catch (Exception ee)
			{
				System.out.println(ee);
			}
		}
	}
}