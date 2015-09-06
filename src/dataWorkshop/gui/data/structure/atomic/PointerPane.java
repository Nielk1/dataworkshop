package dataWorkshop.gui.data.structure.atomic;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.DataFieldDefinition;
import dataWorkshop.data.structure.atomic.PointerDefinition;
import dataWorkshop.gui.ComboPane;

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
public class PointerPane extends JPanel implements LocaleStrings
{

	ComboPane pointerFrameBox;
	JCheckBox signedBox;
	JCheckBox littleEndianBox;

	/******************************************************************************
	 *	Constructors
	 */
	public PointerPane()
	{
		this(null);
	}

	public PointerPane(String label)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		pointerFrameBox = new ComboPane(new Object[0], label);
		pointerFrameBox.setMaximumSize(pointerFrameBox.getMinimumSize());
		signedBox = new JCheckBox("Signed");
		littleEndianBox = new JCheckBox("LittleEndian");

		add(pointerFrameBox);
		add(Box.createRigidArea(new Dimension(6, 0)));
		add(signedBox);
		add(Box.createRigidArea(new Dimension(6, 0)));
		add(littleEndianBox);
		add(Box.createHorizontalGlue());
		//setAlignmentX(Component.RIGHT_ALIGNMENT);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setPointerDefinition(PointerDefinition pointer, DataFieldDefinition[] possibleFields)
	{
		signedBox.setSelected(pointer.isSigned());
		littleEndianBox.setSelected(pointer.isSigned());
		pointerFrameBox.setItems(convert(possibleFields));
		pointerFrameBox.setSelectedItem(pointer.getPath());
		pointerFrameBox.setMaximumSize(pointerFrameBox.getMinimumSize());
	}

	public PointerDefinition getPointerDefinition()
	{
		PointerDefinition pointer = new PointerDefinition();
		pointer.setSigned(signedBox.isSelected());
		pointer.setLittleEndian(littleEndianBox.isSelected());
		pointer.setPath((String) pointerFrameBox.getSelectedItem());
		return pointer;
	}
	
	/******************************************************************************
	*	Private Methods
	*/
	public static String[] convert(DataFieldDefinition[] nodes)
	{
		String[] names = new String[nodes.length];
		for (int i = 0; i < nodes.length; i++)
		{
			names[i] = new String();
			if (nodes[i] != null)
			{
				names[i] = nodes[i].getXPath();
			}
		}
		return names;
	}
}