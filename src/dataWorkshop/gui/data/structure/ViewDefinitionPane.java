package dataWorkshop.gui.data.structure;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.structure.compiler.ValidatorOutput;
import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.MapFieldDefinition;
import dataWorkshop.data.structure.RepeatStatement;
import dataWorkshop.data.structure.DataEncodingFieldDefinition;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.UntilDelimiterFieldDefinition;
import dataWorkshop.data.structure.UntilDelimiterRepeatStatement;
import dataWorkshop.data.structure.UntilOffsetFieldDefinition;
import dataWorkshop.data.structure.UntilOffsetRepeatStatement;
import dataWorkshop.gui.MyPopupMenu;
import dataWorkshop.gui.data.structure.compiler.CompilerOutputPane;
import dataWorkshop.gui.data.structure.compiler.ValidatorOutputPane;
import dataWorkshop.gui.editor.AbstractEditorAction;
import dataWorkshop.gui.editor.StructureAction;
import dataWorkshop.gui.editor.StructureClipboard;
import dataWorkshop.gui.editor.StructureClipboardAction;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.logging.Logger;

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
public class ViewDefinitionPane extends JPanel implements ActionListener, ListSelectionListener, TreeModelListener, TreeSelectionListener, LocaleStrings, ValidatorOutput
{
	public static String NEW_NODE = "New";
	public static String INSERT = "Insert";
	public static String ADD_ABOVE = "Add Above";
	public static String ADD_BELOW = "Add Below";

	public static String NEW_NODE_INSERT = NEW_NODE + " " + INSERT;
	public static String NEW_NODE_ADD_ABOVE = NEW_NODE + " " + ADD_ABOVE;
	public static String NEW_NODE_ADD_BELOW = NEW_NODE + " " + ADD_BELOW;

	public static String CUT_DEFINITION_NODE_ACTION = "Cut";
	public static String COPY_DEFINITION_NODE_ACTION = "Copy";
	public static String PASTE_INSERT_DEFINITION_NODE_ACTION = "Paste (Insert)";
	public static String PASTE_ABOVE_DEFINITION_NODE_ACTION = "Paste (Above)";
	public static String PASTE_BELOW_DEFINITION_NODE_ACTION = "Paste (Below)";
	public static String DELETE_DEFINITION_NODE_ACTION = "Delete";

	public JTree tree;
	public ConfigurationPane configurationPane;

	Validator validator;
	ValidatorOutputPane validatorOutput;
	CompilerOutputPane compilerOutputPane;
	JLabel nodeTypeLabel;
	JPanel buttonPane;

	JButton applyButton = new JButton("Apply");
	JButton cancelButton = new JButton("Cancel");

	private ArrayList stateChangeListeners = new ArrayList();

	private boolean isValidStructure = true;

	/******************************************************************************
	 *	Constructors
	 */
	public ViewDefinitionPane()
	{
		this(new RootStatement());
	}

	public ViewDefinitionPane(RootStatement structure)
	{
		super(new BorderLayout());

		tree = new JTree(new DefaultTreeModel(structure));
		//tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setExpandsSelectedPaths(true);
		tree.addTreeSelectionListener(this);
		tree.getModel().addTreeModelListener(this);

		validatorOutput = new ValidatorOutputPane();
		validatorOutput.getSelectionModel().addListSelectionListener(this);
		validator = new Validator(this);

		compilerOutputPane = new CompilerOutputPane();
		compilerOutputPane.getSelectionModel().addListSelectionListener(this);

		JMenu subMenu;

		JPopupMenu popupMenu = new JPopupMenu();

		subMenu = new JMenu(NEW_NODE_INSERT);
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new CaseStatement()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new RepeatStatement()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new UntilOffsetRepeatStatement()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new UntilDelimiterRepeatStatement()));
		subMenu.addSeparator();
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new MapFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new DataEncodingFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new UntilOffsetFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.InsertDefinitionNode(this, new UntilDelimiterFieldDefinition()));
		popupMenu.add(subMenu);

		subMenu = new JMenu(NEW_NODE_ADD_ABOVE);
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new CaseStatement()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new RepeatStatement()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new UntilOffsetRepeatStatement()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new UntilDelimiterRepeatStatement()));
		subMenu.addSeparator();
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new MapFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new DataEncodingFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new UntilOffsetFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddAboveDefinitionNode(this, new UntilDelimiterFieldDefinition()));
		popupMenu.add(subMenu);

		subMenu = new JMenu(NEW_NODE_ADD_BELOW);
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new CaseStatement()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new RepeatStatement()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new UntilOffsetRepeatStatement()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new UntilDelimiterRepeatStatement()));
		subMenu.addSeparator();
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new MapFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new DataEncodingFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new UntilOffsetFieldDefinition()));
		subMenu.add(new ViewDefinitionPane.AddBelowDefinitionNode(this, new UntilDelimiterFieldDefinition()));
		popupMenu.add(subMenu);
		popupMenu.addSeparator();

		popupMenu.add(ViewDefinitionPane.createCutDefinitionNode(this));
		popupMenu.add(ViewDefinitionPane.createCopyDefinitionNode(this));
		popupMenu.add(ViewDefinitionPane.createPasteInsertDefinitionNode(this));
		popupMenu.add(ViewDefinitionPane.createPasteAboveDefinitionNode(this));
		popupMenu.add(ViewDefinitionPane.createPasteBelowDefinitionNode(this));
		popupMenu.add(ViewDefinitionPane.createDeleteDefinitionNode(this));

		MyPopupMenu menu = new MyPopupMenu(popupMenu);
		menu.addComponent(tree);

		JPanel labelPane = new JPanel();
		nodeTypeLabel = new JLabel();
		labelPane.add(nodeTypeLabel, BorderLayout.CENTER);

		configurationPane = new ConfigurationPane();

		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(applyButton);
		buttonPane.add(cancelButton);
		applyButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(labelPane, BorderLayout.NORTH);
		p.add(new JScrollPane(configurationPane), BorderLayout.CENTER);
		p.add(buttonPane, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(new JScrollPane(tree), JSplitPane.LEFT);
		splitPane.add(p, JSplitPane.RIGHT);

		JTabbedPane outputPanes = new JTabbedPane(JTabbedPane.BOTTOM);
		outputPanes.add(new JScrollPane(validatorOutput), "Validation");
		outputPanes.add(new JScrollPane(compilerOutputPane), "Compiler Output");

		JSplitPane southPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		southPane.add(splitPane, JSplitPane.TOP);
		southPane.add(outputPanes, JSplitPane.BOTTOM);

		add(southPane, BorderLayout.CENTER);

		selectRootNode();
	}

	/******************************************************************************
	*	Action Listener
	*/
	/**
	 *  A Button in the DefinitionNode ConfigurationPane was pressed
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == applyButton)
		{
				//Replace the oldNode with the newNode
				ViewDefinitionElement changedNode = getSelectedNode();
				configurationPane.getDefinitionNode(changedNode);
				DefaultTreeModel model = getModel();
				model.nodeChanged(changedNode);
		}
		else if (source == cancelButton)
		{
			configurationPane.setDefinitionNode(getSelectedNode());
		}
	}

	/******************************************************************************
	*	ListSelectionListener Interface
	*/
	public void valueChanged(ListSelectionEvent event)
	{
		Object source = event.getSource();
		ViewDefinitionElement node = null;
		if (source == compilerOutputPane.getSelectionModel())
		{
			node = compilerOutputPane.getSelectedStructure();
		}
		else if (source == validatorOutput.getSelectionModel())
		{
			node = validatorOutput.getSelectedStructure();
		}
		if (node != null)
		{
			TreePath path = node.getPathToRoot();
			tree.setSelectionPath(path);
			tree.scrollPathToVisible(path);
		}
	}

	/******************************************************************************
	*	TreeModelListener
	*/
	public void treeNodesChanged(TreeModelEvent e)
	{
		validatorOutput.clear();
		isValidStructure = validator.validate((ViewDefinitionElement) getModel().getRoot());
		fireStateChanged();
	}

	public void treeNodesInserted(TreeModelEvent e)
	{
		validatorOutput.clear();
		isValidStructure = validator.validate((ViewDefinitionElement) getModel().getRoot());
		fireStateChanged();
	}

	public void treeNodesRemoved(TreeModelEvent e)
	{
		validatorOutput.clear();
		isValidStructure = validator.validate((ViewDefinitionElement) getModel().getRoot());
		fireStateChanged();
	}

	public void treeStructureChanged(TreeModelEvent e)
	{
		validatorOutput.clear();
		isValidStructure = validator.validate((ViewDefinitionElement) getModel().getRoot());
		fireStateChanged();
	}

	/******************************************************************************
	*	TreeSelection Listener
	*/
	public void valueChanged(TreeSelectionEvent e)
	{
		ViewDefinitionElement node = (ViewDefinitionElement) e.getPath().getLastPathComponent();
		nodeTypeLabel.setText(node.getName());
		configurationPane.setDefinitionNode(node);
		cancelButton.setEnabled(true);
		applyButton.setEnabled(true);
	}

	/******************************************************************************
	*	Validator Output Interface
	*/
	public void error(ViewDefinitionElement node, String error)
	{
		validatorOutput.error(node, error);
	}

	public void warning(ViewDefinitionElement node, String wrning)
	{
		validatorOutput.warning(node, wrning);
	}

	public void info(ViewDefinitionElement node, String info)
	{
		validatorOutput.info(node, info);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public CompilerOutputPane getCompilerOutput()
	{
		return compilerOutputPane;
	}

	public boolean isValidStructure()
	{
		return isValidStructure;
	}

	public void addStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.add(l);
	}

	public void removeStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.remove(l);
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		tree.setEnabled(enabled);
		cancelButton.setEnabled(enabled);
		applyButton.setEnabled(enabled);
		configurationPane.setEnabled(enabled);
		nodeTypeLabel.setEnabled(enabled);
	}

	public void setStructure(RootStatement structure)
	{
		getModel().setRoot(structure);
		selectRootNode();
	}

	public RootStatement getStructure()
	{
		return (RootStatement) getModel().getRoot();
	}

	public ViewDefinitionElement getSelectedNode()
	{
		return (ViewDefinitionElement) tree.getSelectionPath().getLastPathComponent();
	}

	public void selectRootNode()
	{
		selectNode((TreeNode) getModel().getRoot());
	}

	/******************************************************************************
	*	Protected Methods
	*/
	protected DefaultTreeModel getModel()
	{
		return (DefaultTreeModel) tree.getModel();
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void fireStateChanged()
	{
		Object[] listeners = stateChangeListeners.toArray();
		StateChangeEvent changeEvent = new StateChangeEvent(this);
		for (int i = listeners.length - 1; i >= 0; i -= 1)
		{
			((StateChangeListener) listeners[i]).stateChanged(changeEvent);
		}
	}

	private void selectNode(TreeNode node)
	{
		tree.setSelectionPath(new TreePath(getModel().getPathToRoot(node)));
	}

	/******************************************************************************
	 *	Static Methods
	 */
	public static Action createCutDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureAction(view, CUT_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				StructureClipboard.getInstance().setDefinitionNode(structurePane.getSelectedNode());
				structurePane.getModel().removeNodeFromParent(structurePane.getSelectedNode());
				structurePane.selectRootNode();
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				this.setEnabled(structurePane.getModel().getRoot() != e.getPath().getLastPathComponent());
			}
		};
		return a;
	}

	public static Action createCopyDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureAction(view, COPY_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				StructureClipboard.getInstance().setDefinitionNode(structurePane.getSelectedNode());
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				this.setEnabled(structurePane.getModel().getRoot() != e.getPath().getLastPathComponent());
			}
		};
		return a;
	}

	public static Action createPasteInsertDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureClipboardAction(view, PASTE_INSERT_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				ViewDefinitionElement selectedNode = structurePane.getSelectedNode();
				ViewDefinitionElement newNode = StructureClipboard.getInstance().getDefinitionNode();
				structurePane.getModel().insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
				structurePane.selectNode(newNode);
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				this.setEnabled(StructureClipboard.getInstance().hasCopy() && ((ViewDefinitionElement) e.getPath().getLastPathComponent()).getAllowsChildren());
			}
		};
		return a;
	}

	public static Action createPasteAboveDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureClipboardAction(view, PASTE_ABOVE_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				ViewDefinitionElement parent = (ViewDefinitionElement) structurePane.getSelectedNode().getParent();
				ViewDefinitionElement newNode = StructureClipboard.getInstance().getDefinitionNode();
				structurePane.getModel().insertNodeInto(newNode, parent, parent.getIndex(structurePane.getSelectedNode()));
				structurePane.selectNode(newNode);
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				boolean isRootNode = structurePane.getModel().getRoot() == e.getPath().getLastPathComponent();
				this.setEnabled(StructureClipboard.getInstance().hasCopy() && !isRootNode);
			}
		};
		return a;
	}

	public static Action createPasteBelowDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureClipboardAction(view, PASTE_BELOW_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				ViewDefinitionElement parent = (ViewDefinitionElement) structurePane.getSelectedNode().getParent();
				ViewDefinitionElement newNode = StructureClipboard.getInstance().getDefinitionNode();
				structurePane.getModel().insertNodeInto(newNode, parent, parent.getIndex(structurePane.getSelectedNode()) + 1);
				structurePane.selectNode(newNode);
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				boolean isRootNode = structurePane.getModel().getRoot() == e.getPath().getLastPathComponent();
				this.setEnabled(StructureClipboard.getInstance().hasCopy() && !isRootNode);
			}
		};
		return a;
	}

	public static Action createDeleteDefinitionNode(ViewDefinitionPane view)
	{
		Action a = new StructureAction(view, DELETE_DEFINITION_NODE_ACTION)
		{
			public void doAction()
			{
				structurePane.getModel().removeNodeFromParent(structurePane.getSelectedNode());
				structurePane.selectRootNode();
			}

			public void valueChanged(TreeSelectionEvent e)
			{
				this.setEnabled(structurePane.getModel().getRoot() != e.getPath().getLastPathComponent());
			}
		};
		return a;
	}

	/******************************************************************************
	 *	Insert Node Action
	 */
	public class InsertDefinitionNode extends AbstractEditorAction implements TreeSelectionListener
	{

		Class klass;
		ViewDefinitionPane view;

		public InsertDefinitionNode(ViewDefinitionPane view, ViewDefinitionElement node)
		{
			super(INSERT + node.getClassName() + "Action");
			this.view = view;
			this.klass = node.getClass();
			view.tree.addTreeSelectionListener(this);
			putValue(AbstractAction.NAME, node.getName());
		}

		public void doAction()
		{
			try
			{
				ViewDefinitionElement selectedNode = getSelectedNode();
				ViewDefinitionElement newNode = (ViewDefinitionElement) klass.newInstance();
				getModel().insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
				selectNode(newNode);
			}
			catch (IllegalAccessException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
			catch (InstantiationException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
		}

		public void valueChanged(TreeSelectionEvent e)
		{
			this.setEnabled(((ViewDefinitionElement) e.getPath().getLastPathComponent()).getAllowsChildren());
		}
	}

	/******************************************************************************
	 *	Insert Node Action
	 */
	public class AddAboveDefinitionNode extends AbstractEditorAction implements TreeSelectionListener
	{

		Class klass;
		ViewDefinitionPane view;

		public AddAboveDefinitionNode(ViewDefinitionPane view, ViewDefinitionElement node)
		{
			super(ADD_ABOVE + node.getClassName() + "Action");
			this.view = view;
			this.klass = node.getClass();
			view.tree.addTreeSelectionListener(this);
			putValue(AbstractAction.NAME, node.getName());
		}

		public void doAction()
		{
			try
			{
				ViewDefinitionElement parent = (ViewDefinitionElement) getSelectedNode().getParent();
				ViewDefinitionElement newNode = (ViewDefinitionElement) klass.newInstance();
				getModel().insertNodeInto(newNode, parent, parent.getIndex(getSelectedNode()));
				selectNode(newNode);
			}
			catch (IllegalAccessException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
			catch (InstantiationException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
		}

		public void valueChanged(TreeSelectionEvent e)
		{
			this.setEnabled(view.getModel().getRoot() != e.getPath().getLastPathComponent());
		}
	}

	/******************************************************************************
	 *	Insert Node Action
	 */
	public class AddBelowDefinitionNode extends AbstractEditorAction implements TreeSelectionListener
	{

		Class klass;
		ViewDefinitionPane view;

		public AddBelowDefinitionNode(ViewDefinitionPane view, ViewDefinitionElement node)
		{
			super(ADD_BELOW + node.getClassName() + "Action");
			this.view = view;
			this.klass = node.getClass();
			view.tree.addTreeSelectionListener(this);
			putValue(AbstractAction.NAME, node.getName());
		}

		public void doAction()
		{
			try
			{
				ViewDefinitionElement parent = (ViewDefinitionElement) getSelectedNode().getParent();
				ViewDefinitionElement newNode = (ViewDefinitionElement) klass.newInstance();
				getModel().insertNodeInto(newNode, parent, parent.getIndex(getSelectedNode()) + 1);
				selectNode(newNode);
			}
			catch (IllegalAccessException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
			catch (InstantiationException e)
			{
				Logger.getLogger(this.getClass()).severe(e);
			}
		}

		public void valueChanged(TreeSelectionEvent e)
		{
			this.setEnabled(view.getModel().getRoot() != e.getPath().getLastPathComponent());
		}
	}
}