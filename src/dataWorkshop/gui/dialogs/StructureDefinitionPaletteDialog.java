package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.StringToDataMapping;
import dataWorkshop.data.structure.MapFieldDefinition;
import dataWorkshop.data.structure.RepeatStatement;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.DataEncodingFieldDefinition;
import dataWorkshop.data.structure.UntilDelimiterFieldDefinition;
import dataWorkshop.data.structure.atomic.DelimiterDefinition;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
import dataWorkshop.data.structure.atomic.ModificationDefinition;
import dataWorkshop.data.structure.atomic.PointerDefinition;
import dataWorkshop.data.structure.atomic.PointerLengthDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.data.structure.atomic.StringToDataMappingDefinition;
import dataWorkshop.gui.data.structure.ViewDefinitionPane;
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
public class StructureDefinitionPaletteDialog extends DialogWindow implements LocaleStrings
{
	public final static String CLASS_NAME = "StructurePaletteDialog";
	final static String STRUCTURE_PALETTE_TAG = "StructurePalette";

	final JButton[] buttons = { closeButton };

	protected ViewDefinitionPane structurePane;
	private static StructureDefinitionPaletteDialog instance;

	/******************************************************************************
	 *	Constructors
	 */
	public StructureDefinitionPaletteDialog()
	{
		super(STRUCTURE_PALETTE_DIALOG_TITLE, false);
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
		XMLSerializeFactory.serialize(context, structurePane.getStructure());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		structurePane.setStructure((RootStatement) XMLSerializeFactory.deserializeFirst(context));
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
			instance = new StructureDefinitionPaletteDialog();
			instance.buildDialog();
			instance.deserialize(xml);
			instance.setVisible(visible);
		}
	}

	public static StructureDefinitionPaletteDialog getInstance()
	{
		if (instance == null)
		{
			instance = new StructureDefinitionPaletteDialog();
			instance.buildDialog();
		}
		return instance;
	}

	/******************************************************************************
	*	Private Methods
	*/
	private void buildDialog()
	{
		DataEncodingFactory encodingFactory = DataEncodingFactory.getInstance();

		RootStatement root = new RootStatement("Structure-Palette", new String(), new String());
		RepeatStatement strings = new RepeatStatement("Strings");
		root.add(strings);
		strings.add(
			new UntilDelimiterFieldDefinition(
				"c-style-string",
				new EncodingDefinition(encodingFactory.get(DataEncodingFactory.US_ASCII)),
				new DelimiterDefinition(encodingFactory.get(DataEncodingFactory.HEX_8_UNSIGNED), Data.DATA_00000000, 8)));
		RepeatStatement pascalString = new RepeatStatement("pascal-style-string");
		strings.add(pascalString);
		DataEncodingFieldDefinition lengthField =
			new DataEncodingFieldDefinition("length", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_8_SIGNED)), new StaticLengthDefinition(8));
		pascalString.add(lengthField);
		pascalString.add(
			new DataEncodingFieldDefinition(
				"string",
				new EncodingDefinition(encodingFactory.get(DataEncodingFactory.US_ASCII)),
				new PointerLengthDefinition(new PointerDefinition(lengthField.getXPath()), new ModificationDefinition(8, 0))));

		RepeatStatement numbers = new RepeatStatement("Numbers");
		root.add(numbers);
		numbers.add(new DataEncodingFieldDefinition("1-byte-hex", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.HEX_8_UNSIGNED)), new StaticLengthDefinition(8)));
		numbers.add(new DataEncodingFieldDefinition("1-byte-decimal", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_8_UNSIGNED)), new StaticLengthDefinition(8)));
		RepeatStatement bigEndian = new RepeatStatement("bigEndian");
		numbers.add(bigEndian);
		bigEndian.add(new DataEncodingFieldDefinition("2-byte-hex", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.HEX_16_BIG_UNSIGNED)), new StaticLengthDefinition(16)));
		bigEndian.add(new DataEncodingFieldDefinition("4-byte-hex", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.HEX_32_BIG_UNSIGNED)), new StaticLengthDefinition(32)));
		bigEndian.add(new DataEncodingFieldDefinition("2-byte-decimal", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_16_BIG_UNSIGNED)), new StaticLengthDefinition(16)));
		bigEndian.add(new DataEncodingFieldDefinition("4-byte-decimal", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_32_BIG_UNSIGNED)), new StaticLengthDefinition(32)));
		RepeatStatement littleEndian = new RepeatStatement("littleEndian");
		numbers.add(littleEndian);
		littleEndian.add(new DataEncodingFieldDefinition("2-byte-hex", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.HEX_16_LITTLE_UNSIGNED)), new StaticLengthDefinition(16)));
		littleEndian.add(new DataEncodingFieldDefinition("4-byte-hex", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.HEX_32_LITTLE_UNSIGNED)), new StaticLengthDefinition(32)));
		littleEndian.add(new DataEncodingFieldDefinition("2-byte-decimal", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_16_LITTLE_UNSIGNED)), new StaticLengthDefinition(16)));
		littleEndian.add(new DataEncodingFieldDefinition("4-byte-decimal", new EncodingDefinition(encodingFactory.get(DataEncodingFactory.DEC_32_LITTLE_UNSIGNED)), new StaticLengthDefinition(32)));

		RepeatStatement flags = new RepeatStatement("Flags");
		root.add(flags);
		flags.add(
			new MapFieldDefinition(
				"bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		RepeatStatement flags1 = new RepeatStatement("8-bit-flagfield");
		flags.add(flags1);
		flags1.add(
			new MapFieldDefinition(
				"0-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"1-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"2-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"3-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"4-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"5-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"6-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags1.add(
			new MapFieldDefinition(
				"7-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));

		RepeatStatement flags2 = new RepeatStatement("16-bit-flagfield");
		flags.add(flags2);
		flags2.add(
			new MapFieldDefinition(
				"0-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"1-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"2-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"3-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"4-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"5-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"6-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"7-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"8-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"9-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"10-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"11-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"12-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"13-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"14-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));
		flags2.add(
			new MapFieldDefinition(
				"15-bit-flag",
				new StringToDataMappingDefinition(encodingFactory.get(DataEncodingFactory.BINARY), new StringToDataMapping(new Data[] { Data.DATA_0, Data.DATA_1 }, new String[] { "no", "yes" }))));

		structurePane = new ViewDefinitionPane(root);

		JPanel pane = getMainPane();
		pane.setLayout(new BorderLayout());
		pane.add(structurePane);

		setButtons(buttons);
		setDefaultButton(buttons[0]);
		setCancelButton(buttons[0]);

		pack();
	}
}
