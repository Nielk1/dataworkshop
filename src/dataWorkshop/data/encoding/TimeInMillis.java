package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingException;

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
public class TimeInMillis extends AbstractNumberEncoding
{
	public final static String CLASS_NAME = "TimeInMillisEncoding";
	public final static String NAME = "Milliseconds";
	
	transient long minimum;
	transient long maximum;
	transient String minimumString;
	transient String maximumString;

	/******************************************************************************
	 *	Constructors
	 */
	public TimeInMillis()
	{
		this(true);
	}

	public TimeInMillis(boolean littleEndian)
	{
		super(NAME, 32, littleEndian);
		setEmptyChars(new char[] { '-', '-', 'd', ' ', '-', '-', ':', '-', '-', ':', '-', '-', '.', '-', '-', '-' });
		recalc();
	}

	/******************************************************************************
	 *	XMLSerializeable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}
	
	public void serialize(Element context) {
		super.serialize(context);
		recalc();
	}
    
   public void deserialize(Element context) {
		super.deserialize(context);
		recalc();
   }

	/******************************************************************************
	 *	Public Methods
	 */
	public void encode(Data data, long offset, char[] chars, int dot)
	{
		Data d = data.copy(offset, getBitSize());
		if (isLittleEndian())
		{
			d.swapByteOrder();
		}

		System.arraycopy(encode(), 0, chars, dot, getDotSize());
		int index = dot + getDotSize();

		long value = d.longValue(0, getBitSize());
		long millis = value % 1000;
		index -= 3;
		copy(millis, 3, chars, index);

		value = (value - millis) / 1000;
		long seconds = value % 60;
		index -= 3;
		copy(seconds, 2, chars, index);

		value = (value - seconds) / 60;
		long minutes = value % 60;
		index -= 3;
		copy(minutes, 2, chars, index);

		value = (value - minutes) / 60;
		long hours = value % 24;
		index -= 3;
		copy(hours, 2, chars, index);

		value = (value - hours) / 24;
		index -= 4;
		copy(value, 2, chars, index);
	}

	public Data decode(char[] s) throws DataEncodingException
	{
		int offset = getDotSize() - 3;
		long millis = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		long days = 0;

		try
		{
			millis = parseCharArray(s, offset, 3, 10);
		}
		catch (DataEncodingException e)
		{
			//create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] chars = new char[s.length];
			System.arraycopy(s, 0, chars, 0, chars.length);
			throw new DataEncodingException(this, chars, e);
		}

		if (millis < 0 || millis > 999)
		{
			throw new DataEncodingException(this, new String(s), Long.toString(millis), Integer.toString(0), Integer.toString(999));
		}
		offset -= 3;

		try
		{
			seconds = parseCharArray(s, offset, 2, 10);
		}
		catch (DataEncodingException e)
		{
			//create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] chars = new char[s.length];
			System.arraycopy(s, 0, chars, 0, chars.length);
			throw new DataEncodingException(this, chars, e);
		}

		if (seconds < 0 || seconds > 59)
		{
			throw new DataEncodingException(this, new String(s), Long.toString(seconds), Integer.toString(0), Integer.toString(59));
		}
		offset -= 3;

		try
		{
			minutes = parseCharArray(s, offset, 2, 10);
		}
		catch (DataEncodingException e)
		{
			//	create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] chars = new char[s.length];
			System.arraycopy(s, 0, chars, 0, chars.length);
			throw new DataEncodingException(this, chars, e);
		}

		if (minutes < 0 || minutes > 59)
		{
			throw new DataEncodingException(this, new String(s), Long.toString(minutes), Integer.toString(0), Integer.toString(59));
		}
		offset -= 3;

		try
		{
			hours = parseCharArray(s, offset, 2, 10);
		}
		catch (DataEncodingException e)
		{
			//create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] chars = new char[s.length];
			System.arraycopy(s, 0, chars, 0, chars.length);
			throw new DataEncodingException(this, chars, e);
		}

		if (hours < 0 || hours > 23)
		{
			throw new DataEncodingException(this, new String(s), Long.toString(hours), Integer.toString(0), Integer.toString(23));
		}
		offset -= 4;

		try
		{
			days = parseCharArray(s, offset, 2, 10);
		}
		catch (DataEncodingException e)
		{
			//create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] chars = new char[s.length];
			System.arraycopy(s, 0, chars, 0, chars.length);
			throw new DataEncodingException(this, chars, e);
		}

		if (days < 0 || days > 49)
		{
			throw new DataEncodingException(this, new String(s), Long.toString(days), Integer.toString(0), Integer.toString(49));
		}

		long finalValue = (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000) + millis;
		if (finalValue < 0 || finalValue > maximum)
		{
			throw new DataEncodingException(this, new String(s), minimumString, maximumString);
		}

		Data d = new Data(32, finalValue);
		if (isLittleEndian())
		{
			d.swapByteOrder();
		}
		return d;
	}

	public boolean isInputPoint(int dot)
	{
		if (encode()[dot] == '-')
		{
			return true;
		}
		return false;
	}

	public int hashCode()
	{
		return NAME.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof TimeInMillis)
		{
			return super.equals(obj);
		}
		return false;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void recalc() {
		minimum = 0;
		maximum = ((long) Integer.MAX_VALUE) * 2;
		Data minData = new Data(getBitSize());
		Data maxData = new Data(minData);
		maxData.not();
		minimumString = new String(encode(minData));
		maximumString = new String(encode(maxData));
		addProperty("Range", minimumString + " to " + maximumString);
	}
	
	private void copy(long value, int dotSize, char[] chars, int dotOffset)
	{
		String number = Long.toString(value);
		while (number.length() < dotSize)
		{
			number = "0" + number;
		}
		System.arraycopy(number.substring(number.length() - dotSize).toCharArray(), 0, chars, dotOffset, dotSize);
	}

	protected long parseCharArray(char[] chars, int dotOffset, int dotSize, int radix) throws DataEncodingException
	{
		long finalValue = 0;
		long a = 1;
		int value;
		for (int i = dotOffset + dotSize - 1; i >= dotOffset; i--)
		{
			value = NumberEncoding.valueForChar[chars[i]];
			if (value == -1 || value >= radix)
			{
				char[] data = new char[dotSize];
				System.arraycopy(chars, dotOffset, data, 0, dotSize);
				throw new DataEncodingException(this, data, chars[i]);
			}
			finalValue += (value * a);
			a *= radix;
		}
		return finalValue;
	}
}