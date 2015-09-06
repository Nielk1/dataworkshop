package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.DataRecognizer;
import dataWorkshop.data.DataSignatureTemplate;
import dataWorkshop.data.DefaultTemplate;
import dataWorkshop.data.FilenameSuffixTemplate;

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
public class AboutDialog extends DialogWindow implements LocaleStrings
{

	public final static String CLASS_NAME = "AboutDialog";

	final JButton[] buttons = { closeButton };

	private static AboutDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public AboutDialog()
	{
		super(ABOUT_DIALOG_TITLE, false);
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
		if (instance != null)
		{
			boolean visible = instance.isVisible();
			instance.setVisible(false);
			String xml = instance.serialize();
			instance = new AboutDialog();
			instance.buildDialog();
			instance.deserialize(xml);
			instance.setVisible(visible);
		}
	}

	public static AboutDialog getInstance()
	{
		if (instance == null)
		{
			instance = new AboutDialog();
			instance.buildDialog();
		}
		return instance;
	}

	/******************************************************************************
	*	Private Methods
	*/
	private void buildDialog()
	{
		JTextArea aboutPane = new JTextArea(ABOUT_STRING);
		aboutPane.setEditable(false);

		JTextArea creditsPane = new JTextArea(CREDITS_STRING);
		creditsPane.setEditable(false);

		JTextArea propertiesPane = new JTextArea();
		propertiesPane.setEditable(false);
		Object[] keys = System.getProperties().keySet().toArray();
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++)
		{
			propertiesPane.append(keys[i] + "=" + System.getProperty((String) keys[i]) + "\n");
		}

		DataRecognizer recognizer = DataWorkshop.getInstance().getDataRecognizer();

		DataSignatureTemplate[] dataSignatures = recognizer.getDataSignatures();
		String s = new String();
		String[] structures;

		s += "DATA SIGNATURES\n";
		for (int i = 0; i < dataSignatures.length; i++)
		{
			s += dataSignatures[i].getName()  + " ";
			structures = dataSignatures[i].getStructures();
			s += "[ ";
			for (int ii = 0; ii < structures.length; ii++)
			{
				s += structures[ii];
				if (ii < structures.length - 1)
				{
					s += ", ";
				}
			}
			s += "] " + dataSignatures[i].getSignature();
			s += "\n";
		}

		s += "\nFILENAME SIGNATURES\n";

		FilenameSuffixTemplate[] filenameSignatures = recognizer.getFilenameSignatures();
		for (int i = 0; i < filenameSignatures.length; i++)
		{
			s += filenameSignatures[i].getName()  + " ";
			structures = filenameSignatures[i].getStructures();
			s += "[ ";
			for (int ii = 0; ii < structures.length; ii++)
			{
				s += structures[ii];
				if (ii < structures.length - 1)
				{
					s += ", ";
				}
			}
			s += "] " + filenameSignatures[i].getSuffix();
			s += "\n";
		}

		s += "\nDEFAULT SIGNATURES\n";

		DefaultTemplate[] defaultSignatures = recognizer.getDefaultSignatures();
		for (int i = 0; i < defaultSignatures.length; i++)
		{
			s += defaultSignatures[i].getName()  + ": ";
			structures = defaultSignatures[i].getStructures();
			s += "[ ";
			for (int ii = 0; ii < structures.length; ii++)
			{
				s += structures[ii];
				if (ii < structures.length - 1)
				{
					s += ", ";
				}
			}
			s += "]";
			s += "\n";
		}

		JTextArea dataRecognizer = new JTextArea(s);
		dataRecognizer.setEditable(false);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(new JScrollPane(aboutPane), "About");
		tabbedPane.add(new JScrollPane(creditsPane), "Credits");
		tabbedPane.add(new JScrollPane(propertiesPane), "Properties");
		tabbedPane.add(new JScrollPane(dataRecognizer), "Data Recognizer");

		JPanel main = getMainPane();
		main.setLayout(new BorderLayout());
		main.add(tabbedPane, BorderLayout.CENTER);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[0]);

		pack();
	}
}
