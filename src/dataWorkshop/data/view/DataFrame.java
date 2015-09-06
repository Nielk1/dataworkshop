package dataWorkshop.data.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Element;

import dataWorkshop.MyMutableTreeNode;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
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
public class DataFrame extends MyMutableTreeNode implements XMLSerializeable
{

	public final static String CLASS_NAME = "DataFrame";

	public final static String ARRAY_INDEX_TAG = "arrayIndex";
	public final static String BIT_OFFSET_TAG = "bitOffset";
	public final static String BIT_SIZE_TAG = "bitSize";
	public final static String CHILDREN_TAG = "Children";
	public final static String BIT_START_TAG = "bitStart";
	public final static String BIT_END_TAG = "bitEnd";
	public final static String LABEL_TAG = "label";

	/**
	 *  Init values used by other DataFrames as defaults. I put them here to keep
	 *  them central and easy to change. I have not put them into EditorOptions as this
	 *  would mean an unnecessary coupling of DataClasses and high level configuration options
	 */
	public final static DataEncoding DEFAULT_DATA_CONVERTER = DataEncodingFactory.getInstance().get(DataEncodingFactory.BINARY);
	public final static String DEFAULT_LABEL = "new";

	//	This has to be a valid xml name
	private String label;
	private long bitSize = 0;
	private long bitOffset = 0;

	/**
	 *  If this DataFrame is part of an array of similar DataFrames this is the array index.
	 *  -1 means no array.
	 */
	private int arrayIndex = -1;
	private transient boolean visible = true;

	/******************************************************************************
	 *	Constructors
	 */
	public DataFrame()
	{
		this(true);
	}

	public DataFrame(boolean allowsChildren)
	{
		this(DEFAULT_LABEL, 0, 0, allowsChildren);
	}

	public DataFrame(String label, long bitOffset, long bitSize)
	{
		this(label, bitOffset, bitSize, true);
	}

	public DataFrame(String label, long bitOffset, long bitSize, boolean allowsChildren)
	{
		this.allowsChildren = allowsChildren;
		if (allowsChildren)
		{
			children = new ArrayList();
		}
		this.label = label;
		this.bitOffset = bitOffset;
		this.bitSize = bitSize;
	}

	/******************************************************************************
	 *	XMLSerializeable
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		XMLSerializeFactory.setAttribute(context, BIT_OFFSET_TAG, getBitOffset());
		XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, getBitSize());
		XMLSerializeFactory.setAttribute(context, LABEL_TAG, getLabel());
		XMLSerializeFactory.setAttribute(context, ARRAY_INDEX_TAG, getArrayIndex());

		if (allowsChildren)
		{
			XMLSerializeFactory.serialize(context, CHILDREN_TAG, getChildren());
		}
	}

	public void deserialize(Element context)
	{
		setBitOffset(XMLSerializeFactory.getAttributeAsLong(context, BIT_OFFSET_TAG));
		setBitSize(XMLSerializeFactory.getAttributeAsLong(context, BIT_SIZE_TAG));
		setLabel(XMLSerializeFactory.getAttribute(context, LABEL_TAG));
		setArrayIndex(XMLSerializeFactory.getAttributeAsInt(context, ARRAY_INDEX_TAG));

		if (allowsChildren)
		{
			setChildren((DataFrame[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, CHILDREN_TAG)).toArray(new DataFrame[0]));
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
	
	public int[] getPath()
	{
		ArrayList list = new ArrayList();
		getPath(list);
		int[] path = new int[list.size()];
		for (int i = path.length - 1; i != -1; i--) {
			path[i] = ((Integer) list.remove(0)).intValue();
		}
		return path;
	}
	
	public DataFrame getChild(int[] path) {
		DataFrame frame = this;
		for (int i = 0; i < path.length; i++) {
			frame = (DataFrame) frame.getChildAt(path[i]);
			if (frame == null) {
				return null;
			}
		}
		return frame;
	}

	public boolean isField()
	{
		return isLeaf();
	}

	public int hashCode()
	{
		return label.hashCode();
	}

	public boolean equals(Object o)
	{
		if (o instanceof DataFrame)
		{
			DataFrame node = (DataFrame) o;
			if (!label.equals(node.label))
			{
				return false;
			}
			if (bitSize != node.bitSize)
			{
				return false;
			}
			if (bitOffset != node.bitOffset)
			{
				return false;
			}
			if (arrayIndex != node.arrayIndex)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public void replace(DataFrame node)
	{
		bitSize = node.bitSize;
		bitOffset = node.bitOffset;
		label = node.label;
		arrayIndex = node.arrayIndex;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public BitRange getBitRange()
	{
		return new BitRange(bitOffset, bitSize);
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public int getArrayIndex()
	{
		return arrayIndex;
	}

	public void setArrayIndex(int arrayIndex)
	{
		this.arrayIndex = arrayIndex;
	}

	public long getBitSize()
	{
		return bitSize;
	}

	public void setBitSize(long bitSize)
	{
		this.bitSize = bitSize;
	}

	public long getBitOffset()
	{
		return bitOffset;
	}

	public void setBitOffset(long bitOffset)
	{
		this.bitOffset = bitOffset;
	}

	public String toString()
	{
		return getLabel();
		//return renderLabel() + " (BitOffset: " + getBitOffset() + " BitSize: " + getBitSize() + ")";
	}

	public DataFrame getRootFrame()
	{
		DataFrame node = (DataFrame) getParent();
		if (node != null)
		{
			return node.getRootFrame();
		}
		return this;
	}

	public DataFrame getChild(String name)
	{
		Iterator it = children.iterator();
		while (it.hasNext())
		{
			DataFrame child = (DataFrame) it.next();
			if (child.getLabel().equals(name))
			{
				return child;
			}
		}
		return null;
	}

	public DataFrame[] getChildren()
	{
		DataFrame[] children = new DataFrame[getChildCount()];
		for (int i = 0; i < children.length; i++)
		{
			children[i] = (DataFrame) getChildAt(i);
		}
		return children;
	}

	public void setChildren(DataFrame[] frames)
	{
		DataFrame[] oldChildren = getChildren();
		for (int i = 0; i < oldChildren.length; i++)
		{
			remove(oldChildren[i]);
		}

		for (int i = 0; i < frames.length; i++)
		{
			add(frames[i]);
		}
	}

	public DataField bitToField(long bitOffset)
	{
		return bitToField(bitOffset, this);
	}

	public DataField getField(BitRange bitRange)
	{
		DataField field = bitToField(bitRange.getStart());
		if (field != null)
		{
			if (field.getBitRange().contains(bitRange))
			{
				return field;
			}
		}
		return null;
	}

	/**
	 *  return all Frames which lie in the give selection
	 */
	public DataFrame[] getIntersectingFrames(BitRange bitRange)
	{
		ArrayList fields = new ArrayList();

		getIntersectingFrames(bitRange, this, fields);

		DataFrame[] f = new DataFrame[fields.size()];
		for (int i = 0; i < f.length; i++)
		{
			f[i] = (DataFrame) fields.get(i);
		}

		return f;
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected DataField bitToField(long bitOffset, DataFrame node)
	{
		if (bitOffset >= node.getBitOffset())
		{
			if (node.getAllowsChildren())
			{
				for (int i = 0; i < node.getChildCount(); i++)
				{
					DataField field = bitToField(bitOffset, (DataFrame) node.getChildAt(i));
					if (field != null)
					{
						return field;
					}
				}
			}
			else if (bitOffset >= node.getBitOffset() && bitOffset < node.getBitOffset() + node.getBitSize())
			{
				return (DataField) node;
			}
		}
		return null;
	}

	protected void getIntersectingFrames(BitRange bitRange, DataFrame frame, ArrayList frames)
	{
		DataFrame node;
		for (int i = 0; i < frame.getChildCount(); i++)
		{
			node = (DataFrame) frame.getChildAt(i);
			if (node.getBitRange().hasIntersection(bitRange))
			{
				frames.add(node);
			}
			if (node.getAllowsChildren())
			{
				getIntersectingFrames(bitRange, (DataFrame) node, frames);
			}
		}
	}
	
	/******************************************************************************
	 *	Private Methods
	 */
	private void getPath(ArrayList list) {
		DataFrame parent = (DataFrame) getParent();
		if (parent != null) {
			list.add(new Integer(parent.getIndex(this)));
			parent.getPath(list);
		}	
	}	
}