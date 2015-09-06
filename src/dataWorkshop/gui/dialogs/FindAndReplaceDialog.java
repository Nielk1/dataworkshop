package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.transformer.FindAndReplace;
import dataWorkshop.gui.NumberPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.SingleUnitView;
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
public class FindAndReplaceDialog extends DialogWindow implements LocaleStrings
{

	public final static String CLASS_NAME = "FindAndReplaceDialog";
	public final static String GRANULARITY_TAG = "granularity";
	public final static long DEFAULT_GRANULARITY = 8;

	NumberPane granularity;
	SingleUnitView findPane;
	SingleUnitView replacePane;

	JButton[] buttons = { okButton, cancelButton };
	private static FindAndReplaceDialog instance;
	/******************************************************************************
	 *	Constructors
	 */
	public FindAndReplaceDialog()
	{
		super(DEFINE_FIND_AND_REPLACE_DIALOG_TITLE, true);
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
		XMLSerializeFactory.serialize(context, getFindAndReplace());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setFindAndReplace((FindAndReplace) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public long getGranularity()
	{
		return granularity.getValue();
	}

	public void setGranularity(long gran)
	{
		granularity.setValue(gran);
	}

	public static void rebuild()
	{
		if (instance != null)
		{
			boolean visible = instance.isVisible();
			instance.setVisible(false);
			String xml = instance.serialize();
			instance = new FindAndReplaceDialog();
			instance.buildDialog();
			instance.deserialize(xml);
			instance.setVisible(visible);
		}
	}

	public static FindAndReplaceDialog getInstance()
	{
		if (instance == null)
		{
			instance = new FindAndReplaceDialog();
			instance.buildDialog();
		}
		return instance;
	}

	public FindAndReplace getFindAndReplace()
	{
		return new FindAndReplace(findPane.getDataModel().getData(), replacePane.getDataModel().getData(), findPane.getDataConverter(), replacePane.getDataConverter(), (int) granularity.getValue());
	}

	public void setFindAndReplace(FindAndReplace options)
	{
		findPane.setDataModel(new DataModel(options.getFindData()));
		findPane.setDataConverter(options.getFindDataEncoding());
		replacePane.setDataModel(new DataModel(options.getReplaceData()));
		replacePane.setDataConverter(options.getReplaceDataEncoding());
		granularity.setValue(options.getStepSize());
	}

	public boolean show(Frame owner)
	{
		setLocationRelativeTo(owner);
		FindAndReplace oldOptions = getFindAndReplace();

		this.setVisible(true);
		if (wasButtonSelected(okButton))
		{
			return true;
		}
		else
		{
			setFindAndReplace(oldOptions);
			return false;
		}
	}

	/******************************************************************************
	*	Private Methods
	*/
	private void buildDialog()
	{
		DataWorkshop options = DataWorkshop.getInstance();

		granularity = new NumberPane(options.getUnsignedOffsetNumber(), "Search Granularity");
		granularity.setValue(DEFAULT_GRANULARITY);
		granularity.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

		//Create Find+Replace Pane
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		findPane = new SingleUnitView(DATA_ENCODING);
		JPanel fPane = new JPanel(new BorderLayout());
		fPane.setBorder(BorderFactory.createTitledBorder("Search For"));
		fPane.add(granularity, BorderLayout.NORTH);
		fPane.add(findPane, BorderLayout.CENTER);
		main.add(fPane);
		main.add(Box.createRigidArea(new Dimension(0, 6)));

		replacePane = new SingleUnitView(DATA_ENCODING);
		JPanel rPane = new JPanel(new BorderLayout());
		rPane.setBorder(BorderFactory.createTitledBorder("Replace With"));
		rPane.add(replacePane, BorderLayout.CENTER);
		main.add(rPane);

		//Build ContentPane
		JPanel pane = getMainPane();
		pane.setLayout(new BorderLayout());
		pane.add(main, BorderLayout.CENTER);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[1]);

		pack();
	}
}
