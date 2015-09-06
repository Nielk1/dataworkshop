package dataWorkshop.data;

import org.w3c.dom.Element;

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
public class FilenameSuffixTemplate extends ViewTemplate
{
	final static String CLASS_NAME = "FilenameSuffixTemplate";
	final static String SUFFIX_TAG = "Suffix";

	String suffix;

	/******************************************************************************
	 *	Constructors
	 */
	public FilenameSuffixTemplate()
	{
		this(new String(), new String[0], new String());
	}

	public FilenameSuffixTemplate(String description, String[] structures, String suffix)
	{
		super(description, structures);
		this.suffix = suffix;
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
		XMLSerializeFactory.serialize(context, SUFFIX_TAG, getSuffix());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setSuffix(XMLSerializeFactory.deserializeAsString(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String getSuffix()
	{
		return suffix;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}
}
