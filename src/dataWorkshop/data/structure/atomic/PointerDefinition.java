package dataWorkshop.data.structure.atomic;

import javax.swing.tree.TreePath;

import org.w3c.dom.Element;

import dataWorkshop.data.DataNumber;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.number.SignedCount;
import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.RelativeTreePath;
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
public class PointerDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "PointerDefinition";

	final static String SIGNED_TAG = "signed";
	final static String LITTLE_ENDIAN_TAG = "littleEndian";
	final static String PATH_TAG = "path";

	boolean isSigned;
	boolean littleEndian;
	String path;

	/******************************************************************************
	*	Constructors
	*/
	public PointerDefinition()
	{
		this(new String());
	}

	public PointerDefinition(String path)
	{
		this(path, false, false);
	}

	public PointerDefinition(String path, boolean isSigned, boolean littleEndian)
	{
		this.path = path;
		this.isSigned = isSigned;
		this.littleEndian = littleEndian;
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
		XMLSerializeFactory.setAttribute(context, LITTLE_ENDIAN_TAG, isLittleEndian());
		XMLSerializeFactory.setAttribute(context, SIGNED_TAG, isSigned());
		XMLSerializeFactory.setAttribute(context, PATH_TAG, getPath());
	}

	public void deserialize(Element context)
	{
		setLittleEndian(XMLSerializeFactory.getAttributeAsBoolean(context, LITTLE_ENDIAN_TAG));
		setSigned(XMLSerializeFactory.getAttributeAsBoolean(context, SIGNED_TAG));
		setPath(XMLSerializeFactory.getAttribute(context, PATH_TAG));
	}

	/******************************************************************************
	  *	Public Methods
	  */
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		String xpath = getPath();
		TreePath path = node.getRootStatement().translateXPathIntoTreePath(xpath);
		if (path == null)
		{
			validator.getValidatorOutput().error(node, Validator.POINTER_PATH_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE.format(new Object[] { xpath }));
			return false;
		}
		return true;
	}

	public boolean isLittleEndian()
	{
		return littleEndian;
	}

	public void setLittleEndian(boolean littleEndian)
	{
		this.littleEndian = littleEndian;
	}

	public boolean isSigned()
	{
		return isSigned;
	}

	public void setSigned(boolean signed)
	{
		this.isSigned = signed;
	}

	/**
	 * :TRICKY:Martin Pape:Jun 19, 2003
	 *	Null pointers will be translated into new String(), because during serialization and during 
	 * validation compilation we assume a non-null value	
	 */
	public void setPath(String path)
	{
		if (path == null) {
			path = new String();
		}
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}

	public long evaluatePointer(Compiler compiler, ViewDefinitionElement node, long bitOffset, DataFrame parent)
	{
		CompilerOutput output = compiler.getCompilerOutput();
		DataFrame pointerFrame = translatePointer(compiler, node, bitOffset, parent);

		//Do some Error checks
		if (pointerFrame == null)
		{
			output.error(node, bitOffset, "No PointerFrame defined");
			return -1;
		}
		if (pointerFrame.getBitSize() > 64)
		{
			output.error(node, bitOffset, "PointerFrame too large (Max = 64 bits) " + pointerFrame);
			return -1;
		}
		if (pointerFrame.getBitSize() + pointerFrame.getBitOffset() > compiler.getData().getBitSize())
		{
			output.error(node, bitOffset, "PointerFrame lies outside of data " + pointerFrame);
			return -1;
		}

		DataNumber number = new DataNumber((int) pointerFrame.getBitSize(), isLittleEndian(), isSigned());
		long length = number.decode(compiler.getData(), pointerFrame.getBitOffset());
		
		output.info(node, bitOffset, "Evaluated " + pointerFrame + " using " + number + " to " + (new SignedCount(length)).toString(false));
		return length;
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private DataFrame translatePointer(Compiler compiler, ViewDefinitionElement node, long bitOffset, DataFrame parent)
	{
		if (getPath() == null)
		{
			return null;
		}

		ViewDefinitionElement structure = (ViewDefinitionElement) node.getRootStatement().translateXPathIntoTreePath(getPath()).getLastPathComponent();
		if (structure == null)
		{
			compiler.getCompilerOutput().error(node, bitOffset, "Invalid Pointer Field " + getPath());
			return null;
		}

		RelativeTreePath path = node.getPathTo(structure);
		if (path == null)
		{
			return null;
		}

		DataFrame dataFrame = parent;
		ViewDefinitionElement definitionNode = (ViewDefinitionElement) node.getParent();
		for (int i = 0; i < path.getUp(); i++)
		{
			if (!(definitionNode instanceof CaseStatement))
			{
				dataFrame = (DataFrame) dataFrame.getParent();
			}
			definitionNode = (ViewDefinitionElement) definitionNode.getParent();
		}
		String[] names = path.getPath();

		for (int i = 0; i < names.length; i++)
		{
			//This is possible
			if (dataFrame == null) {
				return null;
			}
			dataFrame = (DataFrame) dataFrame.getChild(names[i]);
		}

		return dataFrame;
	}
}
