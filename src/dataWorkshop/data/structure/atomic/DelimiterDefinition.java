package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.structure.ViewDefinitionElement;
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
public class DelimiterDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "DelimiterDefinition";

	final static String SEARCH_STEP_SIZE_TAG = "searchStepSize";

	public final static DataEncoding DEFAULT_DATA_CONVERTER = DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY);

	DataEncoding delimiterEncoding;
	Data delimiterData;
	int stepSize;

	/******************************************************************************
	*	Constructors
	*/
	public DelimiterDefinition()
	{
		this(DEFAULT_DATA_CONVERTER, new Data(), 8);
	}

	public DelimiterDefinition(DataEncoding delimiterEncoding, Data delimiterData, int stepSize)
	{
		this.delimiterEncoding = delimiterEncoding;
		this.delimiterData = delimiterData;
		this.stepSize = stepSize;
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
		XMLSerializeFactory.setAttribute(context, SEARCH_STEP_SIZE_TAG, getStepSize());
		XMLSerializeFactory.serialize(context, getDelimiterEncoding());
		XMLSerializeFactory.serialize(context, getDelimiterData());
	}

	public void deserialize(Element context)
	{
		setStepSize(XMLSerializeFactory.getAttributeAsInt(context, SEARCH_STEP_SIZE_TAG));
		setDelimiterEncoding((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
		setDelimiterData((Data) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		if (stepSize < 1)
		{
			validator.getValidatorOutput().error(node, Validator.STEP_SIZE_MUST_BE_GREATER_THAN_ZERO_ERROR);
			return false;
		}

		if (getDelimiterEncoding() == null)
		{
			validator.getValidatorOutput().error(node, Validator.NO_DELIMITER_ENCODING_DEFINED_ERROR);
			return false;
		}

		if (getDelimiterData() == null)
		{
			validator.getValidatorOutput().error(node, Validator.NO_DELIMITER_DATA_DEFINED_ERROR);
			return false;
		}
		return true;
	}

	public long searchDelimiterOffset(Compiler compiler, ViewDefinitionElement node, long bitOffset)
	{
		Data data = compiler.getData();
		long delimiterBitOffset = data.search(getDelimiterData(), true, bitOffset, getStepSize());
		if (delimiterBitOffset != -1)
		{
			compiler.getCompilerOutput().info(node, bitOffset, "Delimiter Found at" + new UnsignedOffset(delimiterBitOffset));
		}
		else
		{
			compiler.getCompilerOutput().info(node, bitOffset, "Delimiter Not Found");
			delimiterBitOffset = data.getBitSize();
		}

		return delimiterBitOffset;
	}

	public DataEncoding getDelimiterEncoding()
	{
		return delimiterEncoding;
	}

	public void setDelimiterEncoding(DataEncoding delimiterEncoding)
	{
		this.delimiterEncoding = delimiterEncoding;
	}

	public void setDelimiterData(Data delimiterData)
	{
		this.delimiterData = delimiterData;
	}

	public Data getDelimiterData()
	{
		return delimiterData;
	}

	public int getStepSize()
	{
		return stepSize;
	}

	public void setStepSize(int stepSize)
	{
		this.stepSize = stepSize;
	}
}