package dataWorkshop.gui.data.structure.atomic;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.ComboPane;
import dataWorkshop.number.IntegerFormatFactory;
import dataWorkshop.data.structure.DataFieldDefinition;
import dataWorkshop.data.structure.atomic.LengthDefinition;
import dataWorkshop.data.structure.atomic.PointerLengthDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;

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
public class BitSizeLengthPane extends JPanel implements ActionListener, LocaleStrings
{
	final static String STATIC_TYPE = "Static";
	final static String POINTER_TYPE = "Pointer";

	ComboPane typeBox;

	JPanel lengthPane;
	StaticLengthPane staticLengthPane;
	PointerLengthPane pointerLengthPane;

	DataFieldDefinition[] possibleFields = new DataFieldDefinition[0];

	/******************************************************************************
	 *	Constructors
	 */
	public BitSizeLengthPane(String label)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		IntegerFormatFactory integerFormat = DataWorkshop.getInstance().getIntegerFormatFactory();
		staticLengthPane = new StaticLengthPane(integerFormat.getUnsignedOffset(), label);
		pointerLengthPane = new PointerLengthPane(integerFormat.getSignedOffset(), label);

		typeBox = new ComboPane(new String[] {STATIC_TYPE, POINTER_TYPE});
		typeBox.setSelectedItem(STATIC_TYPE);
		typeBox.setMaximumSize(new Dimension(10000, (int) typeBox.getMinimumSize().getHeight()));
		//typeBox.setMaximumSize(typeBox.getMinimumSize());
		typeBox.addActionListener(this);

		lengthPane = staticLengthPane;

		add(typeBox);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(lengthPane);
	}

	/******************************************************************************
	 *	ActionListener
	 */
	public void actionPerformed(ActionEvent e)
	{
		remove(lengthPane);
		String type = (String) typeBox.getSelectedItem();
		if (type == STATIC_TYPE)
		{
			lengthPane = staticLengthPane;
		}
		else if (type == POINTER_TYPE)
		{
			lengthPane = pointerLengthPane;
			pointerLengthPane.setPointerLength(new PointerLengthDefinition(), possibleFields);
		}
		add(lengthPane);
		revalidate();
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public LengthDefinition getLength()
	{
		String type = (String) typeBox.getSelectedItem();
		if (type == STATIC_TYPE)
		{
			return staticLengthPane.getStaticLength();
		}
		else if (type == POINTER_TYPE)
		{
			return pointerLengthPane.getPointerLength();
		}
		else
		{
			throw new RuntimeException("There is no case defined for " + type);
		}
	}

	public void setLength(LengthDefinition length, DataFieldDefinition[] possibleFields)
	{
		this.possibleFields = possibleFields;
		if (length instanceof StaticLengthDefinition)
		{
			typeBox.setSelectedItem(STATIC_TYPE);
			staticLengthPane.setStaticLength((StaticLengthDefinition) length);
		}
		else if (length instanceof PointerLengthDefinition)
		{
			typeBox.setSelectedItem(POINTER_TYPE);
			pointerLengthPane.setPointerLength((PointerLengthDefinition) length, possibleFields);
		}
	}
}