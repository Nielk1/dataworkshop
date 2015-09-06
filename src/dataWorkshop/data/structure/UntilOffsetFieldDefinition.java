package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.Utils;
import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
import dataWorkshop.data.structure.atomic.LengthDefinition;
import dataWorkshop.data.structure.atomic.LocationDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.UnsignedOffset;
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
public class UntilOffsetFieldDefinition extends AbstractDataEncodingFieldDefinition
{
	public final static String CLASS_NAME = "UntilOffsetFieldDefinition";
	final static String NAME = "Field Until Offset";

	private LengthDefinition length;
	private LocationDefinition locationDefinition;

	/******************************************************************************
	 *	Constructors
	 */
	public UntilOffsetFieldDefinition()
	{
		this(DEFAULT_LABEL);
	}

	public UntilOffsetFieldDefinition(String label)
	{
		this(label, new EncodingDefinition(), new StaticLengthDefinition(), new LocationDefinition());
	}

	public UntilOffsetFieldDefinition(String label, EncodingDefinition converterFieldDefinition, LengthDefinition length, LocationDefinition locationDefinition)
	{
		super(label, converterFieldDefinition);
		this.length = length;
		this.locationDefinition = locationDefinition;
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
		super.serialize(context);
		XMLSerializeFactory.serialize(context, getLengthDefinition());
		XMLSerializeFactory.serialize(context, getLocationDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setLengthDefinition((LengthDefinition) XMLSerializeFactory.deserializeFirst(context));
		setLocationDefinition((LocationDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String getShortDescription() {
			return getName();	
		}
		
	public String getName()
	{
		return NAME;
	}

	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & length.validate(validator, this);
		isValid = isValid & locationDefinition.validate(validator, this);
		return isValid;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		CompilerOutput output = compiler.getCompilerOutput();
		DataFrame locationFrame = getLocationDefinition().translate(compiler, this, bitOffset, parent);
		if (locationFrame == null)
		{
			output.error(this, bitOffset, Compiler.COULD_NOT_RESOLVE_LOCATION_FRAME_ERROR);
			return new DataFrame[0];
		}

		output.info(this, bitOffset, Compiler.ALIGN_RELATIVE_TO_INFO_MESSAGE.format(new Object[] {new UnsignedOffset(locationFrame.getBitOffset())}));

		long alignment = getLengthDefinition().getLength(compiler, this, bitOffset, parent);
		if (alignment < 1)
		{
			output.info(this, bitOffset, Compiler.ALIGNMENT_LESS_THAN_ONE_INFO);
			return new DataFrame[0];
		}

		long bitEnd = Utils.alignUp(bitOffset - locationFrame.getBitOffset(), alignment) + locationFrame.getBitOffset();

		DataFrame[] frames = new DataFrame[1];
		frames[0] = render();
		frames[0].setBitOffset(bitOffset);
		frames[0].setBitSize(bitEnd - bitOffset);

		output.info(this, bitOffset, Compiler.FIELD_BITSIZE_INFO_MESSAGE.format(new Object[] {new UnsignedOffset(bitEnd - bitOffset)}));

		parent.add(frames[0]);
		return frames;
	}

	public LengthDefinition getLengthDefinition()
	{
		return length;
	}

	public void setLengthDefinition(LengthDefinition length)
	{
		this.length = length;
	}

	public LocationDefinition getLocationDefinition()
	{
		return locationDefinition;
	}

	public void setLocationDefinition(LocationDefinition locationDefinition)
	{
		this.locationDefinition = locationDefinition;
	}
}