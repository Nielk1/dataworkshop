package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.CheckBoxNumberPane;
import dataWorkshop.gui.MyThread;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.dialogs.ErrorDialogFactory;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.gui.data.structure.ViewDefinitionPane;
import dataWorkshop.gui.data.structure.compiler.CompilerOutputPane;
import dataWorkshop.logging.Logger;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.compiler.Compiler;

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
public class DefaultDynamicDataView extends JPanel 
	implements ActionListener, DynamicDataView, StateChangeListener, DataModelListener, LocaleStrings
{

	public final static String LOCK_OFFSET = "Lock";
	public final static String COMPILE_VIEW = "Compile View";

	public final static int STRUCTURE_TAB = 0;
	public final static int VIEW_TAB = 1;

	public final static String STRUCTURE_TAB_TITLE = "Structure";
	public final static String VIEW_TAB_TITLE = "View";

	DataModel dataModel;
	BrowserView staticDataView;

	CheckBoxNumberPane offsetPane;
	JCheckBox compileViewBox;
	JPanel northPane;

	JTabbedPane mainPane;
	ViewDefinitionPane structurePane;

	private CompileThread compileThread;
	private boolean compile = false;
	private boolean isViewEnabled = true;

	private ArrayList stateChangeListeners = new ArrayList();

	/******************************************************************************
	 *	Constructors
	 */
	public DefaultDynamicDataView(RootStatement structure, DataModel dataModel)
	{
		super();
		this.dataModel = dataModel;
		structurePane = new ViewDefinitionPane();
		structurePane.addStateChangeListener(this);
		compileThread = new CompileThread(this);

		DataWorkshop options = DataWorkshop.getInstance();

		//We need to do this before we start the compiler, because the dataViewOptions are stored in this object
		staticDataView = new BrowserView(new DataEncodingField(), dataModel, options.getDataViewOption());

		//Main Pane
		compileViewBox = new JCheckBox(COMPILE_VIEW);
		compileViewBox.setSelected(true);

		// !! If you change this, make sure rebuild is still working !!
		offsetPane = new CheckBoxNumberPane(options.getUnsignedOffsetNumber(), LOCK_OFFSET, false);
		offsetPane.setSelected(false);
		offsetPane.setMinimum(0);
		offsetPane.setStepSize(DataWorkshop.OFFSET_STEP_SIZE);

		// !! If you change this, make sure rebuild is still working !!
		JPanel northPane = new JPanel();
		northPane.setLayout(new BoxLayout(northPane, BoxLayout.X_AXIS));
		northPane.add(Box.createHorizontalGlue());
		northPane.add(compileViewBox);
		northPane.add(Box.createRigidArea(new Dimension(12, 0)));
		northPane.add(offsetPane);

		mainPane = new JTabbedPane();
		mainPane.add(STRUCTURE_TAB_TITLE, structurePane);
		mainPane.add(VIEW_TAB_TITLE, staticDataView);
		mainPane.setBorder(BorderFactory.createEmptyBorder());
		mainPane.setSelectedIndex(VIEW_TAB);

		setLayout(new BorderLayout());
		add(northPane, BorderLayout.NORTH);
		add(mainPane, BorderLayout.CENTER);

		if (isOffsetLocked())
		{
			offsetPane.setValue((long) 0);
		}
		else
		{
			offsetPane.setValue(dataModel.getSelectionOffset());
		}

		compileViewBox.addActionListener(this);
		dataModel.addDataModelListener(this);
		offsetPane.addActionListener(this);
		
		//		This will trigger the compilation of the structure
		setStructure(structure);
	}

	/******************************************************************************
	 *	Action Listener
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == offsetPane)
		{
			compile();
		}
		else if (source == compileViewBox)
		{
			if (compileViewBox.isSelected() & hasValidStructure())
			{
				setViewEnabled(true);
				compile();
			}
			else
			{
				setViewEnabled(false);
			}
			//the structure might have become valid or invalid and we have to update the recompile actions
			fireStateChanged();
		}
	}
	
	/******************************************************************************
	 *	DataModel Listener
	 */
	public void selectionChanged(DataSelectionEvent e)
	{
		if (!staticDataView.hasDataViewFocus() && isOffsetLocked())
		{
			offsetPane.setValue(e.getNewBitRange().getStart());
		}
	}

	public void dataChanged(DataChangeEvent e)
	{
		compile();
	}

	/******************************************************************************
	 *	StateChangeListener Interface
	 */
	public void stateChanged(StateChangeEvent e)
	{
		setViewEnabled(compileViewBox.isSelected() && hasValidStructure());
		if (hasValidStructure())
		{
			mainPane.setIconAt(STRUCTURE_TAB, null);
			compile();
		}
		else
		{
			mainPane.setIconAt(STRUCTURE_TAB, Editor.getInstance().getErrorIcon());
		}
		//the structure might have become valid or invalid and we have to update the recompile action
		fireStateChanged();
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void addStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.add(l);
	}

	public void removeStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.remove(l);
	}

	public boolean hasValidStructure()
	{
		return structurePane.isValidStructure();
	}

	public RootStatement getStructure()
	{
		return structurePane.getStructure();
	}

	public void setStructure(RootStatement structure)
	{
		structurePane.setStructure(structure);
	}

	public boolean hasDataViewFocus()
	{
		return staticDataView.hasDataViewFocus();
	}

	public BitRange getValidBitRange(BitRange bitRange)
	{
		return staticDataView.getValidBitRange(bitRange);
	}

	public boolean isOffsetLocked()
	{
		return offsetPane.isSelected();
	}

	public long getBitOffset()
	{
		return offsetPane.getValue();
	}

	public DataViewOption getDataViewOption()
	{
		return staticDataView.getDataViewOption();
	}

	public void setDataViewOption(DataViewOption options)
	{
		staticDataView.setDataViewOption(options);
	}

	public DataFrame getDataFrame()
	{
		return staticDataView.getDataFrame();
	}

	public void setDataModel(DataModel model)
	{
		dataModel.removeDataModelListener(this);
		if (isViewEnabled())
		{
			staticDataView.setDataModel(model);
		}
		dataModel = model;
		dataModel.addDataModelListener(this);
		if (isOffsetLocked())
		{
			offsetPane.setValue(model.getSelectionOffset());
		}
		compile();
	}

	public DataModel getDataModel()
	{
		return dataModel;
	}

	public void rebuild()
	{
		northPane.remove(offsetPane);
		offsetPane.removeActionListener(this);
		boolean isSelected = offsetPane.isSelected();
		offsetPane = new CheckBoxNumberPane(DataWorkshop.getInstance().getUnsignedOffsetNumber(), LOCK_OFFSET);
		offsetPane.setMinimum(0);
		if (isSelected)
		{
			offsetPane.setValue(dataModel.getSelectionOffset());
		}
		northPane.add(offsetPane);
		offsetPane.addActionListener(this);
		/**
		 * :TODO:Martin Pape:Jun 14, 2003
		 * We need to rebuild the structurePane and the viewPane
		 */
		compile();
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		structurePane.setEnabled(enabled);
		offsetPane.setEnabled(enabled);
		compileViewBox.setEnabled(enabled);
		mainPane.setEnabled(enabled);
	}

	public void close()
	{
		//prevent compilation
		compileViewBox.setSelected(false);
		staticDataView.setDataModel(new DataModel());
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

	private boolean isViewEnabled()
	{
		return isViewEnabled;
	}

	private void setViewEnabled(boolean enabled)
	{
		if (isViewEnabled != enabled)
		{
			this.isViewEnabled = enabled;
			mainPane.setEnabledAt(VIEW_TAB, enabled);
			if (enabled)
			{
				staticDataView.setDataModel(dataModel);
			}
			else
			{
				mainPane.setSelectedIndex(STRUCTURE_TAB);
				staticDataView.setDataModel(new DataModel());
			}
		}
	}

	private void setCompile(boolean compile)
	{
		this.compile = compile;
		if (compile && !compileThread.isAlive())
		{
			compileThread = new CompileThread(this);
			compileThread.start();
		}
	}

	private boolean hasCompile()
	{
		return compile;
	}

	/**
		 * 	Will only do a compile if the 
		 * 	- structuere is valid
		 * 	- isCompileView is set to true
		 */
	private void compile()
	{
		if (hasValidStructure() & compileViewBox.isSelected())
		{
			setCompile(true);
		}
	}

	/******************************************************************************
	 *	Compile Thread
	 */
	public class CompileThread extends MyThread
	{

		DefaultDynamicDataView dataView;

		public CompileThread(DefaultDynamicDataView dataView)
		{
			this.dataView = dataView;
		}

		public void run()
		{
			while (dataView.hasCompile() && keepRunning)
			{
				try
				{
					dataView.setCompile(false);
					ProgressMonitor progressMonitor = new ProgressMonitor(dataView, "Test", "Test", 0, 10);
					progressMonitor.setProgress(0);
					progressMonitor.setMillisToDecideToPopup(0);
					SwingUtilities.invokeAndWait(new UpdateGUI(dataView, true));
					Compiler compiler =
						new Compiler(
							dataView.getDataModel().getData(),
							dataView.structurePane.getCompilerOutput(),
							DataWorkshop.getInstance().getCompilerOptions(),
							DataWorkshop.getInstance().getIntegerFormatFactory());
					DataFrame frame = compiler.compile(dataView.getStructure(), dataView.getBitOffset());
					if (frame == null)
					{
						frame = new DataFrame();
						ErrorDialogFactory.show("Fatal Compilation Error", dataView);
					}
					SwingUtilities.invokeAndWait(new DataFrameSetter(frame));
					SwingUtilities.invokeAndWait(new UpdateGUI(dataView, false));
					progressMonitor.close();
				}
				catch (InterruptedException e)
				{
					Logger.getLogger(this.getClass()).severe(e);
				}
				catch (InvocationTargetException e)
				{
					Logger.getLogger(this.getClass()).severe(e);
				}
			}
		}
	}

	public class DataFrameSetter extends Thread
	{

		DataFrame frame;

		public DataFrameSetter(DataFrame frame)
		{
			this.frame = frame;
		}

		public void run()
		{
			staticDataView.setDataFrame(frame);
		}
	}

	public class UpdateGUI extends Thread
	{

		DefaultDynamicDataView dataView;
		boolean compile;

		public UpdateGUI(DefaultDynamicDataView view, boolean compile)
		{
			this.dataView = view;
			this.compile = compile;
		}

		public void run()
		{
			if (compile)
			{
				CompilerOutputPane output = dataView.structurePane.getCompilerOutput();
				output.clear();
			}
			dataView.setEnabled(!compile);
			mainPane.revalidate();
		}
	}
}
