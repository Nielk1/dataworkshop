package dataWorkshop.number;

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
import dataWorkshop.data.structure.ViewDefinitionElement;

	/**
	 *	We dont want the comparable interface in the Structure class as the comparasion is
	 * based on the XPath name and thus more closely tied to JTable class 
	 * 
	 * Furthermore we can easily use the toString of this class to customize the rendering process
	 * instead of creating a custom renderer class for Structure 
	 * 
	 * :TRICKY:Martin Pape:Jun 14, 2003
	 * 
	 * We want the table to display the XPath name of the Structure node but we need to keep
	 * the structure object in the table because the XPath name might not be unique (e.g. in case two nodes
	 * have the same label)
	 */
	public class StructureWrapper implements Comparable
		{

			ViewDefinitionElement structure;

			public StructureWrapper(ViewDefinitionElement structure)
			{
				this.structure = structure;
			}

			public ViewDefinitionElement getStructure() {
				return structure;
			}

			public String toString() {
				return structure.getXPath();
			}

			public int compareTo(Object o)
			{
				if (o instanceof StructureWrapper) {
					return this.toString().compareTo(o.toString());
				}
				return -1;
			}
		}
