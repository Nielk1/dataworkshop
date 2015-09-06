package dataWorkshop.data.structure.compiler;

import java.text.MessageFormat;

import dataWorkshop.data.structure.ViewDefinitionElement;

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
public class Validator
{
	public final static String STEP_SIZE_MUST_BE_GREATER_THAN_ZERO_ERROR = "Step Size must be > 0";
	public final static String NO_DELIMITER_DATA_DEFINED_ERROR = "No Delimiter Data defined";
	public final static String NO_DELIMITER_ENCODING_DEFINED_ERROR = "No Delimiter Encoding defined";
	public final static String NO_DATA_ENCODING_DEFINED_ERROR = "No Data Encoding defined";
	public final static String NO_DEFAULT_CASE_DEFINED_ERROR = "No Default Case Defined";
	public final static String LENGTH_MUST_BE_GREATER_THAN_ZERO_ERROR = "Length must be > 0";
	
	public final static String NO_LOCATION_PATH_DEFINED_ERROR = "No Location Path defined";
	public final static String NO_POINTER_PATH_DEFINED_ERROR = "No Pointer Path defined";
	
	public final static MessageFormat POINTER_PATH_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE = new MessageFormat("Pointer Path ''{0}'' could not be resolved");
	public final static MessageFormat LOCATION_PATH_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE = new MessageFormat("Location Path ''{0}'' could not be resolved");
	public final static MessageFormat DEFAULT_CASE_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE = new MessageFormat("Default Case ''{0}'' could not be resolved");
	public final static MessageFormat CASE_COULD_NOT_BE_RESOLVED_ERROR_MESSAGE = new MessageFormat("Case ''{0}'' could not be resolved");
	
	public static MessageFormat ILLEGAL_XML_NAME_ERROR = new MessageFormat("''{0}'' is not a valid xmlname. You are not allowed to use the following characters {1}"); 
	
	ValidatorOutput output;

	/******************************************************************************
	 *	Constructors
	 */
	public Validator(ValidatorOutput output)
	{
		this.output = output; 
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public ValidatorOutput getValidatorOutput() {
		return output;
	}

	public boolean validate(ViewDefinitionElement root)
	{
		return root.validate(this);
	}
}
