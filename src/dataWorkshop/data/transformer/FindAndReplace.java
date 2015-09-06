/**

 * 
 *	Find Options
 *	@version 1.0 2000/08/23
 * 	@author metheus@gmx.net
 */

package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataTransformer;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

/**
 *  :NOTE:Martin Pape:Jun 22, 2003
 * Dont know if it is a good idea to use the FindAndReplace as a Transformation and FindAndReplace Options.
 * The main difference is the transformation uses much more memory as it is done on a copy  of the data.
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
public class FindAndReplace extends DataTransformer implements XMLSerializeable
{
	public final static String STEP_SIZE_TAG = "stepSize";
	public final static String CLASS_NAME = "FindAndReplace";

	private Data findData;
	private Data replaceData;
	private DataEncoding findDataEncoding;
	private DataEncoding replaceDataEncoding;
	private long stepSize;

	/******************************************************************************
	*	Constructors
	*/
	public FindAndReplace()
	{
		super();
	}

	public FindAndReplace(Data f, Data r, DataEncoding fUnit, DataEncoding rUnit, int gran)
	{
		super();
		findData = f;
		replaceData = r;
		findDataEncoding = fUnit;
		replaceDataEncoding = rUnit;
		stepSize = gran;
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
		XMLSerializeFactory.setAttribute(context, STEP_SIZE_TAG, getStepSize());
		XMLSerializeFactory.serialize(context, getFindDataEncoding());
		XMLSerializeFactory.serialize(context, getFindData());
		XMLSerializeFactory.serialize(context, getReplaceDataEncoding());
		XMLSerializeFactory.serialize(context, getReplaceData());
	}

	public void deserialize(Element context)
	{
		setStepSize(XMLSerializeFactory.getAttributeAsLong(context, STEP_SIZE_TAG));
		setFindDataEncoding((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
		setFindData((Data) XMLSerializeFactory.deserializeFirst(context));
		setReplaceDataEncoding((DataEncoding) XMLSerializeFactory.deserializeFirst(context));
		setReplaceData((Data) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public Data getFindData()
	{
		return findData;
	}

	public void setFindData(Data findData)
	{
		this.findData = findData;
	}

	public Data getReplaceData()
	{
		return replaceData;
	}

	public void setReplaceData(Data replaceData)
	{
		this.replaceData = replaceData;
	}

	public DataEncoding getFindDataEncoding()
	{
		return findDataEncoding;
	}

	public void setFindDataEncoding(DataEncoding findDataEncoding)
	{
		this.findDataEncoding = findDataEncoding;
	}

	public DataEncoding getReplaceDataEncoding()
	{
		return replaceDataEncoding;
	}

	public void setReplaceDataEncoding(DataEncoding replaceDataEncoding)
	{
		this.replaceDataEncoding = replaceDataEncoding;
	}

	public long getStepSize()
	{
		return stepSize;
	}

	public void setStepSize(long stepSize)
	{
		this.stepSize = stepSize;
	}

	public boolean hasFind()
	{
		return findData.getBitSize() != 0;
	}

	public Data transform(Data data, long bitOffset, long bitSize)
	{
		while (bitOffset < bitSize)
		{
			long offset = data.search(findData, true, bitOffset);
			if (offset != -1)
			{
				data.replace(offset, findData.getBitSize(), replaceData);
			}
			bitOffset += replaceData.getBitSize() + stepSize;
			bitSize += (replaceData.getBitSize() - findData.getBitSize()); 
		}
		return data;
	}
}