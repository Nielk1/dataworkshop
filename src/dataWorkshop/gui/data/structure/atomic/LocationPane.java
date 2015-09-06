package dataWorkshop.gui.data.structure.atomic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.atomic.LocationDefinition;
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
public class LocationPane extends JPanel implements LocaleStrings
{
	ComboPane locationFrameBox;

	/******************************************************************************
	 *	Constructors
	 */
	public LocationPane()
	{
		this(null);
	}

	public LocationPane(String label)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		locationFrameBox = new ComboPane(new Object[0], label);
		add(locationFrameBox);
		add(Box.createHorizontalGlue());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setLocationDefinition(LocationDefinition location, ViewDefinitionElement[] possibleLocations)
	{
		locationFrameBox.setItems(convert(possibleLocations));
		locationFrameBox.setSelectedItem(location.getPath());
		locationFrameBox.setMaximumSize(locationFrameBox.getMinimumSize());
	}

	public LocationDefinition getLocationDefinition()
	{
		LocationDefinition location = new LocationDefinition();
		location.setPath((String) locationFrameBox.getSelectedItem());
		return location;
	}

	/******************************************************************************
	*	Private Methods
	*/
	public static String[] convert(ViewDefinitionElement[] nodes)
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