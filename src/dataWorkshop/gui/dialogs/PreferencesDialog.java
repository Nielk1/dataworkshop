package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.compiler.CompilerOptions;
import dataWorkshop.gui.CheckBoxNumberPane;
import dataWorkshop.gui.ComboPane;
import dataWorkshop.gui.IntegerFormatFactoryPane;
import dataWorkshop.gui.NumberPane;
import dataWorkshop.gui.data.view.DataViewOptionPane;
import dataWorkshop.gui.editor.Editor;

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
public class PreferencesDialog extends DialogWindow implements LocaleStrings, TreeSelectionListener
{
	public final static String CLASS_NAME = "PreferencesDialog";

	String EDITOR_PANE = "Editor";
	String NUMBER_FORMATTING_PANE = "Number Formatting";
	String COMPILER_PANE = "Compiler";
	String DATA_VIEW_PANE = "Data View Option";

	JTree treePane;
	JPanel configPane;
	JComponent selectedPane;
	JLabel selectedPaneLabel;

	NumberPane singleLineViewsPane;
	NumberPane fontSizePane;
	ComboPane lookAndFeelBox;

	IntegerFormatFactoryPane integerFormatFactoryPane;

	CheckBoxNumberPane pointerThreshold;

	DataViewOptionPane defaultDataViewOptionPane;

	HashMap configPanes = new HashMap();

	final JButton[] buttons = { okButton, cancelButton };
	private static PreferencesDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public PreferencesDialog()
	{
		super(PREFERENCES_DIALOG_TITLE, true);
	}

	/******************************************************************************
	 *	XMLSerializable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	/******************************************************************************
	 *	TreeSelection Listener
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		configPane.remove(selectedPane);
	
		if (node == null || node == treePane.getModel().getRoot())
		{
			selectedPaneLabel.setText(" ");
			selectedPane = new JPanel();
		}
		else
		{
			selectedPaneLabel.setText((String) node.getUserObject());
			selectedPane = new JScrollPane((JPanel) configPanes.get((String) node.getUserObject()));
		}
		
		configPane.add(selectedPane, BorderLayout.CENTER);
		/**
		 * :KLUDGE:Martin Pape:Jun 6, 2003
		 * Why do we have to explicitly force a repaint? Swing should do this for us
		 */
		configPane.repaint();
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static void rebuild()
	{
		instance = null;
	}

	public static PreferencesDialog getInstance()
	{
		if (instance == null)
		{
			instance = new PreferencesDialog();
			instance.buildDialog();
		}
		return instance;
	}

	public boolean show(Frame owner, DataWorkshop eo, Editor editor)
	{
		setLocationRelativeTo(owner);

		fontSizePane.setValue(editor.getFontSize());
		singleLineViewsPane.setValue(editor.getSingleLineDataConverters().length);
		lookAndFeelBox.setSelectedItem(UIManager.getLookAndFeel().getName());

		integerFormatFactoryPane.setIntegerFormatFactory(eo.getIntegerFormatFactory());

		CompilerOptions compilerOptions = eo.getCompilerOptions();
		pointerThreshold.setValue(compilerOptions.getPointerStructureThreshold());
		pointerThreshold.setSelected(compilerOptions.isPointerStructureThresholdEnabled());

		defaultDataViewOptionPane.setDataViewOption(eo.getDataViewOption());

		this.setVisible(true);

		if (wasButtonSelected(okButton))
		{

			editor.setFontSize((int) fontSizePane.getValue());
			editor.setNumberSingleLineViews((int) singleLineViewsPane.getValue());

			String lookAndFeelName = (String) lookAndFeelBox.getSelectedItem();
			UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
			for (int i = 0; i < looks.length; i++)
			{
				if (looks[i].getName().equals(lookAndFeelName))
				{
					editor.setLookAndFeel(looks[i].getClassName());
				}
			}

			eo.setIntegerFormatFactory(integerFormatFactoryPane.getIntegerFormatFactory());

			compilerOptions.setPointerStructureThreshold(pointerThreshold.getValue());
			compilerOptions.setPointerStructureThresholdEnabled(pointerThreshold.isSelected());
			
			eo.setDataViewOption(defaultDataViewOptionPane.getDataViewOption());
			return true;
		}
		else
		{
			return false;
		}
	}

	/******************************************************************************
	 *	Private Methods
	 */
	//Is called only once
	private void buildDialog()
	{
		DataWorkshop options = DataWorkshop.getInstance();

		//
		//  Editor Configuration
		//
		singleLineViewsPane = new NumberPane(options.getUnsignedCount(), SINGLE_LINE_VIEWS);
		singleLineViewsPane.setMinimum(0);
		singleLineViewsPane.setMaximum(10);

		fontSizePane = new NumberPane(options.getUnsignedCount(), FONT_SIZE);
		fontSizePane.setMinimum(1);
		fontSizePane.setMaximum(30);

		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		String[] names = new String[looks.length];
		for (int i = 0; i < looks.length; i++)
		{
			names[i] = looks[i].getName();
		}
		lookAndFeelBox = new ComboPane(names, LOOK_AND_FEEL);

		JPanel editorPane = new JPanel();
		editorPane.setLayout(new BoxLayout(editorPane, BoxLayout.Y_AXIS));
		editorPane.add(singleLineViewsPane);
		editorPane.add(Box.createRigidArea(new Dimension(0, 6)));
		editorPane.add(fontSizePane);
		editorPane.add(Box.createRigidArea(new Dimension(0, 6)));
		editorPane.add(lookAndFeelBox);

		//
		//  Number Formatting Pane
		//
		integerFormatFactoryPane = new IntegerFormatFactoryPane();

		//
		//  Compiler Pane
		//
		JPanel compilerPane = new JPanel();
		compilerPane.setLayout(new BoxLayout(compilerPane, BoxLayout.Y_AXIS));

		pointerThreshold = new CheckBoxNumberPane(options.getUnsignedCount(), "Pointer Threshold");
		compilerPane.add(pointerThreshold);

		//
		//  Default DataView Options
		//
		defaultDataViewOptionPane = new DataViewOptionPane();

		treePane = new JTree(new DefaultMutableTreeNode());
		treePane.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treePane.setRootVisible(false);
		treePane.addTreeSelectionListener(this);
		DefaultTreeModel model = (DefaultTreeModel) treePane.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		model.insertNodeInto(new DefaultMutableTreeNode(EDITOR_PANE), root, root.getChildCount());
		model.insertNodeInto(new DefaultMutableTreeNode(NUMBER_FORMATTING_PANE), root, root.getChildCount());
		model.insertNodeInto(new DefaultMutableTreeNode(COMPILER_PANE), root, root.getChildCount());
		model.insertNodeInto(new DefaultMutableTreeNode(DATA_VIEW_PANE), root, root.getChildCount());

		treePane.expandPath(new TreePath(model.getPathToRoot(root)));

		configPanes.put(EDITOR_PANE, editorPane);
		configPanes.put(NUMBER_FORMATTING_PANE, integerFormatFactoryPane);
		configPanes.put(COMPILER_PANE, compilerPane);
		configPanes.put(DATA_VIEW_PANE, defaultDataViewOptionPane);

		selectedPane = new JPanel();
		selectedPaneLabel = new JLabel(" ");
		selectedPaneLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new BorderLayout());
		labelPane.add(selectedPaneLabel, BorderLayout.CENTER);

		configPane = new JPanel();
		configPane.setLayout(new BorderLayout());
		configPane.add(labelPane, BorderLayout.NORTH);
		configPane.add(selectedPane, BorderLayout.CENTER);
		configPane.add(Box.createRigidArea(new Dimension(12, 0)), BorderLayout.WEST);
		
		//Build the main pane
		treePane.setMinimumSize(treePane.getPreferredSize());
		treePane.setMaximumSize(treePane.getPreferredSize());
		JPanel pane = getMainPane();
		pane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		pane.setLayout(new BorderLayout());
		pane.add(new JScrollPane(treePane), BorderLayout.WEST);
		pane.add(configPane, BorderLayout.CENTER);
	
		treePane.setSelectionRow(0);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[1]);

		pack();
	}
}
