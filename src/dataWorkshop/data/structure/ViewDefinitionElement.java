package dataWorkshop.data.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.tree.TreePath;

import org.w3c.dom.Element;

import dataWorkshop.MyMutableTreeNode;
import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.DataNumber;
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
public abstract class ViewDefinitionElement extends MyMutableTreeNode implements XMLSerializeable
{
	/**
	 *	We use the label as an xml attribute value so we must not use the following chars 
	 */
	public final static char[] ILLEGAL_LABEL_CHARS = { ' ', '<', '>', '/', '(', ')' };

	/**
	 *  Init values used by other DefinitionNodes as defaults. I put them here to keep
	 *  them central and easy to change. I have not put them into EditorOptions as this
	 *  would mean an unnecessary coupling of DataClasses and high level configuration options
	 */
	public final static DataEncoding DEFAULT_DATA_CONVERTER = DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY);
	public final static DataNumber DEFAULT_DATA_NUMBER = DataNumber.getDataNumber(8, false, false);
	public final static int DEFAULT_GRANULARTIY = 8;
	public final static String DEFAULT_LABEL = "new";
	public final static Data DEFAULT_SEARCH_DATA = new Data();

	final static String LABEL_TAG = "label";

	final static String CHILDREN_TAG = "Children";

	//	This has to be a valid xml name
	private String label;

	/******************************************************************************
	 *	Constructors
	 */
	public ViewDefinitionElement(boolean b)
	{
		this(b, "new");
	}

	public ViewDefinitionElement(boolean b, String label)
	{
		this.label = label;
		allowsChildren = b;
		if (allowsChildren)
		{
			children = new ArrayList();
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	/*
	 *  We have put all the XML code here because of we would do it using inheritance
	 *  we would have redundant code (e.g. DelimitedFrame and DelimitedField both need
	 *  simialr serializing/deserializing code)
	 */
	public void serialize(Element context)
	{
		XMLSerializeFactory.setAttribute(context, LABEL_TAG, getLabel());

		if (allowsChildren)
		{
			XMLSerializeFactory.serialize(context, CHILDREN_TAG, getDefinitionNodeChildren());
		}
	}

	public void deserialize(Element context)
	{
		setLabel(XMLSerializeFactory.getAttribute(context, LABEL_TAG));

		/**
		 *  Children must be deserialized before ConditionalDefinitionInterface
		 *  as the Names in ConditionalDefinitionInterface must be valid child labels
		 *
		 */
		if (allowsChildren)
		{
			setDefinitionNodeChildren((ViewDefinitionElement[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, CHILDREN_TAG)).toArray(new ViewDefinitionElement[0]));
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String serialize()
	{
		return XMLSerializeFactory.getInstance().serialize(this);
	}

	public void deserialize(String xml)
	{
		XMLSerializeFactory.getInstance().deserialize(xml, this, false);
	}

	abstract public String getName();

	abstract public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent);

	/**
	 *	A short description of this structure element used to display in the treeview 
	 */
	abstract public String getShortDescription();

	public boolean validate(Validator validator)
	{
		boolean isValid = true;		
		//First check for invalid chars
		//
		StringBuffer invalidChars = new StringBuffer();
		String text = getLabel();
		for (int i = 0; i < ILLEGAL_LABEL_CHARS.length; i++)
		{
			if (text.indexOf(ILLEGAL_LABEL_CHARS[i]) != -1)
			{
				invalidChars.append(ILLEGAL_LABEL_CHARS[i]);
			}
		}

		if (invalidChars.length() != 0)
		{
			validator.getValidatorOutput().error(this, getLabel() + " is not a valid name. It contains the invalid characters '" + invalidChars + "'");
			isValid = false;
		}
		
		//Then check if another child of this parent uses the same label
		//	Do not flag the child which uses the label first
		//
		ViewDefinitionElement parent = (ViewDefinitionElement) getParent();
		boolean labelNotUnique = false;
		if (parent != null) {
			int index = parent.getIndex(this);
			for (int i = 0; i < index; i++) {
				if ( (((ViewDefinitionElement) parent.getChildAt(i)).getLabel().equals(text)) ) {
					labelNotUnique = true;
				}
			}
		}
		
		if (labelNotUnique)
		{
			validator.getValidatorOutput().error(this, "The Name " + getLabel() + " is not unique in the context of " + parent.getLabel() + "");
			isValid = false;
		}

		int max = getChildCount();
		for (int i = 0; i < max; i++)
		{
			isValid = isValid & ((ViewDefinitionElement) getChildAt(i)).validate(validator);
		}
		return isValid;
	}

	public String getXPath()
	{
		String xPath = getLabel();
		ViewDefinitionElement parent = (ViewDefinitionElement) getParent();
		while (parent != null && parent.getParent() != null)
		{
			xPath = parent.getLabel() + "/" + xPath;
			parent = (ViewDefinitionElement) parent.getParent();
		}
		return xPath;
	}

	/**
	 *	The given xpathname is used to construct a corresponding TreePath
	 *  The xPathName is relative to this node; 
	 */
	public TreePath translateXPathIntoTreePath(String xPathName)
	{
		if (xPathName.length() == 0) {
			return null;
		}
		StringTokenizer tok = new StringTokenizer(xPathName, "/");
		String[] namePath = new String[tok.countTokens()];
		for (int i = 0; i < namePath.length; i++)
		{
			namePath[i] = tok.nextToken();
		}

		Object[] nodePath = new Object[namePath.length];
		ViewDefinitionElement currentNode = this;
		for (int i = 0; i < nodePath.length; i++)
		{
			currentNode = currentNode.getChildStructure(namePath[i]);
			nodePath[i] = currentNode;
			if (currentNode == null) {
				return null;
			}
		}

		return new TreePath(nodePath);
	}

	abstract public DataFrame render();

	public String toString()
	{
		return label + " [ " + getShortDescription() + " ]";
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public ViewDefinitionElement getChildStructure(String childName)
	{
		Iterator it = children.iterator();
		while (it.hasNext())
		{
			ViewDefinitionElement structure = (ViewDefinitionElement) it.next();
			if (structure.getLabel().equals(childName))
			{
				return structure;
			}
		}
		return null;
	}

	public ViewDefinitionElement getChildStructure(String[] childNames)
	{
		ViewDefinitionElement structure = this;
		for (int i = 0; i < childNames.length; i++)
		{
			structure = structure.getChildStructure(childNames[i]);
			if (structure == null)
			{
				return null;
			}
		}
		return structure;
	}

	public void render(DataFrame node)
	{
		node.setLabel(getLabel());
	}

	public ViewDefinitionElement[] getDefinitionNodeChildren()
	{
		if (children == null)
		{
			return new ViewDefinitionElement[0];
		}
		ViewDefinitionElement[] nodes = new ViewDefinitionElement[children.size()];
		for (int i = 0; i < nodes.length; i++)
		{
			nodes[i] = (ViewDefinitionElement) children.get(i);
		}
		return nodes;
	}

	public void setDefinitionNodeChildren(ViewDefinitionElement[] children)
	{
		ViewDefinitionElement[] oldChildren = getDefinitionNodeChildren();
		for (int i = 0; i < oldChildren.length; i++)
		{
			remove(oldChildren[i]);
		}
		for (int i = 0; i < children.length; i++)
		{
			add(children[i]);
		}
	}

	public DataFieldDefinition[] getPreviousFields()
	{
		if (getParent() == null)
		{
			return new DataFieldDefinition[0];
		}
		ArrayList list = new ArrayList();
		getRootStatement().getPreviousDefinitionNodes(list, getIndexPathFromRoot(), 0);
		int i = 0;
		while (i < list.size())
		{
			Object o = list.get(i);
			if (o instanceof StatementStructure)
			{
				list.remove(o);
			}
			else
			{
				i++;
			}
		}
		return (DataFieldDefinition[]) list.toArray(new DataFieldDefinition[0]);
	}

	public ViewDefinitionElement[] getPreviousDefinitionNodes()
	{
		if (getParent() == null)
		{
			return new ViewDefinitionElement[0];
		}
		ArrayList list = new ArrayList();
		getRootStatement().getPreviousDefinitionNodes(list, getIndexPathFromRoot(), 0);
		return (ViewDefinitionElement[]) list.toArray(new ViewDefinitionElement[0]);
	}

	public ViewDefinitionElement getDefinitionNode(int[] indices)
	{
		if (indices == null || indices.length == 0)
		{
			return null;
		}
		ViewDefinitionElement node = getRootStatement();
		for (int i = 0; i < indices.length; i++)
		{
			node = (ViewDefinitionElement) node.getChildAt(indices[i]);
		}
		return node;
	}

	public RootStatement getRootStatement()
	{
		ViewDefinitionElement node = (ViewDefinitionElement) getParent();
		if (node != null)
		{
			return (RootStatement) node.getRootStatement();
		}
		return (RootStatement) this;
	}

	/**
	 *  Get the path from this node to the argument node.
	 *  If this is not possible, because the live in different trees return null;
	 *  the first int is the number of steps to travel upwards
	 *  the other ints
	 *  negative indices mean move downwards, positive indices means move upwards
	 */
	public RelativeTreePath getPathTo(ViewDefinitionElement node)
	{
		if (node == null)
		{
			return null;
		}
		int[] indices1 = getIndexPathFromRoot();
		int[] indices2 = node.getIndexPathFromRoot();

		//decide on the common subtree live in
		int start = 0;
		while (start < indices2.length && indices1[start] == indices2[start])
		{
			start++;
		}

		//We can ignore the last path component as this points to the node itself
		//and is not used in upwards tree traversel
		int up = indices1.length - (start + 1);
		String[] path = new String[(indices2.length - start)];
		ViewDefinitionElement parent = getRootStatement();
		for (int i = 0; i < start; i++)
		{
			parent = (ViewDefinitionElement) parent.getChildAt(indices1[i]);
		}

		int index = 0;
		for (int i = start; i < indices2.length; i++)
		{
			parent = (ViewDefinitionElement) parent.getChildAt(indices2[i]);
			path[index] = parent.getLabel();
			index++;
		}

		/*
		int index = path.length - 1;
		DefinitionNode parent = node.getParent();
		for (int i = indices2.length - 1; i >= start; i--) {
		    
		    path[index] = indices2[i];
		    index--;
		}
		*/

		return new RelativeTreePath(up, path);
	}

	protected void getPreviousDefinitionNodes(ArrayList list, int[] pathFromRoot, int depth)
	{
		int index = pathFromRoot[depth];
		//If the parent is a conditionalStructure we ignore the other
		// children as they are not used if this child was choosen
		if (!(this instanceof CaseStatement))
		{
			for (int i = 0; i < index; i++)
			{
				ViewDefinitionElement child = (ViewDefinitionElement) getChildAt(i);
				list.add(child);
				child.getAllChildren(list);
			}
		}
		depth++;
		if (depth < pathFromRoot.length)
		{
			ViewDefinitionElement child = (ViewDefinitionElement) getChildAt(index);
			list.add(child);
			child.getPreviousDefinitionNodes(list, pathFromRoot, depth);
		}
	}

	/**
	 *  Get all nodes that having this node as an anchestor
	 *  The nodes are sorted from top to bottom.
	 */
	protected void getAllChildren(ArrayList list)
	{
		ViewDefinitionElement[] children = getDefinitionNodeChildren();
		for (int i = 0; i < children.length; i++)
		{
			list.add(children[i]);
			children[i].getAllChildren(list);
		}
	}
}