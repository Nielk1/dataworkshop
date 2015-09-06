package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
import dataWorkshop.data.view.DataEncodingField;
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
public abstract class AbstractDataEncodingFieldDefinition extends DataFieldDefinition
{

	EncodingDefinition converterFieldDefinition;

	/******************************************************************************
	 *	Constructors
	 */
	public AbstractDataEncodingFieldDefinition()
	{
		this(DEFAULT_LABEL);
	}

	public AbstractDataEncodingFieldDefinition(String label)
	{
		this(label, new EncodingDefinition());
	}

	public AbstractDataEncodingFieldDefinition(String label, EncodingDefinition converterFieldDefinition)
	{
		super(label);
		this.converterFieldDefinition = converterFieldDefinition;
	}

	/******************************************************************************
	  *	XML Serializeable Interface
	  */
	public void serialize(Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.serialize(context, getConverterFieldDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setConverterFieldDefinition((EncodingDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & converterFieldDefinition.validate(validator, this);
		return isValid;
	}

	public DataFrame render()
	{
		DataEncodingField node = new DataEncodingField();
		render(node);
		return node;
	}

	public void render(DataFrame node)
	{
		super.render(node);
		DataEncodingField field = (DataEncodingField) node;
		field.setDataConverter(getConverterFieldDefinition().getEncoding());
	}

	public EncodingDefinition getConverterFieldDefinition()
	{
		return converterFieldDefinition;
	}

	public void setConverterFieldDefinition(EncodingDefinition converterFieldDefinition)
	{
		this.converterFieldDefinition = converterFieldDefinition;
	}

}