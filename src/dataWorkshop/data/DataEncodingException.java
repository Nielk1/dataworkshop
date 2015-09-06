package dataWorkshop.data;

import dataWorkshop.LocaleStrings;

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
public class DataEncodingException extends Exception implements LocaleStrings
{
	/**
	 * The DataConverter which caused the exception
	 */
	DataEncoding source;
	
	/**
	 * DataConverterExceptions might be chained, as DataConverter can be chained too.
	 *	(e.g. IEEE754 DataConverter is a composite of several simpler DataConverters)
	 */
	DataEncodingException exception = null;

	/******************************************************************************
	 *	Constructors
	 */
	public DataEncodingException(DataEncoding source, String value, String min, String max)
	{
		super(DATA_CONVERTER_VALUE_OUTSIDE_RANGE_MESSAGE.format
					(
						new Object[] {value, min, max}
					)
				);
		this.source = source;
	}
	
	public DataEncodingException(DataEncoding source, String completeData, String invalidSubData, String min, String max)
		{
			super(DATA_CONVERTER_SUBVALUE_OUTSIDE_RANGE_MESSAGE.format
						(
							new Object[] {completeData, invalidSubData, min, max}
						)
					);
			this.source = source;
		}

	public DataEncodingException(DataEncoding source, char[] data, char illegalChar)
	{
		super(DATA_CONVERTER_ILLEGAL_CHAR_MESSAGE.format
			(
				new Object[] {new String(data), new Character(illegalChar), new Integer((int) illegalChar)}
			)
		);
		this.source = source;
	}
	
	public DataEncodingException(DataEncoding source, char[] data, DataEncodingException ex) {
		super(DATA_CONVERTER_MESSAGE.format
					(
						new Object[] {new String(data)}
					)
				);
		this.source = source;
		this.exception = ex;
	}

	/******************************************************************************
	*	Constructors
	*/
	public String getMessage() {
		String msg = super.getMessage();
		if (exception != null) {
			msg += exception.getMessage();
		}
		return msg;
	}

	public DataEncoding getSource()
	{
		return source;
	}
}
