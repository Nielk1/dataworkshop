package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataTransformer;
import dataWorkshop.xml.XMLSerializeFactory;

/**
 * if bits is positive shift right, else shift left
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
public class Shift extends DataTransformer
{

	int bits = 0;
	boolean fillWithOnes = false;

	final static String BITS_TAG = "bits";
	final static String FILL_WITH_ONES_TAG = "fillWithOnes";
	final static String CLASS_NAME = "Shift";

	/******************************************************************************
	 *	Constructors
	 */
	public Shift()
	{
		this(1, false);
	}

	public Shift(int bits, boolean fillWithOnes)
	{
		super(bits);
		this.bits = bits;
		this.fillWithOnes = fillWithOnes;
	}

	/******************************************************************************
	 *	XMLSerializeable Interface
	 */
	public void serialize(Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.setAttribute(context, BITS_TAG, getBits());
		XMLSerializeFactory.setAttribute(context, FILL_WITH_ONES_TAG, isFillWithOnes());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setBits(XMLSerializeFactory.getAttributeAsInt(context, BITS_TAG));
		setFillWithOnes(XMLSerializeFactory.getAttributeAsBoolean(context, FILL_WITH_ONES_TAG));
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public int getBits()
	{
		return bits;
	}

	public void setBits(int bits)
	{
		this.bits = bits;
	}

	/**
	 * @return boolean
	 */
	public boolean isFillWithOnes()
	{
		return fillWithOnes;
	}

	/**
	 * Sets the fillWithOnes.
	 * @param fillWithOnes The fillWithOnes to set
	 */
	public void setFillWithOnes(boolean fillWithOnes)
	{
		this.fillWithOnes = fillWithOnes;
	}

	public Data transform(Data data, long bitOffset, long bitSize)
	{
		int shiftedBits = Math.abs(bits);

		//If bitSize <= bits the transformation will result in a fill the selection with either ones or zeros
		if (bitSize <= shiftedBits)
		{
			Data result = new Data(bitSize);
			if (isFillWithOnes())
			{
				result.negate(0, result.getBitSize());
			}
			return result;
		}
		else
		{
			Data result = new Data(shiftedBits);
			if (isFillWithOnes())
			{
				result.negate(0, result.getBitSize());
			}
			//do a right shift
			if (bits > 0)
			{
				result.append(data.copy(bitOffset, bitSize - shiftedBits));
				return result;
			}
			//dot a left shift
			else
			{
				Data d = data.copy(bitOffset + shiftedBits, bitSize - shiftedBits);
				d.append(result);
				return d;
			}
		}
	}
}