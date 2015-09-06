package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.structure.atomic.StringToDataMappingDefinition;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.MapField;
import dataWorkshop.number.UnsignedOffset;
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
public class MapFieldDefinition extends DataFieldDefinition 
{
    public final static String CLASS_NAME = "MapFieldDefinition";
    final static String NAME = "Map Field";

	StringToDataMappingDefinition stringToDataMappingDefinition;
    
    /******************************************************************************
     *	Constructors
     */
	public MapFieldDefinition()
	{
		this(DEFAULT_LABEL);
	}

	public MapFieldDefinition(String label)
	{
		this(label, new StringToDataMappingDefinition());
	}

	public MapFieldDefinition(String label, StringToDataMappingDefinition stringToDataMappingDefinition)
	{
		super(label);
		this.stringToDataMappingDefinition = stringToDataMappingDefinition;
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
		XMLSerializeFactory.serialize(context, getStringToDataMappingDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setStringToDataMappingDefinition((StringToDataMappingDefinition) XMLSerializeFactory.deserializeFirst(context));
	}
    
    /******************************************************************************
     *	Public Methods
     */
	public String getShortDescription() {
		return (new UnsignedOffset(getBitSize())).toString(false) + "-bit " + getName();	
	}
	
     public String getName() {
        return NAME;
    }
    
	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & stringToDataMappingDefinition.validate(validator, this);
		return isValid;
	}
    
    public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent) {
		DataFrame[] frames = new DataFrame[1];
		frames[0] = render();
		frames[0].setBitOffset(bitOffset);
		frames[0].setBitSize(getBitSize());
	
		compiler.getCompilerOutput().info(this, bitOffset, "BitSize " + new UnsignedOffset(getBitSize()));

		parent.add(frames[0]);

		return frames;
    }
     
    public boolean isVisible() {
        return true;
    }
    
    public void render(DataFrame node) {
        super.render(node);
        MapField mapField = (MapField) node;
        mapField.setMapping(getStringToDataMappingDefinition().getMapping());
    }
    
    public DataFrame render() {
        MapField node = new MapField();
        render(node);
        return node;
    }
    
    public long getBitSize() {
        return getStringToDataMappingDefinition().getMapping().getBitSize();
    }

	public StringToDataMappingDefinition getStringToDataMappingDefinition()
	{
		return stringToDataMappingDefinition;
	}

	public void setStringToDataMappingDefinition(StringToDataMappingDefinition stringToDataMappingDefinition)
	{
		this.stringToDataMappingDefinition = stringToDataMappingDefinition;
	}

}