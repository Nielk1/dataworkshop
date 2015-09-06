package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
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
public class EncodingDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "EncodingDefinition";

	final static String VISIBLE_TAG = "visible";

	public final static DataEncoding DEFAULT_DATA_CONVERTER = DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY);

	private DataEncoding encoding;
	private boolean isVisible;

	/******************************************************************************
	*	Constructors
	*/
	public EncodingDefinition()
	{
		this(DEFAULT_DATA_CONVERTER);
	}

	public EncodingDefinition(DataEncoding dataConverter)
	{
		this(dataConverter, true);
	}

	public EncodingDefinition(DataEncoding dataConverter, boolean isVisible)
	{
		this.isVisible = isVisible;
		this.encoding = dataConverter;
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
		XMLSerializeFactory.setAttribute(context, VISIBLE_TAG, isVisible());
		XMLSerializeFactory.serialize(context, getEncoding());
	}

	public void deserialize(Element context)
	{
		setEncoding((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
		setVisible(XMLSerializeFactory.getAttributeAsBoolean(context, VISIBLE_TAG));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		if (getEncoding() == null) {
			validator.getValidatorOutput().error(node, Validator.NO_DATA_ENCODING_DEFINED_ERROR);
			return false;
		}
		return true;
	}

	public DataEncoding getEncoding()
	{
		return encoding;
	}

	public void setEncoding(DataEncoding encoding)
	{
		this.encoding = encoding;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}

}