package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.SignedCount;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.CompilerOutput;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

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
public class PointerLengthDefinition implements LengthDefinition, XMLSerializeable
{
	public final static String CLASS_NAME = "PointerLengthDefinition";

	private PointerDefinition pointerDefinition;
	private ModificationDefinition modificationDefinition;

	/******************************************************************************
	*	Constructors
	*/
	public PointerLengthDefinition()
	{
		this(new PointerDefinition());
	}
	
	public PointerLengthDefinition(PointerDefinition pointer)
	{
		this(pointer, new ModificationDefinition());
	}

	public PointerLengthDefinition(PointerDefinition pointerDefinition, ModificationDefinition modificationDefinition)
	{
		this.pointerDefinition = pointerDefinition;
		this.modificationDefinition = modificationDefinition;
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
		XMLSerializeFactory.serialize(context, getPointerDefinition());
		XMLSerializeFactory.serialize(context, getModificationDefinition());
	}

	public void deserialize(Element context)
	{
		setPointerDefinition((PointerDefinition) XMLSerializeFactory.deserializeFirst(context));
		setModificationDefinition((ModificationDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	  *	Public Methods
	  */
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		boolean isValid = getPointerDefinition().validate(validator, node);
		isValid = isValid & getModificationDefinition().validate(validator, node);
		return isValid;
	}

	public long getLength(Compiler compiler, ViewDefinitionElement node, long bitOffset, DataFrame parent)
	{
		CompilerOutput output = compiler.getCompilerOutput();
		
		long lengthBefore = getPointerDefinition().evaluatePointer(compiler, node, bitOffset, parent);
		if (lengthBefore == -1)
		{
			return -1;
		}
		
		long lengthAfter = Math.max(0, modificationDefinition.calculate(lengthBefore));

		output.info(node, bitOffset,"After modification " + (new SignedCount(lengthAfter)).toString(false));
		return lengthAfter;
	}

	public ModificationDefinition getModificationDefinition()
	{
		return modificationDefinition;
	}

	public void setModificationDefinition(ModificationDefinition modificationDefinition)
	{
		this.modificationDefinition = modificationDefinition;
	}

	public PointerDefinition getPointerDefinition()
	{
		return pointerDefinition;
	}

	public void setPointerDefinition(PointerDefinition pointerDefinition)
	{
		this.pointerDefinition = pointerDefinition;
	}
}
