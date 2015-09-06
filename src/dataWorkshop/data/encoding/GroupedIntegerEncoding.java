package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
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
public class GroupedIntegerEncoding extends NumberEncoding
{

	public final static String BIT_GROUPS_TAG = "bitGroups";
	public final static String SIGNED_TAG = "signed";

	final static String CLASS_NAME = "GroupedIntegerEncoding";
	public final static String NAME = "Grouped Integer";

	int[] bitGroups;
	boolean signed;

	transient private long maximum;
	transient private long minimum;
	transient private String maximumString;
	transient private String minimumString;

	transient private DataEncoding[] dataConverters;
	//These are for the unsigned Converter
	transient private int[] invalidInputPoints;

	/******************************************************************************
	 *	Constructors
	 */
	public GroupedIntegerEncoding()
	{
		this(2, new int[] { 4 }, false, false);
	}

	public GroupedIntegerEncoding(int radix, int[] bitGroups, boolean littleEndian, boolean signed)
	{
		super(NAME, radix, 1, littleEndian);
		setSigned(signed);
		setBitGroups(bitGroups);
		recalc();
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
		super.serialize(context);
		XMLSerializeFactory.setAttribute(context, SIGNED_TAG, signed);
		XMLSerializeFactory.setAttribute(context, BIT_GROUPS_TAG, getBitGroups());
	}

	public void deserialize(Element context)
	{
		removeAllProperties();
		super.deserialize(context);
		setSigned(XMLSerializeFactory.getAttributeAsBoolean(context, SIGNED_TAG));
		setBitGroups(XMLSerializeFactory.getAttributeAsIntArray(context, BIT_GROUPS_TAG));
		recalc();
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void encode(Data originalData, long offset, char[] chars, int dot)
	{
		Data data = originalData.copy(offset, getBitSize());
		if (isLittleEndian())
		{
			data.swapByteOrder();
		}

		if (signed)
		{
			if (data.booleanValue(offset))
			{
				data.negate(offset, getBitSize());
				chars[dot] = '-';
			}
			else
			{
				chars[dot] = '+';
			}
			dot++;
		}

		Data splitData;
		long off = offset;
		for (int i = 0; i < dataConverters.length; i++)
		{
			splitData = data.copy(off, dataConverters[i].getBitSize());
			off += splitData.getBitSize();
			dataConverters[i].encode(splitData, 0, chars, dot);
			if (i != dataConverters.length - 1)
			{
				dot += dataConverters[i].getDotSize();
				chars[dot] = '.';
				dot++;
			}
		}
	}

	public Data decode(char[] s) throws DataEncodingException
	{
		int startDot = 0;

		if (signed)
		{
			startDot++;
			if (s[0] != '+' && s[0] != '-')
			{
				char[] data = new char[getDotSize()];
				System.arraycopy(s, 0, data, 0, getDotSize());
				throw new DataEncodingException(this, data, s[0]);
			}
		}

		Data d = new Data();

		for (int i = 0; i < dataConverters.length; i++)
		{
			char[] chars = new char[dataConverters[i].getDotSize()];
			System.arraycopy(s, startDot, chars, 0, chars.length);
			startDot += chars.length;
			startDot++;
			d.append(dataConverters[i].decode(chars));
		}

		long finalValue = d.longValue(0, getBitSize());
		if (finalValue > maximum)
		{
			char[] data = new char[getDotSize()];
			System.arraycopy(s, 0, data, 0, getDotSize());
			throw new DataEncodingException(this, new String(data), minimumString, maximumString);
		}

		if (signed && s[0] == '-')
		{
			if (0 - finalValue < minimum)
			{
				char[] data = new char[getDotSize()];
				System.arraycopy(s, 0, data, 0, getDotSize());
				throw new DataEncodingException(this, new String(data), minimumString, maximumString);
			}
		}

		if (signed && s[0] == '-')
		{
			d.negate(0, getBitSize());
		}

		if (isLittleEndian())
		{
			d.swapByteOrder();
		}

		return d;
	}

	public boolean isInputPoint(int dot)
	{
		if (signed)
		{
			dot--;
		}
		for (int i = 0; i < invalidInputPoints.length; i++)
		{
			if (dot == invalidInputPoints[i])
			{
				return false;
			}
		}
		return true;
	}

	public int hashCode()
	{
		return NAME.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof GroupedIntegerEncoding)
		{
			GroupedIntegerEncoding converter = (GroupedIntegerEncoding) obj;
			if (this.signed != converter.signed)
			{
				return false;
			}
			if (this.bitGroups.length != converter.bitGroups.length)
			{
				return false;
			}
			for (int i = 0; i < this.bitGroups.length; i++)
			{
				if (bitGroups[i] != converter.bitGroups[i])
				{
					return false;
				}
			}
			return super.equals(obj);
		}
		return false;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	protected void setSigned(boolean signed)
	{
		this.signed = signed;
		if (signed)
		{
			addProperty(SIGNED_TAG, "Signed");
		}
	}

	protected int[] getBitGroups()
	{
		return bitGroups;
	}

	protected void setBitGroups(int[] bitGroups)
	{
		this.bitGroups = bitGroups;
		int dot = 0;
		int bitSize = 0;
		dataConverters = new IntegerEncoding[bitGroups.length];
		invalidInputPoints = new int[bitGroups.length];
		for (int i = 0; i < bitGroups.length; i++)
		{
			bitSize += bitGroups[i];
			dataConverters[i] = new IntegerEncoding(radix, bitGroups[i], false, false);
			dot += dataConverters[i].getDotSize();
			invalidInputPoints[i] = dot;
			dot++;
		}
		setBitSize(bitSize);
		addProperty("Grouping", getBitsizeNametag());
	}

	protected String getBitsizeNametag()
	{
		String s = new String();
		for (int i = 0; i < bitGroups.length; i++)
		{
			s += Integer.toString(bitGroups[i]);
			if (i != bitGroups.length - 1)
			{
				s += "-";
			}
		}
		return s;
	}

	protected void recalc()
	{
		int bitSize = getBitSize();
		Data minData = null;
		Data maxData = null;

		if (!signed)
		{
			minimum = 0;
			maximum = Data.maxUnsignedNumber(bitSize);
			minData = new Data(getBitSize());
			maxData = new Data(minData);
			maxData.not();
		}
		else
		{
			minimum = Data.minSignedNumber(bitSize);
			maximum = Data.maxSignedNumber(bitSize);
			minData = new Data(getBitSize());
			maxData = new Data(getBitSize());
			maxData.not();
			if (minData.getBitSize() > 0)
			{
				minData.setBit(0, true);
				maxData.setBit(0, false);
			}
		}

		int dotSize = 0;
		for (int i = 0; i < dataConverters.length; i++)
		{
			dotSize += dataConverters[i].getDotSize();
			dotSize++;
		}
		dotSize--;
		if (signed)
		{
			dotSize++;
		}

		char[] e = new char[dotSize];
		for (int i = 0; i < dotSize; i++)
		{
			e[i] = '-';
		}
		setEmptyChars(e);
		
		/**
		 * :TRICKY:Martin Pape:Sep 22, 2003
		 *	has to be last because the encode method needs a
		 * completely initialitzed object
		 */
		minimumString = new String(encode(minData));
		maximumString = new String(encode(maxData));
		addProperty("Range", minimumString + " to " + maximumString);
	}
}