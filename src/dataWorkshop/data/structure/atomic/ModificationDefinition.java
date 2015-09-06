package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.structure.ViewDefinitionElement;
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
public class ModificationDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "ModificationDefinition";

	final static String MULTIPLICATION_TAG = "multiplication";
	final static String ADDITION_TAG = "addition";

	long multiplication = 1;
	long addition = 0;

	/******************************************************************************
	*	Constructors
	*/
	public ModificationDefinition()
	{
		this(0, 1);
	}

	public ModificationDefinition(long multiplication, long addition)
	{
		this.multiplication = multiplication;
		this.addition = addition;
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
		XMLSerializeFactory.setAttribute(context, MULTIPLICATION_TAG, getMultiplication());
		XMLSerializeFactory.setAttribute(context, ADDITION_TAG, getAddition());
	}

	public void deserialize(Element context)
	{
		setMultiplication(XMLSerializeFactory.getAttributeAsLong(context, MULTIPLICATION_TAG));
		setAddition(XMLSerializeFactory.getAttributeAsLong(context, ADDITION_TAG));
	}

	/******************************************************************************
	  *	Public Methods
	  */
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		return true;
	}

	public long calculate(long bitSize)
	{
		bitSize *= getMultiplication();
		bitSize += getAddition();
		return bitSize;
	}

	public long getAddition()
	{
		return addition;
	}

	public void setAddition(long addition)
	{
		this.addition = addition;
	}

	public long getMultiplication()
	{
		return multiplication;
	}

	public void setMultiplication(long mul)
	{
		multiplication = mul;
	}
}