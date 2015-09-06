package dataWorkshop.data.structure;

import java.io.File;

import org.w3c.dom.Element;

import dataWorkshop.data.view.DataFrame;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;
import dataWorkshop.data.structure.compiler.Compiler;

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
public class RootStatement extends StatementStructure implements XMLSerializeable
{
	public final static String CLASS_NAME = "RootStatement";
	final static String NAME = "Root Statement";
	
	private String author;
	private String description;

	transient private File file;

	final static String AUTHOR_TAG = "Author";
	final static String DESCRIPTION_TAG = "Description";

	/******************************************************************************
	 *	Constructors
	 */
	public RootStatement()
	{
		this("new-structure", new String(), new String());
	}
	
	public RootStatement(String label, String author, String description)
	{
		super(label);
		this.author = author;
		this.description = description;
	}

	/******************************************************************************
	 *	XML Serializeable Interface
	 */
	public String getClassName() {
		return CLASS_NAME;
	}
	
	public void serialize(Element context)
	{
		super.serialize(context);
		XMLSerializeFactory.serialize(context, AUTHOR_TAG, getAuthor());
		XMLSerializeFactory.serialize(context, DESCRIPTION_TAG, getDescription());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setAuthor(XMLSerializeFactory.deserializeAsString(context));
		setDescription(XMLSerializeFactory.deserializeAsString(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public String getShortDescription() {
		return getName();	
	}
		
	public String getName()
		{
			return NAME;
		}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		DataFrame[] frames = new DataFrame[1];
		frames[0] = compiler.compileChildren(this, bitOffset, parent);
		return frames;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public File getFile()
	{
		return file;
	}
	
	public boolean isNew() {
		return file == null;
	}
}