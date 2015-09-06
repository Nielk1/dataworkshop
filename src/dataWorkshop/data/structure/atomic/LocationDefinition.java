package dataWorkshop.data.structure.atomic;

import javax.swing.tree.TreePath;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.RelativeTreePath;
import dataWorkshop.data.structure.ViewDefinitionElement;
import dataWorkshop.data.view.DataFrame;
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
public class LocationDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "LocationDefinition";
	final static String PATH_TAG = "path";

	private String path;

	/******************************************************************************
	*	Constructors
	*/
	public LocationDefinition()
	{
		this(new String());
	}

	public LocationDefinition(String path)
	{
		this.path = path;
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
		XMLSerializeFactory.setAttribute(context, PATH_TAG, getPath());
	}

	public void deserialize(Element context)
	{
		setPath(XMLSerializeFactory.getAttribute(context, PATH_TAG));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		String xpath = getPath();
		TreePath path = node.getRootStatement().translateXPathIntoTreePath(xpath);
		if (path == null) {
			validator.getValidatorOutput().error(node, Validator.LOCATION_PATH_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE.format(new Object[] {xpath}));
			return false;
		}
		return true;
	}

	public DataFrame translate(Compiler compiler, ViewDefinitionElement node, long bitOffset, DataFrame parent)
	{
		ViewDefinitionElement structure = (ViewDefinitionElement) node.getRootStatement().translateXPathIntoTreePath(getPath()).getLastPathComponent();
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
			dataFrame = (DataFrame) dataFrame.getChild(names[i]);
		}

		return dataFrame;
	}

	public String getPath()
	{
		return path;
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
}