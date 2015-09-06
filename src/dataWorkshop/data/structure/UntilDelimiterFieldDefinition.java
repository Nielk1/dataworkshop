package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.structure.atomic.DelimiterDefinition;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
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
public class UntilDelimiterFieldDefinition extends AbstractDataEncodingFieldDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "UntilDelimiterFieldDefinition";
	final static String NAME = "Field Until Delimiter";

	private DelimiterDefinition delimiterDefinition;
	private SimpleModificationDefinition modificationDefinition;

	/******************************************************************************
	 *	Constructors
	 */
	public UntilDelimiterFieldDefinition()
	{
		this(DEFAULT_LABEL, new EncodingDefinition(), new DelimiterDefinition());
	}

	public UntilDelimiterFieldDefinition(String label, EncodingDefinition converterFieldDefinition, DelimiterDefinition delimiterDefinition)
	{
		this(label, converterFieldDefinition, delimiterDefinition, new SimpleModificationDefinition());
	}

	public UntilDelimiterFieldDefinition(String label, EncodingDefinition converterFieldDefinition, DelimiterDefinition delimiterDefinition, SimpleModificationDefinition modificationDefinition)
	{
		super(label, converterFieldDefinition);
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
		
	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & delimiterDefinition.validate(validator, this);
		isValid = isValid & modificationDefinition.validate(validator, this);
		return isValid;
	}

	public String getName()
	{
		return NAME;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		long offset = getDelimiterDefinition().searchDelimiterOffset(compiler, this, bitOffset);
		long bitSize = offset - bitOffset;
		compiler.getCompilerOutput().info(this, bitOffset, "Bitsize before Modification " + new UnsignedOffset(bitSize));

		bitSize = getSimpleModificationDefinition().calculate(bitSize);
		bitSize = Math.max(0, bitSize);

		DataFrame[] frames = new DataFrame[1];
		frames[0] = render();
		frames[0].setBitOffset(bitOffset);
		frames[0].setBitSize(bitSize);

		compiler.getCompilerOutput().info(this, bitOffset, "BitSize " + new UnsignedOffset(bitSize));

		parent.add(frames[0]);

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