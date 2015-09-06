package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.view.DataFrame;
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
public class StaticLengthDefinition implements LengthDefinition
{
	public final static String CLASS_NAME = "StaticLengthDefinition";
	public final static String LENGTH_TAG = "length";

	long length;

	/******************************************************************************
	*	Constructors
	*/
	public StaticLengthDefinition()
	{
		this(1);
	}

	public StaticLengthDefinition(long len)
	{
		this.length = len;
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
		XMLSerializeFactory.setAttribute(context, LENGTH_TAG, getLength());
	}

	public void deserialize(Element context)
	{
		setLength(XMLSerializeFactory.getAttributeAsLong(context, LENGTH_TAG));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		if (getLength() < 1)
		{
			validator.getValidatorOutput().error(node, Validator.LENGTH_MUST_BE_GREATER_THAN_ZERO_ERROR);
			return false;
		}
		return true;
	}

	public long getLength(Compiler compiler, ViewDefinitionElement node, long bitOffset, DataFrame parent)
	{
		return getLength();
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(long length)
	{
		this.length = length;
	}
}
