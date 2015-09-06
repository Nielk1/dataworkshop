package dataWorkshop.gui.data.structure.atomic;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
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
public class ConverterFieldPane extends JPanel implements LocaleStrings, ChangeListener
{
	JCheckBox displayDataBox;
	DataEncodingPane dataUnitBox;

	/******************************************************************************
	 *	Constructors
	 */
	public ConverterFieldPane()
	{
		super();
		displayDataBox = new JCheckBox("Visible");
		displayDataBox.setHorizontalTextPosition(SwingConstants.LEADING);
		displayDataBox.addChangeListener(this);
		dataUnitBox = new DataEncodingPane(DATA_ENCODING);

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p1.add(displayDataBox);
		p1.add(Box.createHorizontalGlue());

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(dataUnitBox);
		p2.add(Box.createHorizontalGlue());

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(p1);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(p2);
	}

	/******************************************************************************
	*	ChangeListener
	*/
	public void stateChanged(ChangeEvent e)
	{
		dataUnitBox.setEnabled(displayDataBox.isSelected());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setConverterFieldDefinition(EncodingDefinition converterField)
	{
		displayDataBox.setSelected(converterField.isVisible());
		dataUnitBox.setDataEncoding(converterField.getEncoding());
	}

	public EncodingDefinition getConverterFieldDefinition()
	{
		return new EncodingDefinition(dataUnitBox.getDataEncoding(), displayDataBox.isSelected());
	}
}