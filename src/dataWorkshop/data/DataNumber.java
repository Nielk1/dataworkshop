package dataWorkshop.data;

import java.util.HashMap;

import org.w3c.dom.Element;

import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;
import dataWorkshop.xml.XMLSerializeableSingleton;

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
public class DataNumber implements XMLSerializeableSingleton
{

	final static String BITSIZE_TAG = "bitSize";
	final static String SIGNED_TAG = "signed";
	final static String LITTLE_ENDIAN_TAG = "littleEndian";

	final static String CLASS_NAME = "DataNumber";

	int bitSize;
	boolean signed;
	boolean littleEndian;

	transient String name;
	transient long maximum;
	transient long minimum;

	private static HashMap dataNumbers = new HashMap();

	/******************************************************************************
	 *	Constructors
	 */
	public DataNumber()
	{
		this(1, true, false);
	}

	public DataNumber(int bitSize, boolean littleEndian, boolean signed)
	{
		this.bitSize = bitSize;
		this.littleEndian = littleEndian;
		this.signed = signed;
		recalcBoundsAndName();
	}

	/******************************************************************************
	 *	XMLSerializeable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		XMLSerializeFactory.setAttribute(context, BITSIZE_TAG, getBitSize());
		XMLSerializeFactory.setAttribute(context, SIGNED_TAG, isSigned());
		XMLSerializeFactory.setAttribute(context, LITTLE_ENDIAN_TAG, isLittleEndian());
	}

	public void deserialize(Element context)
	{
		bitSize = XMLSerializeFactory.getAttributeAsInt(context, BITSIZE_TAG);
		signed = XMLSerializeFactory.getAttributeAsBoolean(context, SIGNED_TAG);
		littleEndian = XMLSerializeFactory.getAttributeAsBoolean(context, LITTLE_ENDIAN_TAG);
		recalcBoundsAndName();
	}

	public XMLSerializeable getInstance(XMLSerializeable instance)
	{
		DataNumber number = (DataNumber) instance;
		return getDataNumber(number.getBitSize(), number.isLittleEndian(), number.isSigned());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static DataNumber getDataNumber(int bitSize, boolean littleEndian, boolean signed)
	{
		DataNumber number = new DataNumber(bitSize, littleEndian, signed);
		DataNumber n = (DataNumber) dataNumbers.get(number.toString());
		if (n == null)
		{
			dataNumbers.put(number.toString(), number);
			return number;
		}
		return n;
	}

	public Data add(Data data, long value)
	{
		return add(data, value, 0);
	}

	public Data add(Data data, long value, long bitOffset)
	{
		return encode(decode(data, bitOffset) + value);
	}

	/*
	 *  !! Caller must check:  minimum <= value <= maximum
	 */
	public Data encode(long value)
	{
		//assert minimum <= value;
		//assert maximum >= value;
		Data d = new Data(bitSize, Math.abs(value));
		if (isLittleEndian())
		{
			d.swapByteOrder();
		}
		if (signed && value < 0)
		{
			d.negate(0, bitSize);
		}
		return d;
	}

	public long decode(Data data)
	{
		return decode(data, 0);
	}

	public long decode(Data data, long bitOffset)
	{
		Data d = data.copy(bitOffset, bitSize);
		if (isLittleEndian())
		{
			d.swapByteOrder();
		}
		//can this number be negative and is this number negative
		if (signed && d.booleanValue(0))
		{
			d.negate(0, bitSize);
			return 0 - d.longValue(0, bitSize);

		}
		return d.longValue(0, bitSize);
	}

	public int getBitSize()
	{
		return bitSize;
	}

	public long getMax()
	{
		return maximum;
	}

	public long getMin()
	{
		return minimum;
	}

	public String toString()
	{
		return name;
	}

	public boolean isSigned()
	{
		return signed;
	}

	public boolean isLittleEndian()
	{
		return littleEndian;
	}

	private void recalcBoundsAndName()
	{
		if (!signed)
		{
			minimum = 0;
			maximum = 1;
			for (int i = 0; i < bitSize; i++)
			{
				maximum *= 2;
			}
			maximum--;
		}
		else
		{
			minimum = 1;
			for (int i = 0; i < bitSize; i++)
			{
				minimum *= 2;
			}
			minimum /= 2;
			maximum = minimum - 1;
			minimum = 0 - minimum;
		}

		name = " Integer ( " + bitSize + "-bit, ";
		if (signed)
		{
			name += "signed";
		}
		else
		{
			name += "unsigned";
		}

		if (bitSize % 8 == 0 && bitSize > 8)
		{
			name += ", ";
			if (littleEndian)
			{
				name += "little Endian";
			}
			else
			{
				name += "big Endian";
			}
		}

		name += " )";
	}
}
