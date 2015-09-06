package dataWorkshop.gui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.Action;
import javax.swing.JMenu;

import org.w3c.dom.Element;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.DataTransformation;
import dataWorkshop.gui.data.DataModel;
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
public class DataTransformationMenu implements XMLSerializeable
{
	final static String CLASS_NAME = "DataTransformationMenu";

	final static String NAME_TAG = "name";

	final static String SUBMENU_TAG = "SubMenu";
	final static String TRANSFORMATION_TAG = "Transformation";

	TreeMap menuMap = new TreeMap();

	/******************************************************************************
	 *	Constructors
	 */
	public DataTransformationMenu()
	{
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void add(String menuPath, String transformerName)
	{
		String[] s = new String[1];
		s[0] = menuPath;
		add(s, transformerName);
	}

	public void add(String[] menuPath, String transformerName)
	{
		TreeMap map = menuMap;
		for (int i = 0; i < menuPath.length; i++)
		{
			if (!map.containsKey(menuPath[i]))
			{
				map.put(menuPath[i], new TreeMap());
			}
			map = (TreeMap) map.get(menuPath[i]);
		}

		map.put(transformerName, transformerName);
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
		serialize(context, menuMap);
	}

	public void deserialize(Element context)
	{
		menuMap = new TreeMap();
		deserialize(context, menuMap);
	}

	/******************************************************************************
	*	Public Methods
	*/
	public void populateCalculatorsMenu(JMenu menu, DataModel dataModel, HashMap actions)
	{
		populateCalculatorsMenu(menu, dataModel, actions, menuMap);
	}

	private void populateCalculatorsMenu(JMenu menu, DataModel dataModel, HashMap actions, TreeMap map)
	{
		Iterator it = map.keySet().iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			Object value = map.get(key);
			if (value instanceof TreeMap)
			{
				JMenu subMenu = new JMenu(key);
				menu.add(subMenu);
				populateCalculatorsMenu(subMenu, dataModel, actions, (TreeMap) value);
			}
			else
			{
				Action a = new CalculatorAction(dataModel, DataWorkshop.getInstance().getDataTransformation(key));
				if (!actions.containsKey(a.getValue(Action.NAME)))
				{
					actions.put(a.getValue(Action.NAME), a);
				}
				else
				{
					a = (Action) actions.get(a.getValue(Action.NAME));
				}
				menu.add(a);
			}
		}
	}

	public void populateTransformerMenu(JMenu menu, DataModel dataModel, HashMap actions)
	{
		populateTransformerMenu(menu, dataModel, actions, menuMap);
	}

	/******************************************************************************
	*	Private Methods
	*/
	/*
	 *  Only instanciates a new DataTransformerAction if this action is not contained in the HashSet provided
	 */
	private void populateTransformerMenu(JMenu menu, DataModel dataModel, HashMap actions, TreeMap map)
	{
		Iterator it = map.keySet().iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			Object value = map.get(key);
			if (value instanceof TreeMap)
			{
				JMenu subMenu = new JMenu(key);
				menu.add(subMenu);
				populateTransformerMenu(subMenu, dataModel, actions, (TreeMap) value);
			}
			else
			{
				DataTransformation transformation = DataWorkshop.getInstance().getDataTransformation(key);
				if (transformation != null)
				{
					Action a = new TransformerAction(dataModel, transformation);
					if (!actions.containsKey(a.getValue(Action.NAME)))
					{
						actions.put(a.getValue(Action.NAME), a);
					}
					else
					{
						a = (Action) actions.get(a.getValue(Action.NAME));
					}
					menu.add(a);
				}
			}
		}
	}

	private void serialize(Element context, TreeMap map)
	{
		Iterator it = map.keySet().iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			Object value = map.get(key);
			if (value instanceof TreeMap)
			{
				Element element = XMLSerializeFactory.addElement(context, SUBMENU_TAG);
				XMLSerializeFactory.setAttribute(element, NAME_TAG, (String) key);
				serialize(element, (TreeMap) value);
			}
			else if (value instanceof String)
			{
				Element element = XMLSerializeFactory.addElement(context, TRANSFORMATION_TAG);
				XMLSerializeFactory.setAttribute(element, NAME_TAG, (String) value);
			}
		}
	}
	
	private void deserialize(Element context, TreeMap map)
	{
		Element[] elements = XMLSerializeFactory.getChildElements(context);
		for (int i = 0; i < elements.length; i++)
		{
			if (elements[i].getNodeName().equals(SUBMENU_TAG))
			{
				TreeMap newMap = new TreeMap();
				map.put(XMLSerializeFactory.getAttribute(elements[i], NAME_TAG), newMap);
				deserialize(elements[i], newMap);
			}
			else if (elements[i].getNodeName().equals(TRANSFORMATION_TAG))
			{
				String name = XMLSerializeFactory.getAttribute(elements[i], NAME_TAG);
				map.put(name, name);
			}
		}
	}
}
