package dataWorkshop.data.encoding;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.data.DataEncodingFactory;
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
public class IEEE754Decoded extends IEEE754
{
	public final static String CLASS_NAME = "IEEE754DecodedEncoding";
	public final static String NAME = "Real-Decoded";
	//these are the fraction digits of the FractionConverter
	final static String MANTISSE_DIGITS_TAG = "mantisseDigits";

	public static int MANTISSE_DIGITS_MIN = 1;
	public static int MANTISSE_DIGITS_MAX = 20;

	transient IntegerEncoding exponentConverter;
	transient FractionEncoding mantisseConverter;

	/******************************************************************************
	 *	Constructors
	 */
	public IEEE754Decoded()
	{
		this(10, IEEE754.SHORT_REAL, true, 5);
	}

	public IEEE754Decoded(int radix, String precision, boolean littleEndian, int mantisseDigits)
	{
		this(radix, ((Integer) NAME_TO_TYPE_MAPPING.get(precision)).intValue(), littleEndian, mantisseDigits);
	}

	public IEEE754Decoded(int radix, int precision, boolean littleEndian, int mantisseDigits)
	{
		super(NAME, radix, precision, littleEndian);
		setMantisseDigits(mantisseDigits);
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
		XMLSerializeFactory.setAttribute(context, MANTISSE_DIGITS_TAG, getMantisseDigits());
	}

	public void deserialize(Element context)
	{
		removeAllProperties();
		super.deserialize(context);
		setMantisseDigits(XMLSerializeFactory.getAttributeAsInt(context, MANTISSE_DIGITS_TAG));
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

		int bitOffset = 0;
		System.arraycopy(encode(), 0, chars, dot, getDotSize());
		if (d.booleanValue(bitOffset))
		{
			chars[dot] = '-';
		}
		else
		{
			chars[dot] = '+';
		}
		dot++;
		bitOffset++;
		int exponentValue = d.intValue(bitOffset, getExponentBitSize());
		double mantisseValue = d.doubleValue(bitOffset + getExponentBitSize(), 0, getMantisseBitSize());

		if (getRealType() == IEEE754.SHORT_REAL || getRealType() == IEEE754.LONG_REAL)
		{
			//This means the number is zero
			if (exponentValue == 0 && mantisseValue == 0)
			{
			}
			//Number is normalized
			//We have to add one to the mantisse, because of the normalization the leading bit (which is always 1) was ommited
			else if (exponentValue != 0 && mantisseValue == 0)
			{
				//subtract bias
				exponentValue -= Data.maxSignedNumber(getExponentBitSize());
				mantisseValue += 1;
			}
			//Number is not normalized
			else if (exponentValue == 0 && mantisseValue != 0)
			{
				//subtract bias
				exponentValue -= Data.maxSignedNumber(getExponentBitSize());
			}
			//Number is normalized
			//We have to add one to the mantisse, because of the normalization the leading bit (which is always 1) was ommited
			else if (exponentValue != 0 && mantisseValue != 0)
			{
				//subtract bias
				exponentValue -= Data.maxSignedNumber(getExponentBitSize());
				mantisseValue += 1;
			}
		}
		else if (getRealType() == IEEE754.TEMPORARY_REAL)
		{
			exponentValue -= Data.maxSignedNumber(getExponentBitSize());
		}
		else
		{
			throw new RuntimeException("Invalid RealType: " + getRealType());
		}

		double logConversion = 1 / Math.log(getRadix());
		double newExponentValue2 = (logConversion * Math.log(Math.exp(exponentValue * Math.log(2))));

		//We need an integer exponent
		int newExponentValue = (int) Math.floor(newExponentValue2);
		mantisseValue = mantisseValue * Math.exp((newExponentValue2 - newExponentValue) * Math.log(getRadix()));

		//normalize number, one digits has to be before the ','
		while (0 > mantisseValue)
		{
			mantisseValue = mantisseValue * getRadix();
			newExponentValue--;
		}
		while (mantisseValue > getRadix() - 1 && !Double.isInfinite(mantisseValue))
		{
			mantisseValue = mantisseValue / getRadix();
			newExponentValue++;
		}

		System.arraycopy(mantisseConverter.encode(mantisseValue), 0, chars, dot, mantisseConverter.getDotSize());
		dot += mantisseConverter.getDotSize();
		// skip 'e'
		dot++;

		if (newExponentValue < 0)
		{
			newExponentValue = Math.abs(newExponentValue);
			chars[dot] = '-';
		}
		else
		{
			chars[dot] = '-';
		}
		dot++;
		exponentConverter.encode(new Data(exponentConverter.getBitSize(), newExponentValue), 0, chars, dot);
	}

	public Data decode(char[] chars) throws DataEncodingException
	{
		Data d = new Data();
		try
		{
			boolean sign = decodeSign(chars, 0);
			int exponent = (exponentConverter.decode(chars, 1 + mantisseConverter.getDotSize() + 2)).intValue();
			if (decodeSign(chars, 1 + mantisseConverter.getDotSize() + 1))
			{
				exponent = 0 - exponent;
			}
			double mantisse = mantisseConverter.getValue(chars, 1);
			//In these cases Java can do all the work for us
			if (getRealType() == IEEE754.SHORT_REAL || getRealType() == IEEE754.LONG_REAL)
			{
				if (sign)
				{
					mantisse = 0 - mantisse;
				}
				double value = mantisse * Math.exp(exponent * Math.log(getRadix()));
				if (getRealType() == IEEE754.SHORT_REAL)
				{
					d = new Data(getBitSize(), Float.floatToRawIntBits((float) value));
				}
				else
				{
					d = new Data(getBitSize(), Double.doubleToRawLongBits(value));
				}
			}
			else if (getRealType() == IEEE754.TEMPORARY_REAL)
			{
				d = new Data(1, sign);
				//get the exponent for the binary base
				double logConversion = 1 / Math.log(2);
				double newExponentValue2 = (logConversion * Math.log(Math.exp(exponent * Math.log(getRadix()))));
				//We need an integer exponent
				int newExponentValue = (int) Math.floor(newExponentValue2);
				mantisse = mantisse * Math.exp((newExponentValue2 - newExponentValue) * Math.log(2));
				//normalize number, one digits has to be before the ','
				while (0 > mantisse)
				{
					mantisse = mantisse * 2;
					newExponentValue--;
				}
				while (mantisse > 2)
				{
					mantisse = mantisse / 2;
					newExponentValue++;
				}
				d.append(new Data(getExponentBitSize(), newExponentValue));
				Data mantisseData = Data.toData(Double.doubleToRawLongBits(mantisse), 1 + EXPONENT_BIT_SIZE[IEEE754.LONG_REAL], MANTISSE_BIT_SIZE[IEEE754.LONG_REAL]);
				mantisseData.setBitSize(getMantisseBitSize());
				d.append(mantisseData);
			}
			else
			{
				throw new RuntimeException("Invalid RealType: " + getRealType());
			}
		}
		catch (DataEncodingException e)
		{
			//create a new DataConverterException for this DataConverter and chain it to the one caused by the simpler DataConverter
			char[] exceptionChars = new char[getDotSize()];
			System.arraycopy(exceptionChars, 0, chars, 0, getDotSize());
			throw new DataEncodingException(this, exceptionChars, e);
		}
		if (isLittleEndian())
		{
			d.swapByteOrder();
		}
		return d;
	}

	public int getMantisseDigits()
	{
		return mantisseConverter.getFractionDigits();
	}

	private void setMantisseDigits(int digits)
	{
		int manBitSize = getMantisseBitSize();
		/*
		 * NOTE (MPA 2002-09-14)
		 *
		 * in these cases the real is normalized and we gain one bit by ommitting the first set bit
		 * so we must size the converter a bit (no pun intended) bigger
		 */
		if (getRealType() == IEEE754.SHORT_REAL || getRealType() == IEEE754.LONG_REAL)
		{
			manBitSize++;
		}
		int intBitSize = 1;
		int digit = getRadix() - 1;
		while (Data.maxSignedNumber(intBitSize) < digit)
		{
			intBitSize++;
		}
		mantisseConverter = (FractionEncoding) DataEncodingFactory.getDataEncoding(new FractionEncoding(getRadix(), intBitSize, false, getBitSize() - intBitSize, digits));
	}

	public boolean isInputPoint(int dot)
	{
		return encode()[dot] == '-';
	}

	public int hashCode()
	{
		return NAME.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof IEEE754Decoded)
		{
			IEEE754Decoded converter = (IEEE754Decoded) obj;
			if (this.getMantisseDigits() != converter.getMantisseDigits())
			{
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private void recalc()
	{
		addProperty(MANTISSE_DIGITS_TAG, Integer.toString(getMantisseDigits()));
		exponentConverter = (IntegerEncoding) DataEncodingFactory.getDataEncoding(new IntegerEncoding(getRadix(), getExponentBitSize(), false, false));
		char[] empty = new char[1 + mantisseConverter.getDotSize() + 2 + exponentConverter.getDotSize()];
		for (int i = 0; i < empty.length; i++)
		{
			empty[i] = '-';
		}
		empty[1 + mantisseConverter.getDotSize()] = 'e';
		setEmptyChars(empty);
	}
}