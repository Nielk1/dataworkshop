package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.StringToDataMapping;
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
public class StringToDataMappingDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "StringToDataMappingDefinition";

	public final static DataEncoding DEFAULT_DATA_CONVERTER = DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY);

	private DataEncoding dataConverter;
	private StringToDataMapping mapping;

	/******************************************************************************
	*	Constructors
	*/
	public StringToDataMappingDefinition()
	{
		this(DEFAULT_DATA_CONVERTER, new StringToDataMapping());
	}

	public StringToDataMappingDefinition(DataEncoding dataConverter, StringToDataMapping mapping)
	{
		this.dataConverter = dataConverter;
		this.mapping = mapping;
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
		XMLSerializeFactory.serialize(context, getDataEncoding());
		XMLSerializeFactory.serialize(context, getMapping());
	}

	public void deserialize(Element context)
	{
		setDataEncoding((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
		setMapping((StringToDataMapping) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		if (getMapping().getBitSize() < 1) {
			validator.getValidatorOutput().error(node, "Bitsize must be > 0");
			return false;
		}
		return true;
	}

	public DataEncoding getDataEncoding()
	{
		return dataConverter;
	}

	public void setDataEncoding(DataEncoding dataConverter)
	{
		this.dataConverter = dataConverter;
	}

	public StringToDataMapping getMapping()
	{
		return mapping;
	}

	public void setMapping(StringToDataMapping mapping)
	{
		this.mapping = mapping;
	}
}