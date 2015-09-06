package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.ComboPane;
import dataWorkshop.gui.DiffDataOptions;
import dataWorkshop.gui.NumberPane;
import dataWorkshop.gui.ProjectPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.encoding.DataEncodingPane;
import dataWorkshop.gui.data.view.InfoView;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.xml.XMLSerializeFactory;

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
public class DiffDataDialog extends DialogWindow implements ActionListener, DataModelListener, LocaleStrings
{

	public final static String CLASS_NAME = "DiffDataDialog";
	public final static String STEP_SIZE_TAG = "stepSize";

	ComboPane compareBox;
	InfoView selectedDataModelInfoPane;
	InfoView activeProjectInfoPane;
	JLabel activeDataModelName;
	NumberPane stepSizePane;
	DataEncodingPane unitBox;

	DataModel[] dataModels;

	DataModel selectedDataModel;
	DataModel activeDataModel;

	public JButton diffDataButton = new JButton(DIFF_DATA_BUTTON_NAME);

	final JButton[] buttons = { diffDataButton, cancelButton };
	private static DiffDataDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public DiffDataDialog()
	{
		super(DIFF_DATA_DIALOG_TITLE, true);
	}

	/******************************************************************************
	 *	ActionListener
	 */
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		Object source = e.getSource();
		if (source == compareBox)
		{
			selectedDataModel.removeDataModelListener(this);
			selectedDataModel = dataModels[compareBox.getSelectedIndex()];
			selectedDataModelInfoPane.setDataModel(selectedDataModel);
			selectedDataModel.addDataModelListener(this);
			if (selectedDataModel.getSelectionSize() == activeDataModel.getSelectionSize())
			{
				diffDataButton.setEnabled(true);
			}
			else
			{
				diffDataButton.setEnabled(false);
			}
		}
	}

	/******************************************************************************
	 *	DataModelListener Methods
	 */
	public void dataChanged(DataChangeEvent e)
	{
	}

	public void selectionChanged(DataSelectionEvent e)
	{
		if (selectedDataModel.getSelectionSize() == activeDataModel.getSelectionSize())
		{
			diffDataButton.setEnabled(true);
		}
		else
		{
			diffDataButton.setEnabled(false);
		}
	}

	/******************************************************************************
	*	XMLSerializable Interface
	*/
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(org.w3c.dom.Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.setAttribute(context, STEP_SIZE_TAG, getStepSize());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setStepSize(XMLSerializeFactory.getAttributeAsLong(context, STEP_SIZE_TAG));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public long getStepSize()
	{
		return stepSizePane.getValue();
	}

	public void setStepSize(long gran)
	{
		stepSizePane.setValue(gran);
	}

	public static void rebuild()
	{
		if (instance != null)
		{
			boolean visible = instance.isVisible();
			instance.setVisible(false);
			String xml = instance.serialize();
			instance = new DiffDataDialog();
			instance.buildDialog();
			instance.deserialize(xml);
			instance.setVisible(visible);
		}
	}

	public static DiffDataDialog getInstance()
	{
		if (instance == null)
		{
			instance = new DiffDataDialog();
			instance.buildDialog();
		}
		return instance;
	}

	/**
	 *	Show
	 *
	 *	return null if cancel was pressed
	 */
	public DiffDataOptions show(Frame owner, DataModel[] models, String[] names, ProjectPane projectPane)
	{
		setLocationRelativeTo(owner);
		this.activeDataModel = projectPane.getDataModel();
		activeDataModel.addDataModelListener(this);
		activeProjectInfoPane.setDataModel(activeDataModel);
		activeDataModelName.setText(projectPane.getShortName());

		dataModels = models;
		compareBox.setItems(names);

		//We have to repack because the sizes may have changed after initialization
		pack();
		this.setVisible(true);
		if (wasButtonSelected(diffDataButton))
		{
			activeDataModel.removeDataModelListener(this);
			return new DiffDataOptions(models[compareBox.getSelectedIndex()], names[compareBox.getSelectedIndex()], unitBox.getDataEncoding(), (int) stepSizePane.getValue());
		}
		else
		{
			activeDataModel.removeDataModelListener(this);
			return null;
		}
	}

	/******************************************************************************
	*	Private Methods
	*/
	private void buildDialog()
	{
		DataWorkshop options = DataWorkshop.getInstance();

		//Main Pane
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		compareBox = new ComboPane(new String[0]);
		compareBox.addActionListener(this);

		selectedDataModel = new DataModel();
		selectedDataModelInfoPane = new InfoView(selectedDataModel);
		selectedDataModelInfoPane.setFileSizeEditable(false);
		selectedDataModel.addDataModelListener(this);

		JPanel comparePane = new JPanel();
		comparePane.setLayout(new BoxLayout(comparePane, BoxLayout.Y_AXIS));
		comparePane.add(compareBox);
		comparePane.add(Box.createRigidArea(new Dimension(0, 6)));
		comparePane.add(selectedDataModelInfoPane);
		comparePane.setBorder(BorderFactory.createTitledBorder(""));

		activeProjectInfoPane = new InfoView(new DataModel());
		activeProjectInfoPane.setFileSizeEditable(false);

		activeDataModelName = new JLabel();

		JPanel activeDataModelPane = new JPanel();
		activeDataModelPane.setLayout(new BoxLayout(activeDataModelPane, BoxLayout.X_AXIS));
		activeDataModelPane.add(activeDataModelName);
		activeDataModelPane.add(Box.createHorizontalGlue());

		JPanel activeProjectPane = new JPanel();
		activeProjectPane.setLayout(new BoxLayout(activeProjectPane, BoxLayout.Y_AXIS));
		activeProjectPane.add(activeDataModelPane);
		activeProjectPane.add(Box.createRigidArea(new Dimension(0, 6)));
		activeProjectPane.add(activeProjectInfoPane);
		activeProjectPane.setBorder(BorderFactory.createTitledBorder(""));

		stepSizePane = new NumberPane(options.getUnsignedOffsetNumber(), "Diff Granularity");
		stepSizePane.setMinimum(1);
		stepSizePane.setValue(8);
		unitBox = new DataEncodingPane("Display result as");

		mainPane.add(stepSizePane);
		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		mainPane.add(unitBox);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(activeProjectPane);

		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		mainPane.add(new JLabel("against"));
		mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
		mainPane.add(comparePane);

		JPanel pane = getMainPane();
		pane.setLayout(new BorderLayout());
		pane.add(mainPane, BorderLayout.NORTH);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[1]);

		pack();
	}
}
