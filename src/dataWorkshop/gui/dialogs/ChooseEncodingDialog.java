package dataWorkshop.gui.dialogs;

import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.gui.data.encoding.DataEncodingPane;

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
public class ChooseEncodingDialog extends DialogWindow implements LocaleStrings
{

	public final static String CLASS_NAME = "ChooseEncodingDialog";
	JButton[] buttons = { okButton, cancelButton };

	DataEncodingPane dataConverterBox;
	private static ChooseEncodingDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public ChooseEncodingDialog()
	{
		super(CHOOSE_ENCODING_DIALOG_TITLE, true);
	}

	/******************************************************************************
	*	XMLSerializable Interface
	*/
	public String getClassName()
	{
		return CLASS_NAME;
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static void rebuild()
	{
		instance = null;
	}

	public static ChooseEncodingDialog getInstance()
	{
		if (instance == null)
		{
			instance = new ChooseEncodingDialog();
			instance.buildDialog();
		}
		return instance;
	}

	public DataEncoding show(Frame owner)
	{
		return show(owner, dataConverterBox.getDataEncoding());
	}

	public DataEncoding show(Frame owner, DataEncoding converter)
	{
		setLocationRelativeTo(owner);
		dataConverterBox.setDataEncoding(converter);
		pack();

		this.setVisible(true);
		if (wasButtonSelected(okButton))
		{
			return dataConverterBox.getDataEncoding();
		}
		else
		{
			return null;
		}
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void buildDialog()
	{

		dataConverterBox = new DataEncodingPane();

		JPanel pane = getMainPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.add(dataConverterBox);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[1]);
	}
}
