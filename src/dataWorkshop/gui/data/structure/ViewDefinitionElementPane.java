package dataWorkshop.gui.data.structure;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.ViewDefinitionElement;

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
public abstract class ViewDefinitionElementPane extends JPanel implements LocaleStrings
{
	JTextField nameField;
	JPanel mainPane;

	/******************************************************************************
	 *	Constructors
	 */
	public ViewDefinitionElementPane(ViewDefinitionElement node)
	{
		super();
		setLayout(new BorderLayout());
		mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

		JPanel labelPane = new JPanel();
		labelPane.setLayout(new BoxLayout(labelPane, BoxLayout.X_AXIS));
		labelPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		nameField = new JTextField(node.getLabel());
		labelPane.add(new JLabel(NAME));
		labelPane.add(Box.createRigidArea(new Dimension(6, 0)));
		labelPane.add(nameField);

		add(labelPane, BorderLayout.NORTH);
		add(mainPane, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public JPanel getMainPane()
	{
		return mainPane;
	}

	public void setDefinitionNode(ViewDefinitionElement node)
	{
		nameField.setText(node.getLabel());
	}

	/**
	 * :KLUDGE:Martin Pape:May 4, 2003
	 * Crap Design. 
	 * - No typesafety, the caller must make sure to use the correct DefinitionNode subclass when calling this method.
	 */
	public void getDefinitionNode(ViewDefinitionElement node)
	{
		node.setLabel(nameField.getText());
	}
}
