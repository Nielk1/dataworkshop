package dataWorkshop.data.structure;

import org.w3c.dom.Element;

import dataWorkshop.data.structure.compiler.*;
import dataWorkshop.data.structure.compiler.Compiler;
import dataWorkshop.data.Data;
import dataWorkshop.data.structure.atomic.CaseDefinition;
import dataWorkshop.data.structure.atomic.LocationDefinition;
import dataWorkshop.data.view.DataFrame;
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
public class CaseStatement extends StatementStructure
{
	public final static String CLASS_NAME = "CaseStatement";
	final static String NAME = "Case Statement";

	public final static String NONE = "-- None --";

	private LocationDefinition locationDefinition;
	private CaseDefinition caseDefinition;

	/******************************************************************************
	 *	Constructors
	 */
	public CaseStatement()
	{
		this(DEFAULT_LABEL, new CaseDefinition(), new LocationDefinition());
	}

	public CaseStatement(String label, CaseDefinition caseDefinition, LocationDefinition locationDefinition)
	{
		super(label);
		this.caseDefinition = caseDefinition;
		this.locationDefinition = locationDefinition;
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
		XMLSerializeFactory.serialize(context, getLocationDefintion());
		XMLSerializeFactory.serialize(context, getCaseDefinition());
	}

	public void deserialize(Element context)
	{
		super.deserialize(context);
		setLocationDefintion((LocationDefinition) XMLSerializeFactory.deserializeFirst(context));
		setCaseDefinition((CaseDefinition) XMLSerializeFactory.deserializeFirst(context));
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

	public boolean validate(Validator validator)
	{
		boolean isValid = super.validate(validator);
		isValid = isValid & locationDefinition.validate(validator, this);
		isValid = isValid & caseDefinition.validate(validator, this);
		return isValid;
	}

	public DataFrame[] compile(Compiler compiler, long bitOffset, DataFrame parent)
	{
		CompilerOutput output = compiler.getCompilerOutput();
		DataFrame locationFrame = getLocationDefintion().translate(compiler, this, bitOffset, parent);
		if (locationFrame == null)
		{
			output.error(this, bitOffset, "Could not resolve locationReference");
			return new DataFrame[0];
		}

		if (locationFrame.getBitOffset() + getCaseDefinition().getBitSize() > compiler.getData().getBitSize())
		{
			output.error(this, bitOffset, "LocationReference points outside of data");
			return new DataFrame[0];
		}

		Data caseData =  compiler.getData().copy(locationFrame.getBitOffset(), getCaseDefinition().getBitSize());
		String caseDataAsString = caseData.toString(getCaseDefinition().getStringToDataMappingDefinition().getDataEncoding());

		String childName = getCaseDefinition().evaluateCase(caseData);
		output.info(this, bitOffset, Compiler.CASE_DATA_RESOLVED_TO_INFO_MESSAGE.format(new Object[] {caseDataAsString, childName}));
		
		if (childName.equals(CaseDefinition.NONE))
		{
			return new DataFrame[0];
		}
		
		ViewDefinitionElement caseStructure = (ViewDefinitionElement) getChildStructure(childName);

		DataFrame[] frames = caseStructure.compile(compiler, bitOffset, parent);
		for (int i = 0; i < frames.length; i++)
		{
			parent.add(frames[i]);
			long childSize = frames[i].getBitSize();
			parent.setBitSize(parent.getBitSize() + childSize);
			bitOffset += childSize;
		}

		return frames;
	}

	public LocationDefinition getLocationDefintion()
	{
		return locationDefinition;
	}

	public void setLocationDefintion(LocationDefinition locationDefinition)
	{
		this.locationDefinition = locationDefinition;
	}

	public String[] getPossibleValueNames()
	{
		int children = getChildCount();
		String[] names = new String[children + 1];
		for (int i = 0; i < children; i++)
		{
			names[i] = ((ViewDefinitionElement) getChildAt(i)).getLabel();
		}
		names[children] = NONE;
		return names;
	}

	public CaseDefinition getCaseDefinition()
	{
		return caseDefinition;
	}

	public void setCaseDefinition(CaseDefinition caseDefinition)
	{
		this.caseDefinition = caseDefinition;
	}
}