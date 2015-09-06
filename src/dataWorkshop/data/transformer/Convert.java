package dataWorkshop.data.transformer;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataTransformer;
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
public class Convert extends DataTransformer
{

	final static String CLASS_NAME = "Convert";

	DataEncoding encoder;
	DataEncoding decoder;

	final static String ENCODER_TAG = "Encoder";
	final static String DECODER_TAG = "Decoder";

	/******************************************************************************
	 *	Constructors
	 */
	public Convert()
	{
		super();
	}

	public Convert(DataEncoding encoder, DataEncoding decoder)
	{
		super(encoder.getBitSize());
		this.encoder = encoder;
		this.decoder = decoder;
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
		XMLSerializeFactory.serialize(context, ENCODER_TAG, encoder);
		XMLSerializeFactory.serialize(context, DECODER_TAG, decoder);
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		//Element element = XMLSerializeFactory.getElement(context, ENCODER_TAG);
		encoder = (DataEncoding) XMLSerializeFactory.deserializeFirst(context, ENCODER_TAG);
		decoder = (DataEncoding) XMLSerializeFactory.deserializeFirst(context, DECODER_TAG);
		setBitSize(encoder.getBitSize());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public Data transform(Data data, long bitOffset, long bitSize) throws Exception
	{
		char[] charData = encoder.encode(data, bitOffset, bitSize);
		return decoder.decode(charData, 0, charData.length);
	}
}