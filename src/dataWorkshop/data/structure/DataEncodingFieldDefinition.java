package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
import dataWorkshop.data.structure.atomic.LengthDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.UnsignedOffset;
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
public class DataEncodingFieldDefinition extends AbstractDataEncodingFieldDefinition
{
	public final static String CLASS_NAME = "DataEncodingFieldDefinition";
	final static String NAME = "Field";
	
	final static String BIT_SIZE_TAG = "bitSize";

	LengthDefinition length;

	/******************************************************************************
	 *	Constructors
	 */
	public DataEncodingFieldDefinition()
	{
		this(DEFAULT_LABEL);
	}

	public DataEncodingFieldDefinition(String label)
	{
		this(label, new EncodingDefinition());
	}

	public DataEncodingFieldDefinition(String label, EncodingDefinition converterFieldDefinition)
	{
		this(label, converterFieldDefinition, new StaticLengthDefinition(1));
	}

	public DataEncodingFieldDefinition(String label, EncodingDefinition converterFieldDefinition, LengthDefinition length)
	{
		super(label, converterFieldDefinition);
		this.length = length;
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
		XMLSerializeFactory.serialize(context, getLengthDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setLengthDefinition((LengthDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String getShortDescription() {
		LengthDefinition length = getLengthDefinition();
		if (length instanceof StaticLengthDefinition) {
			return (new UnsignedOffset(((StaticLengthDefinition) length).getLength())).toString(false) + "-bit " + getName();
		}
		return getName();
	}
		
	public String getName()
	{
		return NAME;
	}

	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & length.validate(validator, this);
		return isValid;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		long bitSize = getLengthDefinition().getLength(compiler, this, bitOffset, parent);

		DataFrame[] frames = new DataFrame[1];
		frames[0] = render();
		frames[0].setBitOffset(bitOffset);
		frames[0].setBitSize(bitSize);

		compiler.getCompilerOutput().info(this, bitOffset, "BitSize " + new UnsignedOffset(bitSize));

		parent.add(frames[0]);

		return frames;
	}

	public LengthDefinition getLengthDefinition()
	{
		return length;
	}

	public void setLengthDefinition(LengthDefinition length)
	{
		this.length = length;
	}
}