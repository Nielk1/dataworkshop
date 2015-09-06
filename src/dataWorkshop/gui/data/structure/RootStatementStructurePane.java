package dataWorkshop.gui.data.structure;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import dataWorkshop.data.structure.RootStatement;
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
public class RootStatementStructurePane extends ViewDefinitionElementPane
{

	JTextField fileField;
	JTextField authorField;
	JTextArea descriptionPane;

	/******************************************************************************
	 *	Constructors
	 */
	public RootStatementStructurePane(RootStatement statement)
	{
		super(statement);
		authorField = new JTextField();
		descriptionPane = new JTextArea();
		descriptionPane.setWrapStyleWord(true);
		fileField = new JTextField();
		fileField.setEditable(false);

		JPanel authorPane = new JPanel();
		authorPane.setLayout(new BoxLayout(authorPane, BoxLayout.X_AXIS));
		authorPane.add(new JLabel("Author"));
		authorPane.add(Box.createRigidArea(new Dimension(6, 0)));
		authorPane.add(authorField);
	
		JPanel filePane = new JPanel();
		filePane.setLayout(new BoxLayout(filePane, BoxLayout.X_AXIS));
		filePane.add(new JLabel("Filename"));
		filePane.add(Box.createRigidArea(new Dimension(6, 0)));
		filePane.add(fileField);

		JPanel northPane = new JPanel();
		northPane.setLayout(new BoxLayout(northPane, BoxLayout.Y_AXIS));
		northPane.add(Box.createRigidArea(new Dimension(0, 6)));
		northPane.add(filePane);
		northPane.add(Box.createRigidArea(new Dimension(0, 6)));
		northPane.add(authorPane);
		northPane.add(Box.createRigidArea(new Dimension(0, 6)));
		northPane.add(new JLabel("Description"));

		JPanel dPane = new JPanel();
		dPane.setLayout(new BorderLayout());
		dPane.add(northPane, BorderLayout.NORTH);
		dPane.add(new JScrollPane(descriptionPane), BorderLayout.CENTER);
		
		JPanel main = getMainPane();
		main.add(dPane);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void getDefinitionNode(ViewDefinitionElement n)
	{
		super.getDefinitionNode(n);
		RootStatement node = (RootStatement) n;
		node.setAuthor(authorField.getText());
		node.setDescription(descriptionPane.getText());
	}

	public void setDefinitionNode(ViewDefinitionElement n)
	{
		super.setDefinitionNode(n);
		RootStatement node = (RootStatement) n;
		authorField.setText(node.getAuthor());
		descriptionPane.setText(node.getDescription());
		File file = node.getFile();
		if (file != null) {
			fileField.setText(file.getAbsolutePath());
		}
		else {
			fileField.setText("");
		}
	}
}
