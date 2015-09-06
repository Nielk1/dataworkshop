package dataWorkshop.data.structure;

import java.util.ArrayList;

import org.w3c.dom.Element;

import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.structure.atomic.DelimiterDefinition;
import dataWorkshop.data.structure.atomic.SimpleModificationDefinition;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.number.UnsignedOffset;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

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
public class UntilDelimiterRepeatStatement extends StatementStructure implements XMLSerializeable
{
	public final static String CLASS_NAME = "UntilDelimiterRepeatStatement";
	final static String NAME = "Repeat Until Delimiter Statement";

	private DelimiterDefinition delimiterDefinition;
	private SimpleModificationDefinition modificationDefinition;

	/******************************************************************************
	 *	Constructors
	 */
	public UntilDelimiterRepeatStatement()
	{
		this(DEFAULT_LABEL, new DelimiterDefinition(), new SimpleModificationDefinition());
	}

	public UntilDelimiterRepeatStatement(String label, DelimiterDefinition delimiterDefinition, SimpleModificationDefinition modificationDefinition)
	{
		super(label);
		this.delimiterDefinition = delimiterDefinition;
		this.modificationDefinition = modificationDefinition;
	}

	/******************************************************************************
	 *	XML Serializeable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.serialize(context, getDelimiterDefinition());
		XMLSerializeFactory.serialize(context, getSimpleModificationDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setDelimiterDefinition((DelimiterDefinition) XMLSerializeFactory.deserializeFirst(context));
		setSimpleModificationDefinition((SimpleModificationDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String getShortDescription() {
			return getName();	
		}
		
	public String getName()
	{
		return NAME;
	}

	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & delimiterDefinition.validate(validator, this);
		isValid = isValid & modificationDefinition.validate(validator, this);
		return isValid;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		long offset = getDelimiterDefinition().searchDelimiterOffset(compiler, this, bitOffset);
		long bitSize = offset - bitOffset;
		compiler.getCompilerOutput().info(this, bitOffset, "Raw Bitsize " + new UnsignedOffset(bitSize));

		bitSize = getSimpleModificationDefinition().calculate(bitSize);
		bitSize = Math.max(0, bitSize);
		compiler.getCompilerOutput().info(this, bitOffset, "Modified Bitsize " + new UnsignedOffset(bitSize));

		ArrayList renderedNodes = new ArrayList();

		DataFrame frame = null;
		long bitEnd = bitOffset + bitSize;
		while (bitOffset <= bitEnd)
		{
			frame = compiler.compileChildren(this, bitOffset, parent);
			renderedNodes.add(frame);
			bitOffset += frame.getBitSize();
			if (frame.getBitSize() == 0)
			{
				compiler.getCompilerOutput().info(this, bitOffset, "Stopped repeating to prevent infinite loop, no data was cosumed in last repeat");
				break;
			}
		}
		//Remove last frame
		if (renderedNodes.size() > 0)
		{
			renderedNodes.remove(frame);
			parent.remove(frame);
			parent.setBitSize(parent.getBitSize() - frame.getBitSize());
		}

		DataFrame[] frames = (DataFrame[]) renderedNodes.toArray(new DataFrame[0]);
		compiler.setArrayIndex(frames);
		return frames;
	}

	public SimpleModificationDefinition getSimpleModificationDefinition()
	{
		return modificationDefinition;
	}

	public void setSimpleModificationDefinition(SimpleModificationDefinition modificationDefinition)
	{
		this.modificationDefinition = modificationDefinition;
	}

	public DelimiterDefinition getDelimiterDefinition()
	{
		return delimiterDefinition;
	}

	public void setDelimiterDefinition(DelimiterDefinition delimiterDefinition)
	{
		this.delimiterDefinition = delimiterDefinition;
	}
}