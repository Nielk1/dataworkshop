package dataWorkshop.data.structure.compiler;

import java.text.MessageFormat;

import dataWorkshop.data.Data;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.StatementStructure;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.IntegerFormatFactory;

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
public class Compiler
{
	
	public static String COULD_NOT_RESOLVE_LOCATION_FRAME_ERROR = new String("Could not resolve Location Frame");
	
	public static MessageFormat COMPILER_WARNING_POINTER_STRUCTURE = new MessageFormat("{0} at {1} : Evaluating the pointer resulted in a number above the threshold. Is this number correct ?");
	public static MessageFormat COMPILER_WARNING_DELIMITED_STRUCTURE = new MessageFormat("Searching for the delimiter resulted in a bitsize above the threshold. Is this size correct ?");
	
	public static String ALIGNMENT_LESS_THAN_ONE_INFO = new String("Alignment < 1");
	
	public static MessageFormat ALIGN_RELATIVE_TO_INFO_MESSAGE = new MessageFormat("Align relative to {0}");
	public static MessageFormat FIELD_BITSIZE_INFO_MESSAGE = new MessageFormat("Field Bitsize {0}");
	public static MessageFormat CASE_DATA_RESOLVED_TO_INFO_MESSAGE = new MessageFormat("Case {0} resolved to {1}");
	
	Data data;
	CompilerOutput output;
	CompilerOptions compilerOptions;
	IntegerFormatFactory integerFormatFactory;

	/******************************************************************************
	 *	Constructors
	 */
	public Compiler(Data data, CompilerOutput output, CompilerOptions compilerOptions, IntegerFormatFactory integerFormatFactory)
	{
		this.data = data;
		this.output = output;
		this.compilerOptions = compilerOptions;
		this.integerFormatFactory = integerFormatFactory;
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public IntegerFormatFactory getIntegerFormatFactory() {
		return integerFormatFactory;
	}
	
	public CompilerOptions getCompilerOptions() {
		return compilerOptions;
	}
	
	public CompilerOutput getCompilerOutput()
	{
		return output;
	}

	public Data getData()
	{
		return data;
	}

	public DataFrame compile(RootStatement structure, long bitOffset)
	{
		DataFrame[] frames = structure.compile(this, bitOffset, null);
		if (frames == null) {
			return null;
		}
		return frames[0];
	}

	public DataFrame compileChildren(StatementStructure frameDefintion, long bitOffset, DataFrame parent)
	{
		DataFrame frame = (DataFrame) frameDefintion.render();
		frame.setBitOffset(bitOffset);
		long frameSize = 0;
		int childCount = frameDefintion.getChildCount();

		if (parent != null)
		{
			parent.add(frame);
		}
		for (int i = 0; i < childCount; i++)
		{
			ViewDefinitionElement childNode = (ViewDefinitionElement) frameDefintion.getChildAt(i);
			DataFrame[] childNodes = childNode.compile(this, bitOffset, frame);
			for (int ii = 0; ii < childNodes.length; ii++)
			{
				//if (childNode.isVisible()) {
				//    frame.add(childNodes[ii]);
				//}
				long childSize = childNodes[ii].getBitSize();
				frameSize += childSize;
				bitOffset += childSize;
			}
		}

		frame.setBitSize(frameSize);
		if (parent != null)
		{
			parent.setBitSize(parent.getBitSize() + frameSize);
		}
		return frame;
	}

	public void setArrayIndex(DataFrame[] frames)
	{
		if (frames.length > 1)
		{
			for (int i = 0; i < frames.length; i++)
			{
				frames[i].setArrayIndex(i);
			}
		}
	}
}