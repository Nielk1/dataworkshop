package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.NumberPane;

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
public class DataViewOptionPane extends JPanel implements LocaleStrings
{
	JCheckBox renderOffsetBox;
	JCheckBox renderSizeBox;
	NumberPane bitsPerLine;
	NumberPane linesPerPage;

	/******************************************************************************
	 *	Constructors
	 */
	public DataViewOptionPane()
	{
		DataWorkshop eo = DataWorkshop.getInstance();

		JPanel generalOptions = new JPanel();
		generalOptions.setBorder(BorderFactory.createTitledBorder("Data Field Information"));
		renderOffsetBox = new JCheckBox(RENDER_OFFSET);
		renderSizeBox = new JCheckBox(RENDER_SIZE);
		JPanel pane1 = new JPanel();
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
		pane1.add(renderOffsetBox);
		pane1.add(renderSizeBox);
		generalOptions.add(pane1, BorderLayout.CENTER);

		JPanel textFieldLayout = new JPanel();
		textFieldLayout.setBorder(BorderFactory.createTitledBorder("Data Encoding Field Layout"));
		bitsPerLine = new NumberPane(eo.getUnsignedOffsetNumber(), BITS_PER_LINE);
		linesPerPage = new NumberPane(eo.getUnsignedCount(), LINES_PER_PAGE);
		JPanel pane2 = new JPanel();
		pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
		pane2.add(bitsPerLine);
		pane2.add(Box.createRigidArea(new Dimension(0, 6)));
		pane2.add(linesPerPage);
		textFieldLayout.add(pane2, BorderLayout.CENTER);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(generalOptions);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(textFieldLayout);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void setDataViewOption(DataViewOption options)
	{
		renderOffsetBox.setSelected(options.isRenderOffset());
		 renderSizeBox.setSelected(options.isRenderSize());
		 bitsPerLine.setValue(options.getBitsPerLine());
		 linesPerPage.setValue(options.getLinesPerPage());
	}

	public DataViewOption getDataViewOption()
	{
		DataViewOption options = new DataViewOption();
		options.setRenderOffset(renderOffsetBox.isSelected());
		options.setRenderSize(renderSizeBox.isSelected());
		options.setBitsPerLine((int) bitsPerLine.getValue());
		options.setLinesPerPage((int) linesPerPage.getValue());
		return options;
	}
}
