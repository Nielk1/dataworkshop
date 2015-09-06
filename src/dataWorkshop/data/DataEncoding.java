package dataWorkshop.data;

import java.util.Iterator;
import java.util.TreeMap;

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
public abstract class DataEncoding implements XMLSerializeableSingleton
{
	protected String name;
	private int bitSize;
	protected char[] empty;
	protected char deleteChar;

	//lazy instanciation
	protected TreeMap properties;

	/******************************************************************************
	 *	Constructors
	 */
	public DataEncoding(String name, int bitSize, char deleteChar)
	{
		this.name = name;
		this.bitSize = bitSize;
		this.deleteChar = deleteChar;
	}

	/******************************************************************************
	 *	XMLSerializeable Interface
	 */
	public XMLSerializeable getInstance(XMLSerializeable instance)
	{
		DataEncoding converter = (DataEncoding) instance;
		return (XMLSerializeable) DataEncodingFactory.getDataEncoding(converter);
	}

	/******************************************************************************
	 *	Public Methods
	 */
	/**
	 *  	To Data
	 *
	 *	Parse char[] and try to convert into Data
	 *	return null, if error
	 */
	public char[] encode()
	{
		return empty;
	}

	public void encode(char[] chars, int dotOffset)
	{
		System.arraycopy(empty, 0, chars, dotOffset, getDotSize());
	}

	public char[] encode(Data data)
	{
		return encode(data, 0, (long) getBitSize());
	}

	public char[] encode(Data data, long bitOffset, long bitSize)
	{
		char[] chars = new char[(int) (bitSize / getBitSize()) * getDotSize()];
		encode(data, bitOffset, bitSize, chars, 0);
		return chars;
	}

	public char getDeleteChar() {
		return deleteChar;
	}

	/**
	 *  :NOTE: (MPA 2002-09-05)
	 *
	 *  For performance reasons the char[] reference is provided by the caller and not
	 *  by the callee
	 */
	public abstract void encode(Data data, long bitOffset, char[] chars, int dotOffset);

	/*
	 *  Return the trivial name displayed to the user
	 */
	public String getName()
	{
		return name;
	}

	public void encode(Data data, long bitOffset, long bitSize, char[] chars, int dotOffset)
	{
		long maxBitOffset = bitOffset + bitSize;
		while (bitOffset < maxBitOffset)
		{
			encode(data, bitOffset, chars, dotOffset);
			dotOffset += getDotSize();
			bitOffset += getBitSize();
		}
	}

	/*
	 *  The subclass can override this method for performance gain
	 */
	public Data decode(byte[] s) throws DataEncodingException
	{
		return decode(s, 0, 1);
	}

	/*
	 *  The subclass can override this method for performance gain
	 */
	public Data decode(byte[] s, int offset, int size) throws DataEncodingException
	{
		char[] chars = new char[getDotSize()];
		Data data = new Data((size / getDotSize()) * bitSize);
		long bitOffset = 0;

		for (int a = 0; a < size; a += getDotSize())
		{
			for (int i = 0; i < getDotSize(); i++)
			{
				chars[i] = (char) s[offset + i];
			}
			data.replace(bitOffset, bitSize, decode(chars));
			bitOffset += bitSize;
			offset += getDotSize();
		}
		return data;
	}

	/**
	 *  Only decodes one DataConverter.getDotSize()
	 */
	public abstract Data decode(char[] chars) throws DataEncodingException;

	public Data decode(char[] chars, int offset) throws DataEncodingException
	{
		char[] charData = new char[getDotSize()];
		System.arraycopy(chars, offset, charData, 0, getDotSize());
		return decode(charData);
	}

	public Data decode(char[] chars, int offset, int size) throws DataEncodingException
	{
		Data data = new Data((size / getDotSize()) * bitSize);
		long bitOffset = 0;
		int maxOffset = offset + size;
		while (offset < maxOffset)
		{
			data.replace(bitOffset, bitSize, decode(chars, offset));
			bitOffset += bitSize;
			offset += getDotSize();
		}
		return data;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		String n = name;
		n += " (" + bitSize + "-Bit";
		if (properties != null)
		{
			Iterator it = properties.values().iterator();
			while (it.hasNext())
			{
				n += ", " + (String) it.next();
			}
		}
		n += ")";
		return n;
	}

	public int getBitSize()
	{
		return bitSize;
	}

	public int getDotSize()
	{
		return empty.length;
	}

	public boolean hasInputPoint()
	{
		return true;
	}

	public boolean isInputPoint(int dot)
	{
		if (dot >= 0 && dot < getDotSize()) {
			return true;
		}
		return false;
	}

	public int nextInputPoint(int dot)
	{
		dot++;
		while (dot < getDotSize())
		{
			if (isInputPoint(dot))
			{
				return dot;
			}
			dot++;
		}
		return -1;
	}

	public int previousInputPoint(int dot)
	{
		dot--;
		while (dot >= 0)
		{
			if (isInputPoint(dot))
			{
				return dot;
			}
			dot--;
		}
		return -1;
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected void setBitSize(int bitSize)
	{
		this.bitSize = bitSize;
	}

	protected void addProperty(String name, String property)
	{
		if (properties == null)
		{
			properties = new TreeMap();
		}
		properties.put(name, property);
	}

	protected void removeProperty(String name)
	{
		if (properties != null)
		{
			properties.remove(name);
		}
	}

	protected void removeAllProperties()
	{
		if (properties != null)
		{
			properties.clear();
		}
	}

	protected void setEmptyChars(char[] empty)
	{
		this.empty = empty;
	}
}