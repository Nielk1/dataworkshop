package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.InfoView;
import dataWorkshop.gui.data.view.SingleLineView;
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
public class EncodingConverterDialog extends DialogWindow implements LocaleStrings
{

	public final static String CLASS_NAME = "EncodingConverterDialog";
	final static String ENCODINGS_TAG = "Encodings";

	DataModel dataModel;

	JButton[] buttons = { closeButton };
	static DataEncoding[] DEFAULT_ENCODINGS =
		{
			DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_16_BIG_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_32_BIG_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.DEC_8_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.DEC_16_BIG_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.DEC_32_BIG_UNSIGNED),
			DataEncodingFactory.getInstance().get(DataEncodingFactory.US_ASCII),
			};

	SingleLineView[] singleLineViews;
	private static EncodingConverterDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public EncodingConverterDialog()
	{
		super(ENCODING_CONVERTER_DIALOG_TITLE, false);
	}

	/******************************************************************************
	*	XMLSerializable Interface
	*/
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.serialize(context, ENCODINGS_TAG, getDataConverters());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setDataConverters((DataEncoding[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, ENCODINGS_TAG)).toArray(new DataEncoding[0]));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static void rebuild()
	{
		if (instance != null)
		{
			String xml = instance.serialize();
			DataModel dataModel = instance.getDataModel();
			instance = new EncodingConverterDialog();
			instance.deserialize(xml);
			instance.setDataModel(dataModel);
		}
	}

	public static EncodingConverterDialog getInstance()
	{
		if (instance == null)
		{
			instance = new EncodingConverterDialog();
			instance.buildDialog();
		}
		return instance;
	}

	public void setDataConverters(DataEncoding[] converters)
	{
		for (int i = 0; i < converters.length; i++)
		{
			singleLineViews[i].setDataConverter(converters[i]);
		}
	}

	public DataEncoding[] getDataConverters()
	{
		DataEncoding[] converters = new DataEncoding[singleLineViews.length];
		for (int i = 0; i < converters.length; i++)
		{
			converters[i] = singleLineViews[i].getDataConverter();
		}
		return converters;
	}

	public DataModel getDataModel()
	{
		return dataModel;
	}

	public void setDataModel(DataModel dataModel)
	{
		this.dataModel = dataModel;
		for (int i = 0; i < singleLineViews.length; i++)
		{
			singleLineViews[i].setDataModel(dataModel);
		}
	}

	/******************************************************************************
	*	Private Methods
	*/
	private void buildDialog()
	{
		dataModel = new DataModel();
		InfoView infoView = new InfoView(dataModel);
		JPanel converterPane = new JPanel();
		converterPane.setLayout(new BoxLayout(converterPane, BoxLayout.Y_AXIS));
		singleLineViews = new SingleLineView[DEFAULT_ENCODINGS.length];
		for (int i = 0; i < DEFAULT_ENCODINGS.length; i++)
		{
			singleLineViews[i] = new SingleLineView(DEFAULT_ENCODINGS[i], dataModel, false);
			singleLineViews[i].setFollowDataModelSelectionChange(false);
			converterPane.add(singleLineViews[i]);
		}
		
		converterPane.add(Box.createRigidArea(new Dimension(0, 6)));
		converterPane.add(Box.createVerticalGlue());
		converterPane.add(infoView);

		JPanel main = getMainPane();
		main.setLayout(new BorderLayout());
		main.add(converterPane, BorderLayout.CENTER);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[0]);

		pack();
	}
}
