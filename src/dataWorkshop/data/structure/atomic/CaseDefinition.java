package dataWorkshop.data.structure.atomic;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.Validator;
import dataWorkshop.data.Data;
import dataWorkshop.data.structure.ViewDefinitionElement;
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
public class CaseDefinition implements XMLSerializeable
{
	public final static String CLASS_NAME = "CaseDefinition";
	final static String DEFAULT_CASE_TAG = "defaultCase";
	public final static String NONE = "-- None --";

	private String defaultCase;
	private StringToDataMappingDefinition stringToDataMappingDefinition;

	/******************************************************************************
	*	Constructors
	*/
	public CaseDefinition()
	{
		this(new String(), new StringToDataMappingDefinition());
	}

	public CaseDefinition(String defaultCase, StringToDataMappingDefinition stringToDataMappingDefinition)
	{
		this.defaultCase = defaultCase;
		this.stringToDataMappingDefinition = stringToDataMappingDefinition;
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
		XMLSerializeFactory.setAttribute(context, DEFAULT_CASE_TAG, getDefaultCase());
		XMLSerializeFactory.serialize(context, getStringToDataMappingDefinition());
	}

	public void deserialize(Element context)
	{
		setDefaultCase(XMLSerializeFactory.getAttribute(context, DEFAULT_CASE_TAG));
		setStringToDataMappingDefinition((StringToDataMappingDefinition) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	*	Public Methods
	*/
	public boolean validate(Validator validator, ViewDefinitionElement node)
	{
		boolean isValid = getStringToDataMappingDefinition().validate(validator, node);
		
		String defaultCase = getDefaultCase();
		if (defaultCase == null) {
			validator.getValidatorOutput().error(node, Validator.NO_DEFAULT_CASE_DEFINED_ERROR);
			return false;
		}
		
		if (!defaultCase.equals(NONE) && node.getChildStructure(defaultCase) == null) {
			validator.getValidatorOutput().error(node, Validator.DEFAULT_CASE_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE.format(new Object[] {defaultCase}));
			return false;
		}
		
		String[] cases = getStringToDataMappingDefinition().getMapping().getNames();
		for (int i = 0; i < cases.length; i++) {
			if (!cases[i].equals(NONE) && node.getChildStructure(cases[i]) == null) {
				validator.getValidatorOutput().error(node, Validator.CASE_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE.format(new Object[] {cases[i]}));
				isValid = false; 
			}
		}
		
		return isValid;
	}

	public String evaluateCase(Data caseData)
	{
		String child = null;

		Data[] values = getStringToDataMappingDefinition().getMapping().getData();
		String[] childNames = getStringToDataMappingDefinition().getMapping().getNames();
		for (int i = 0; i < values.length; i++)
		{
			if (values[i].equals(caseData))
			{
				child = childNames[i];
			}
		}

		if (child == null)
		{
			//use default pointer
			child = getDefaultCase();
			if (child == null)
			{
				return null;
			}
		}

		return child;
	}

	public long getBitSize()
	{
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

	public String getDefaultCase()
	{
		return defaultCase;
	}

	public void setDefaultCase(String defaultCase)
	{
		this.defaultCase = defaultCase;
	}
}