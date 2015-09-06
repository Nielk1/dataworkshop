package dataWorkshop.data.structure;

import java.util.ArrayList;

import org.w3c.dom.Element;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.structure.atomic.LengthDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.CompilerOptions;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.UnsignedCount;
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
public class RepeatStatement extends StatementStructure
{
	public final static String CLASS_NAME = "RepeatStatement";
	final static String NAME = "Repeat Statement";

	LengthDefinition length;

	/******************************************************************************
	 *	Constructors
	 */
	public RepeatStatement()
	{
		this(DEFAULT_LABEL);
	}

	public RepeatStatement(String label)
	{
		this(label, new StaticLengthDefinition());
	}

	public RepeatStatement(String label, LengthDefinition length)
	{
		super(label);
		this.length = length;
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
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setLengthDefinition((LengthDefinition) XMLSerializeFactory.deserializeFirst(context));
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
		return isValid;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		ArrayList renderedNodes = new ArrayList();
		CompilerOptions compilerOptions = compiler.getCompilerOptions();

		DataFrame frame;
		long len = getLengthDefinition().getLength(compiler, this, bitOffset, parent);

		if (compilerOptions.isPointerStructureThresholdEnabled() && len > compilerOptions.getPointerStructureThreshold())
		{
			Object[] args = new Object[2];
			args[0] = getLabel();
			args[1] = new UnsignedOffset(bitOffset);
			len = compiler.getCompilerOutput().changeValue(Compiler.COMPILER_WARNING_POINTER_STRUCTURE.format(args), getLabel() + "Length", len, DataWorkshop.getInstance().getUnsignedCount());
		}
		compiler.getCompilerOutput().info(this, bitOffset, "Final Length " + new UnsignedCount(len));

		if (len > 0)
		{
			for (long i = 0; i < len; i++)
			{
				frame = compiler.compileChildren(this, bitOffset, parent);
				renderedNodes.add(frame);
				bitOffset += frame.getBitSize();
			}
		}

		DataFrame[] frames = (DataFrame[]) renderedNodes.toArray(new DataFrame[0]);
		compiler.setArrayIndex(frames);
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
}