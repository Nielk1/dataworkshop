package dataWorkshop.data;

import java.util.ArrayList;

import org.w3c.dom.Element;

import dataWorkshop.Utils;
import dataWorkshop.logging.Logger;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

/**
 * Data stores words and dwords in BigEndian.
 * <br>
 * All primitive return types are treated as unsigned
 * (e.g add((int) -1) will add 0xFFFFFFFF and not sub 1)
 *
 *
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
public final class Data implements Cloneable, Comparable, XMLSerializeable
{
	public final static String CLASS_NAME = "Data";

	//Limit when copy() uses System.arraycopy instead of intValue();
	final static long FAST_COPY_LIMIT = 10000;

	//XML Tags
	public final static String BIT_SIZE_TAG = "bitSize";
	public final static String DATA_TAG = "data";

	public final static long[] MAX_UNSIGNED = new long[63];
	public final static long[] MIN_SIGNED = new long[63];
	public final static long[] MAX_SIGNED = new long[63];
	static {
		long max = 1;
		for (int i = 0; i < MAX_UNSIGNED.length; i++)
		{
			MAX_UNSIGNED[i] = max - 1;
			MAX_SIGNED[i] = Math.max(0, (max / 2) - 1);
			MIN_SIGNED[i] = Math.min(0, 0 - (max / 2));
			max *= 2;
		}
	}

	public final static long MAX_SIZE_IN_BYTES = Integer.MAX_VALUE;
	public final static long LONG_BIT_0 = 0x8000000000000000L;

	private final static int[] forwardBitMask =
		{
			0x80000000,
			0xC0000000,
			0xE0000000,
			0xF0000000,
			0xF8000000,
			0xFC000000,
			0xFE000000,
			0xFF000000,
			0xFF800000,
			0xFFC00000,
			0xFFE00000,
			0xFFF00000,
			0xFFF80000,
			0xFFFC0000,
			0xFFFE0000,
			0xFFFF0000,
			0xFFFF8000,
			0xFFFFC000,
			0xFFFFE000,
			0xFFFFF000,
			0xFFFFF800,
			0xFFFFFC00,
			0xFFFFFE00,
			0xFFFFFF00,
			0xFFFFFF80,
			0xFFFFFFC0,
			0xFFFFFFE0,
			0xFFFFFFF0,
			0xFFFFFFF8,
			0xFFFFFFFC,
			0xFFFFFFFE,
			0xFFFFFFFF,
			};

	private final static int[] backwardBitMask =
		{
			0xFFFFFFFF,
			0x7FFFFFFF,
			0x3FFFFFFF,
			0x1FFFFFFF,
			0x0FFFFFFF,
			0x07FFFFFF,
			0x03FFFFFF,
			0x01FFFFFF,
			0x00FFFFFF,
			0x007FFFFF,
			0x003FFFFF,
			0x001FFFFF,
			0x000FFFFF,
			0x0007FFFF,
			0x0003FFFF,
			0x0001FFFF,
			0x0000FFFF,
			0x00007FFF,
			0x00003FFF,
			0x00001FFF,
			0x00000FFF,
			0x000007FF,
			0x000003FF,
			0x000001FF,
			0x000000FF,
			0x0000007F,
			0x0000003F,
			0x0000001F,
			0x0000000F,
			0x00000007,
			0x00000003,
			0x00000001,
			};

	private final static int[] setBitMask =
		{
			0x80000000,
			0x40000000,
			0x20000000,
			0x10000000,
			0x08000000,
			0x04000000,
			0x02000000,
			0x01000000,
			0x00800000,
			0x00400000,
			0x00200000,
			0x00100000,
			0x00080000,
			0x00040000,
			0x00020000,
			0x00010000,
			0x00008000,
			0x00004000,
			0x00002000,
			0x00001000,
			0x00000800,
			0x00000400,
			0x00000200,
			0x00000100,
			0x00000080,
			0x00000040,
			0x00000020,
			0x00000010,
			0x00000008,
			0x00000004,
			0x00000002,
			0x00000001,
			};

	private final static int[] clearBitMask =
		{
			0x7FFFFFFF,
			0xBFFFFFFF,
			0xDFFFFFFF,
			0xEFFFFFFF,
			0xF7FFFFFF,
			0xFBFFFFFF,
			0xFDFFFFFF,
			0xFEFFFFFF,
			0xFF7FFFFF,
			0xFFBFFFFF,
			0xFFDFFFFF,
			0xFFEFFFFF,
			0xFFF7FFFF,
			0xFFFBFFFF,
			0xFFFDFFFF,
			0xFFFEFFFF,
			0xFFFF7FFF,
			0xFFFFBFFF,
			0xFFFFDFFF,
			0xFFFFEFFF,
			0xFFFFF7FF,
			0xFFFFFBFF,
			0xFFFFFDFF,
			0xFFFFFEFF,
			0xFFFFFF7F,
			0xFFFFFFBF,
			0xFFFFFFDF,
			0xFFFFFFEF,
			0xFFFFFFF7,
			0xFFFFFFFB,
			0xFFFFFFFD,
			0xFFFFFFFE,
			};

	private final static int[] modMask =
		{
			0x00000000,
			0x00000001,
			0x00000003,
			0x00000007,
			0x0000000F,
			0x0000001F,
			0x0000003F,
			0x0000007F,
			0x000000FF,
			0x000001FF,
			0x000003FF,
			0x000007FF,
			0x00000FFF,
			0x00001FFF,
			0x00003FFF,
			0x00007FFF,
			0x0000FFFF,
			0x0001FFFF,
			0x0003FFFF,
			0x0007FFFF,
			0x000FFFFF,
			0x001FFFFF,
			0x003FFFFF,
			0x007FFFFF,
			0x00FFFFFF,
			0x01FFFFFF,
			0x03FFFFFF,
			0x07FFFFFF,
			0x0FFFFFFF,
			0x1FFFFFFF,
			0x3FFFFFFF,
			0x7FFFFFFF };

	//Conienve members
	public final static Data DATA_0 = new Data(1, false);
	public final static Data DATA_1 = new Data(1, true);
	public final static Data DATA_00000000 = new Data(8, new byte[] { 0 });
	public final static Data DATA_11111111 = new Data(8, new byte[] { -1 });

	private final static DataEncoding DATA_ENCODING = DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED);

	long length; //length of data in bits;
	/**
	 *  Array for the data
	 *  <p>
	 *  if length == 0 then array == int[0]
	 */
	int[] array;
	
	private Logger logger;

	/******************************************************************************
	 *	Constructors
	 */
	public Data()
	{
		this(0);
	}

	public Data(long len)
	{
		this(len, 0);
	}

	public Data(long len, boolean value)
	{
		this(len, value ? 1 : 0);
	}

	public Data(long len, int value)
	{
		this(len, ((long) value) & 0x00000000FFFFFFFF);
	}

	public Data(long len, long value)
	{
		logger = Logger.getLogger(this.getClass());
		length = len;
		if (length == 0)
		{
			array = new int[0];
		}
		else
		{
			array = new int[(int) (Utils.alignUp(len, 32) / 32)];
			value = value << (64 - length); //align to left
			array[0] = (int) (value >>> 32);
			if (len > 32)
			{
				array[1] = (int) value;
			}
		}
	}

	public Data(long bitSize, byte[] byteData)
	{
		logger = Logger.getLogger(this.getClass());
		length = bitSize;
		array = new int[(int) Utils.alignUp(bitSize, 32) / 32];

		if (array.length > 0)
		{
			int value = 0;
			int offset = 0;
			for (int i = 0; i < byteData.length - 4; i += 4)
			{
				value = ((int) byteData[i]) & 0xFF;
				value = (value << 8) | (((int) byteData[i + 1]) & 0xFF);
				value = (value << 8) | (((int) byteData[i + 2]) & 0xFF);
				value = (value << 8) | (((int) byteData[i + 3]) & 0xFF);
				array[offset] = value;
				offset++;
			}
			int max = (array.length - 1) * 4;
			if (byteData.length > max)
				value = ((int) byteData[max]) & 0xFF;
			else
				value = 0;
			if (byteData.length > max + 1)
				value = (value << 8) | (((int) byteData[max + 1]) & 0xFF);
			else
				value = value << 8;
			if (byteData.length > max + 2)
				value = (value << 8) | (((int) byteData[max + 2]) & 0xFF);
			else
				value = value << 8;
			if (byteData.length > max + 3)
				value = (value << 8) | (((int) byteData[max + 3]) & 0xFF);
			else
				value = value << 8;
			array[max / 4] = value;
		}
	}

	//Clone
	public Data(Data data)
	{
		logger = Logger.getLogger(this.getClass());
		length = data.length;
		array = new int[data.array.length];
		System.arraycopy(data.array, 0, array, 0, array.length);
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
		long originalBitSize = getBitSize();
		XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, originalBitSize);
		XMLSerializeFactory.setAttribute(context, DATA_TAG, Data.serialize(this, DATA_ENCODING));
	}

	public void deserialize(Element context)
	{
		long size = XMLSerializeFactory.getAttributeAsLong(context, BIT_SIZE_TAG);
		if (size > 0)
		{
			Data d = Data.deserialize(size, XMLSerializeFactory.getAttribute(context, DATA_TAG), DATA_ENCODING);
			array = d.array;
			length = d.length;
		}
		else
		{
			array = new int[0];
			length = 0;
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String serialize()
	{
		return XMLSerializeFactory.getInstance().serialize(this);
	}

	public void deserialize(String xml)
	{
		XMLSerializeFactory.getInstance().deserialize(xml, this, false);
	}

	public static Data deserialize(long bitSize, String s, DataEncoding converter)
	{
		if (bitSize == 0)
		{
			return new Data();
		}
		try
		{
			Data data = converter.decode(s.toCharArray(), 0, s.length());
			data.setBitSize(bitSize);
			return data;
		}
		catch (DataEncodingException e)
		{
			Logger.getLogger(Data.class).severe("Could not deserialize Data", e);
			return null;
		}
	}

	public static String serialize(Data data, DataEncoding converter)
	{
		//pad Data to dataConverter bitsize with 0
		long originalBitSize = data.getBitSize();
		long paddedBitSize = Utils.alignUp(originalBitSize, converter.getBitSize());
		data.setBitSize(paddedBitSize);
		char[] outputData = new char[(int) ((data.getBitSize() / converter.getBitSize()) * converter.getDotSize())];
		converter.encode(data, 0, paddedBitSize, outputData, 0);
		data.setBitSize(originalBitSize);
		return new String(outputData);
	}

	public String serialize(DataEncoding converter, BitRange bitRange)
	{
		return Data.serialize(copy(bitRange.getStart(), bitRange.getSize()), converter);
	}

	public int intValue()
	{
		return intValue(0);
	}

	public int intValue(long offset)
	{
		return intValue(offset, Math.min(getBitSize(), 32));
	}

	public byte byteValue(long offset)
	{
		return (byte) intValue(offset, 8);
	}

	public long longValue(long offset)
	{
		return longValue(offset, getBitSize() - offset);
	}

	/**
	 *	Long Value
	 *
	 *	Bits are right aligned
	 */
	public long longValue(long offset, long bitSize)
	{
		long value = 0;
		for (int i = 0; i < bitSize; i++)
		{
			value = value << 1;
			if (booleanValue(offset + i))
			{
				value = value | 1;
			}
		}
		//if (bitSize > 32) {
		//	value = intValue(offset, 32);
		//	value = (value << bitSize - 32) | intValue(offset + 32, bitSize - 32);
		//} else {
		//	value = intValue(offset, bitSize);
		//}
		return value;
	}

	/**
	 *	Int Value
	 *
	 *	Bits are right aligned
	 */
	public int intValue(long offset, long bitSize)
	{
		int byteOffset = (int) (offset >> 5);
		int bitOffset = (int) (offset & modMask[5]);
		if (bitOffset == 0)
		{
			return (array[byteOffset] >>> 32 - bitSize);
		}
		else if (bitSize <= 32 - bitOffset)
		{
			return ((array[byteOffset] & backwardBitMask[bitOffset]) >>> (32 - bitSize - bitOffset));
		}
		else
		{
			return ((array[byteOffset] << bitOffset) | (array[byteOffset + 1] >>> (32 - bitOffset))) >>> (32 - bitSize);
		}
	}

	public double doubleValue(long offset, long intBitSize, long fractionBitSize)
	{
		double value = intValue(offset, intBitSize);
		double fraction = 0.5;
		offset += intBitSize;
		for (int i = 0; i < fractionBitSize; i++)
		{
			if (booleanValue(offset + i))
			{
				value += fraction;
			}
			fraction = fraction * 0.5;
		}
		return value;
	}

	public boolean booleanValue(long bitOffset)
	{
		if ((array[(int) (bitOffset >>> 5)] & setBitMask[(int) (bitOffset & modMask[5])]) != 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * 	Concatenate the given data object to the end of this data object 
	 */
	public void append(Data newData)
	{
		long bitOffset = length;
		setBitSize(length + newData.getBitSize());
		merge(bitOffset, newData);
	}

	public Data copy(long bitOffset, long bitSize)
	{
		Data d = new Data();
		if (bitSize == 0)
		{
			return d;
		}

		d.length = bitSize;
		d.array = new int[(int) (Utils.alignUp(bitSize, 32) / 32)];

		if (bitOffset % 32 == 0 && bitSize > FAST_COPY_LIMIT)
		{
			System.arraycopy(array, (int) (bitOffset / 32), d.array, 0, (int) (bitSize / 32));
			long size = (bitSize / 32) * 32;
			bitOffset += size;
			bitSize -= size;
		}
		else
		{
			int i = 0;
			while (bitSize > 31)
			{
				d.array[i] = intValue(bitOffset, 32);
				bitOffset += 32;
				bitSize -= 32;
				i++;
			}
		}

		if (bitSize > 0)
		{
			d.array[d.array.length - 1] = intValue(bitOffset, bitSize) << (32 - bitSize);
		}

		return d;
	}

	final public void delete(long bitOffset, long bitSize)
	{
		if (bitOffset + bitSize < getBitSize())
		{
			Data d = copy(bitOffset + bitSize, getBitSize() - (bitOffset + bitSize));
			setBitSize(getBitSize() - bitSize);
			merge(bitOffset, d);
		}
		else
		{
			setBitSize(bitOffset);
		}
	}

	final public void replace(long bitOffset, long bitSize, Data newData)
	{
		if (bitSize != newData.length)
		{
			delete(bitOffset, bitSize);
			insert(bitOffset, newData);
		}
		else
		{
			merge(bitOffset, newData);
		}
	}

	/**
	 *  Should be reimplemented to make it faster
	 */
	public void replace(long bitOffset, long bitSize, int value)
	{
		replace(bitOffset, bitSize, new Data(bitSize, value));
	}

	/*
	 *  Make 2-complement of range
	 */
	public void negate(long bitOffset, long bitSize)
	{
		not(bitOffset, bitSize);
		// inc(bitOffset + 1, bitSize - 1);
		inc(bitOffset, bitSize);
	}

	public boolean inc(long bitOffset, long bitSize)
	{
		long bit = bitOffset + bitSize;
		boolean carry = true;
		while (carry & bit > bitOffset)
		{
			bit--;
			carry = booleanValue(bit);
			setBit(bit, !carry);
		}
		return carry;
	}

	public void add(long bitOffset, long bitSize, int value)
	{
		for (int i = 0; i < value; i++)
		{
			inc(bitOffset, bitSize);
		}
	}

	public void sub(long bitOffset, long bitSize, int value)
	{
		for (int i = 0; i < value; i++)
		{
			dec(bitOffset, bitSize);
		}
	}

	public boolean dec() {
		return dec(0, getBitSize());
	}

	public boolean dec(long bitOffset, long bitSize)
	{
		long bit = bitOffset + bitSize;
		boolean carry = false;
		while (!carry & bit > bitOffset)
		{
			bit--;
			carry = booleanValue(bit);
			setBit(bit, !carry);
		}
		return carry;
	}

	public void not() {
		not(0, getBitSize());
	}

	public void not(long bitOffset, long bitSize)
	{
		bitSize += bitOffset;
		while (bitOffset < bitSize)
		{
			setBit(bitOffset, !booleanValue(bitOffset));
			bitOffset++;
		}
	}

	public void setBit(long offset, boolean value)
	{
		int byteOffset = (int) offset >> 5;
		if (value)
		{
			array[byteOffset] = array[byteOffset] | setBitMask[(int) (offset & modMask[5])];
		}
		else
		{
			array[byteOffset] = array[byteOffset] & clearBitMask[(int) (offset & modMask[5])];
		}
	}

	public long getBitSize()
	{
		return length;
	}

	public void setBitSize(long newSize)
	{
		if (newSize > 0)
		{
			int[] newArray = new int[(int) (Utils.alignUp(newSize, 32) / 32)];
			int len = Math.min(newArray.length, array.length);
			System.arraycopy(array, 0, newArray, 0, len);
			array = newArray;
			length = newSize;
		}
		else
		{
			length = 0;
			array = new int[0];
		}
	}

	public byte[] toByteArray()
	{
		byte[] bytes = new byte[(int) (Utils.alignUp(getBitSize(), 8) / 8)];
		for (int i = 0; i < bytes.length - 1; i++)
		{
			bytes[i] = (byte) intValue(i << 3, 8);
		}
		long off = (bytes.length - 1) * 8;
		bytes[bytes.length - 1] = (byte) intValue(off, getBitSize() - off);
		return bytes;
	}

	/**
	 *  :TODO: Should speed this up
	 */
	public boolean isZero()
	{
		long offset = 0;
		while (offset < getBitSize())
		{
			if (booleanValue(offset))
			{
				return false;
			}
		}
		return true;
	}

	public Object clone()
	{
		Data data = new Data(getBitSize());
		System.arraycopy(array, 0, data.array, 0, array.length);
		return data;
	}

	public boolean equals(Object o)
	{
		if (o instanceof Data)
		{
			Data d = (Data) o;
			if (getBitSize() == d.getBitSize())
			{
				return equals(d, 0);
			}
		}
		return false;
	}

	public int hashCode()
	{
		return (int) getBitSize();
	}

	/*
	 *  if d.getBitSize() == 0 returns true;
	 */
	public boolean equals(Data d, long offset)
	{
		if (d.getBitSize() + offset > getBitSize())
		{
			return false;
		}

		long maxBitOffset = d.getBitSize();

		for (int i = 0; i < maxBitOffset; i++)
		{
			if (d.booleanValue(i) != booleanValue(offset + i))
			{
				return false;
			}
		}

		return true;
	}

	public long search(Data search, boolean direction, long offset)
	{
		return search(search, direction, offset, 1);
	}

	/**
	 *	Search
	 *
	 *	If search.getBitSize() == 0, -1 is returned
	 *
	 *	Compare searchData to this. If compare fails add granularity to offset and search again
	 *	till end of this is reached.
	 */
	public long search(Data search, boolean direction, long offset, long granularity)
	{
		if (search.getBitSize() == 0)
		{
			return -1;
		}
		//forward
		if (direction)
		{
			offset = Utils.alignUp(offset, granularity);
			long matchedBits = 0;
			while (offset < getBitSize())
			{
				while (booleanValue(offset) == search.booleanValue(matchedBits))
				{
					matchedBits++;
					offset++;
					if (matchedBits == search.getBitSize())
					{
						return offset - matchedBits;
					}
					if (offset >= getBitSize())
					{
						return -1;
					}
				}
				offset -= matchedBits;
				offset += granularity;
				matchedBits = 0;
			}
		}
		//backward
		else
		{
			offset = Utils.alignDown(offset, granularity);
			long matchedBits = search.getBitSize() - 1;
			long oldOffset = offset;
			while (offset > -1)
			{
				while (booleanValue(offset) == search.booleanValue(matchedBits))
				{
					matchedBits--;
					if (matchedBits < 0)
					{
						return offset;
					}
					offset--;
					if (offset < 0)
					{
						return -1;
					}
				}
				oldOffset -= granularity;
				offset = oldOffset;
				matchedBits = search.getBitSize() - 1;
			}
		}
		return -1;
	}

	public BitRange[] diff(Data searchData, long offset)
	{
		return diff(searchData, offset, 1);
	}

	/**
	 *	Diff
	 *
	 *  offset + search.getBitSize <= this.getBitSize()
	 */
	public BitRange[] diff(Data search, long offset, int granularity)
	{
		if (search.getBitSize() + offset > getBitSize())
		{
			System.out.println(this +"SearchData.getBitSize() + offset > this.getBitSize()");
			return null;
		}

		ArrayList diffs = new ArrayList();
		long maxOffset = offset + search.getBitSize();
		long diffOffset = 0;
		long diffSize = 0;

		Data d;

		while (offset < maxOffset)
		{
			d = search.copy(offset, granularity);
			if (equals(d, offset))
			{
				if (diffSize != 0)
				{
					diffs.add(new BitRange(diffOffset, diffSize));
					diffSize = 0;
				}
				offset += granularity;
				diffOffset = offset;
			}
			else
			{
				offset += granularity;
				diffSize += granularity;
			}
		}

		if (diffSize != 0)
		{
			diffs.add(new BitRange(diffOffset, diffSize));
		}

		BitRange[] intervals = new BitRange[diffs.size()];
		for (int i = 0; i < intervals.length; i++)
		{
			intervals[i] = (BitRange) diffs.get(i);
		}

		return intervals;
	}

	public void swapByteOrder()
	{
		if (getBitSize() > 8 && getBitSize() % 8 == 0)
		{
			swapByteOrder(0, (int) getBitSize() / 8);
		}
		else
		{
			logger.warning("Could not swap bytes", new Exception());
		}
	}

	/*
	 *  Change the order of nBytes starting at bitOffset
	 */
	public void swapByteOrder(long bitOffset, int nBytes)
	{
		Data d = copy(bitOffset, nBytes * 8);
		for (long i = d.getBitSize() - 8; i >= 0; i -= 8)
		{
			replace(bitOffset, 8, d.copy(i, 8));
			bitOffset += 8;
		}
	}

	public String toString()
	{
		String s = "BitSize: " + getBitSize();
		s += " Data: " + toString(DATA_ENCODING);
		return s;
	}
	
	public String toString(DataEncoding encoding) {
		return Data.serialize(this, encoding);
	}

	/**
	 *  natural order:
	 *  smaller Data Objects first
	 *  data with most significant bit set is bigger
	 */
	public int compareTo(Object o)
	{
		Data data = (Data) o;
		if (data.getBitSize() > getBitSize())
		{
			return -1;
		}
		else if (data.getBitSize() < getBitSize())
		{
			return 1;
		}
		else
		{
			for (long bit = 0; bit < getBitSize(); bit++)
			{
				if (data.booleanValue(bit) != booleanValue(bit))
				{
					// If this.booleanValue(bit) is true the other must be false
					if (booleanValue(bit))
					{
						return -1;
					}
					else
					{
						return 1;
					}
				}
			}
		}
		return 0;
	}

	public static long maxSignedNumber(int bitSize)
	{
		return MAX_SIGNED[bitSize];
	}

	public static long minSignedNumber(int bitSize)
	{
		return MIN_SIGNED[bitSize];
	}

	public static long maxUnsignedNumber(int bitSize)
	{
		return MAX_UNSIGNED[bitSize];
	}

	/**
	 *  Bits are number from left to right
	 */
	public static Data toData(long value, int bitStart, int bitSize)
	{
		Data d = new Data(bitSize);
		int bit = 0;
		while (bit < bitStart)
		{
			value = value << 1;
			bit++;
		}
		while (bit <= bitStart + bitSize)
		{
			if ((value & LONG_BIT_0) != 0)
			{
				d.setBit(bit, true);
			}
			value = value << 1;
			bit++;
		}
		return d;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void insert(long bitOffset, Data newData)
	{
		long tailOffset = bitOffset + newData.getBitSize();
		Data tail = copy(bitOffset, getBitSize() - bitOffset);
		setBitSize(getBitSize() + newData.getBitSize());
		merge(bitOffset, newData);
		merge(tailOffset, tail);
	}

	final private void merge(long offset, Data newData)
	{
		if (length == 0)
		{
			//clone newData and put the reference into this
			deserialize(newData.serialize());
			return;
		}
		int oldBitOffset = (int) (offset & modMask[5]);
		int oldIntOffset = (int) (offset >>> 5);
		long newBitSize = newData.length;
		long newBitOffset = 0;
		int oldValue;
		int newValue;

		//align oldBitOffset to zero
		if (oldBitOffset != 0 & newBitSize != 0)
		{
			if (newBitSize >= 32 - oldBitOffset)
			{
				oldValue = array[oldIntOffset] & forwardBitMask[oldBitOffset - 1];
				newValue = newData.intValue(0, 32) >>> oldBitOffset;
			}
			else
			{
				oldValue = array[oldIntOffset] & (forwardBitMask[oldBitOffset - 1] | backwardBitMask[(int) (oldBitOffset + newBitSize)]);
				newValue = (newData.intValue(0, 32) >>> oldBitOffset) & forwardBitMask[(int) (oldBitOffset + newBitSize)];
			}
			array[oldIntOffset] = oldValue | newValue;
			newBitOffset += (32 - oldBitOffset);
			oldIntOffset++;
		}
		//Do da fast copy
		while (newBitSize - newBitOffset > 31)
		{
			array[oldIntOffset] = newData.intValue(newBitOffset, 32);
			oldIntOffset++;
			newBitOffset += 32;
		}
		//Do da tail copy
		if (newBitOffset < newBitSize)
		{
			newValue = newData.intValue(newBitOffset, newBitSize - newBitOffset) << 32 - (newBitSize - newBitOffset);
			array[oldIntOffset] = (array[oldIntOffset] & backwardBitMask[(int) (newBitSize - newBitOffset)]) | newValue;
		}
	}
}