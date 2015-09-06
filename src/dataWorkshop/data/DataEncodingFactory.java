package dataWorkshop.data;

import java.util.HashMap;

import dataWorkshop.data.encoding.IntegerEncoding;
import dataWorkshop.data.encoding.USAscii;
import dataWorkshop.logging.Logger;

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
public class DataEncodingFactory
{

	private static Logger logger = Logger.getLogger(DataEncodingFactory.class);
	private static HashMap dataEncodings = new HashMap();
	private HashMap namedDataEncodings = new HashMap();

	/**
	 *	Convenience members 
	 */
	public final static String BINARY = "Binary";

	public final static String HEX_8_UNSIGNED = "HEX_8_UNSIGNED";
	public final static String HEX_8_SIGNED = "HEX_8_SIGNED";
	public final static String HEX_16_BIG_UNSIGNED = "HEX_16_BIG_UNSIGNED";
	public final static String HEX_16_BIG_SIGNED = "HEX_16_BIG_SIGNED";
	public final static String HEX_16_LITTLE_UNSIGNED = "HEX_16_LITTLE_UNSIGNED";
	public final static String HEX_16_LITTLE_SIGNED = "HEX_16_LITTLE_SIGNED";
	public final static String HEX_32_BIG_UNSIGNED = "HEX_32_BIG_UNSIGNED";
	public final static String HEX_32_BIG_SIGNED = "HEX_32_BIG_SIGNED";
	public final static String HEX_32_LITTLE_UNSIGNED = "HEX_32_LITTLE_UNSIGNED";
	public final static String HEX_32_LITTLE_SIGNED = "HEX_32_LITTLE_SIGNED";

	public final static String DEC_8_UNSIGNED = "DEC_8_UNSIGNED";
	public final static String DEC_8_SIGNED = "DEC_8_SIGNED";
	public final static String DEC_16_BIG_UNSIGNED = "DEC_16_BIG_UNSIGNED";
	public final static String DEC_16_BIG_SIGNED = "DEC_16_BIG_SIGNED";
	public final static String DEC_16_LITTLE_UNSIGNED = "DEC_16_LITTLE_UNSIGNED";
	public final static String DEC_16_LITTLE_SIGNED = "DEC_16_LITTLE_SIGNED";
	public final static String DEC_32_BIG_UNSIGNED = "DEC_32_BIG_UNSIGNED";
	public final static String DEC_32_BIG_SIGNED = "DEC_32_BIG_SIGNED";
	public final static String DEC_32_LITTLE_UNSIGNED = "DEC_32_LITTLE_UNSIGNED";
	public final static String DEC_32_LITTLE_SIGNED = "DEC_32_LITTLE_SIGNED";

	public final static String US_ASCII = "US_ASCII";

	private static DataEncodingFactory instance;

	/******************************************************************************
	 *	Constructors
	 */
	private DataEncodingFactory()
	{
		add(BINARY, DataEncodingFactory.getDataEncoding(new IntegerEncoding(2, 1, false, false)));
		add(HEX_8_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 8, false, false)));
		add(HEX_8_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 8, false, true)));
		add(HEX_16_BIG_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 16, false, false)));
		add(HEX_16_BIG_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 16, false, true)));
		add(HEX_16_LITTLE_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 16, true, false)));
		add(HEX_16_LITTLE_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 16, true, true)));
		add(HEX_32_BIG_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 32, false, false)));
		add(HEX_32_BIG_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 32, false, true)));
		add(HEX_32_LITTLE_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 32, true, false)));
		add(HEX_32_LITTLE_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(16, 32, true, true)));

		add(DEC_8_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 8, false, false)));
		add(DEC_8_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 8, false, true)));
		add(DEC_16_BIG_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 16, false, false)));
		add(DEC_16_BIG_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 16, false, true)));
		add(DEC_16_LITTLE_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 16, true, false)));
		add(DEC_16_LITTLE_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 16, true, true)));
		add(DEC_32_BIG_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 32, false, false)));
		add(DEC_32_BIG_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 32, false, true)));
		add(DEC_32_LITTLE_UNSIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 32, true, false)));
		add(DEC_32_LITTLE_SIGNED, DataEncodingFactory.getDataEncoding(new IntegerEncoding(10, 32, true, true)));

		add(US_ASCII, DataEncodingFactory.getDataEncoding(new USAscii()));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public DataEncoding get(String name) {
		DataEncoding converter = (DataEncoding) namedDataEncodings.get(name);
		assert (converter != null);
		return converter;
	}

	public static DataEncodingFactory getInstance()
	{
		if (instance == null)
		{
			instance = new DataEncodingFactory();
		}
		return instance;
	}

	public static DataEncoding getDataEncoding(DataEncoding dataConverter)
	{
		DataEncoding converter = (DataEncoding) dataEncodings.get(dataConverter);
		//put the new dataconverter in the cache
		if (converter == null)
		{
			dataEncodings.put(dataConverter, dataConverter);
			logger.finest("DataEncodingCacheSize: " + dataEncodings.size() + " " + dataConverter.toString());
			return dataConverter;
		}
		return converter;
	}
	
	/******************************************************************************
		 *	Private Methods
		 */
		private void add(String name, DataEncoding converter)
		{
			if (namedDataEncodings.containsKey(name))
			{
				throw new RuntimeException(name + " was not unique");
			}
			namedDataEncodings.put(name, converter);
		}
}