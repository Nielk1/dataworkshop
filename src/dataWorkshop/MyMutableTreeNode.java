package dataWorkshop;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

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
public class MyMutableTreeNode implements MutableTreeNode
{
	protected ArrayList children;
	protected MutableTreeNode parent;
	protected boolean allowsChildren;

	/******************************************************************************
	*	Constructors
	*/
	public MyMutableTreeNode()
	{
	}

	/******************************************************************************
	*	MutableTreeNode Interface
	*/
	static public final Enumeration EMPTY_ENUMERATION = new Enumeration()
	{
		public boolean hasMoreElements()
		{
			return false;
		}
		public Object nextElement()
		{
			throw new NoSuchElementException("No more elements");
		}
	};

	public void insert(MutableTreeNode newChild, int childIndex)
	{
		MutableTreeNode oldParent = (MutableTreeNode) newChild.getParent();

		if (oldParent != null)
		{
			oldParent.remove(newChild);
		}
		newChild.setParent(this);
		if (children == null)
		{
			children = new ArrayList();
		}
		children.add(childIndex, newChild);
	}

	public void remove(int childIndex)
	{
		children.remove(childIndex);
	}

	public void setParent(MutableTreeNode newParent)
	{
		parent = newParent;
	}

	public TreeNode getParent()
	{
		return parent;
	}

	public TreeNode getChildAt(int index)
	{
		return (TreeNode) children.get(index);
	}

	public int getChildCount()
	{
		if (children == null)
		{
			return 0;
		}
		else
		{
			return children.size();
		}
	}

	public int getIndex(TreeNode child)
	{
		return children.indexOf(child);
	}

	public Enumeration children()
	{

		if (children == null)
		{
			return EMPTY_ENUMERATION;
		}
		else
		{
			return new DefaultEnumeration(children.toArray());
		}
	}

	public boolean getAllowsChildren()
	{
		return allowsChildren;
	}

	/**
	*	Not implemented.
	*/
	public void setUserObject(Object userObject)
	{
	}

	public void removeFromParent()
	{
		MutableTreeNode node = (MutableTreeNode) getParent();
		if (node != null)
		{
			node.remove(this);
			setParent(null);
		}
	}

	public void remove(MutableTreeNode child)
	{
		remove(getIndex(child));
	}

	public boolean isLeaf()
	{
		if (children == null)
		{
			return true;
		}
		return false;
	}

	public void add(MutableTreeNode child)
	{
		if (child != null && child.getParent() == this)
		{
			insert(child, getChildCount() - 1);
		}
		else
		{
			insert(child, getChildCount());
		}
	}

	/******************************************************************************
	*	Public Methods
	*/
	public void setAllowsChildren(boolean allowsChildren) {
		this.allowsChildren = allowsChildren;
	}
	
	public int[] getIndexPathFromRoot() {
		   ArrayList list = new ArrayList();
		   getIndexPathToRoot(list);
		   int[] indices = new int[list.size()];
		   Iterator it = list.iterator();
		   for (int i = list.size() - 1; i != -1; i--) {
			   indices[i] = ((Integer) it.next()).intValue();
		   }
		   return indices;
	   }
	   
   public MyMutableTreeNode getNode(int[] path) {
		MyMutableTreeNode node = this;
		for (int i = 0; i < path.length; i++) {
			node = (MyMutableTreeNode) node.getChildAt(path[i]);
		}
		return node;
   }
   
   public TreePath getPathToRoot() {
		  return new TreePath(getPathToRoot(this, 0));
	  }
	   
	/******************************************************************************
	*	Private Methods
	*/
	private void getIndexPathToRoot(ArrayList list) {
		   MyMutableTreeNode parent = (MyMutableTreeNode) getParent();
		   if (parent != null) {
			   list.add(Integer.valueOf(parent.getIndex(this)));
			   parent.getIndexPathToRoot(list);
		   }
	   }
	 
	   private TreeNode[] getPathToRoot(TreeNode node, int depth) {
		   TreeNode[] treeNodes;
		   if (node == null) {
			   treeNodes = new TreeNode[depth];
		   } else {
			   depth++;
			   treeNodes = getPathToRoot(node.getParent(), depth);
			   treeNodes[treeNodes.length - depth] = node;
		   }
		   return treeNodes;
	   }

	/******************************************************************************
		*	Enumeration Class
		*/
	public class DefaultEnumeration implements Enumeration
	{

		Object[] objects;
		int pos = 0;

		public DefaultEnumeration(Object[] o)
		{
			objects = o;
		}

		public boolean hasMoreElements()
		{
			if (pos != objects.length)
			{
				return true;
			}
			return false;
		}

		public Object nextElement()
		{
			pos++;
			return objects[pos - 1];
		}
	}
}
