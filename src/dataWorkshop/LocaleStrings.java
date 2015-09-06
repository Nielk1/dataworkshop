package dataWorkshop;

import java.text.MessageFormat;

import dataWorkshop.data.Data;

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
public interface LocaleStrings {
	
	final static String DATA_WORKSHOP = "Data Workshop";
    final static String VERSION = "v1.1.1";
    final static String AUTHOR = "martin.pape@gmx.de";
    final static String HOMEPAGE = "http://www.dataworkshop.de";
    
    final static String CREDITS_STRING = 
		"Greets go to" + "\n" +
		"Seth [GECKO]" + "\n" +
		 "Bakuryu [GOLDEN HARVEST]" + "\n" +
    	"Yuen Bao [GOLDEN HARVEST]" + "\n" +
		"Fong Sai Yuk [GOLDEN HARVEST]" + "\n" +
		"Elyza [ERASMUS]";
		
    final static String ABOUT_STRING =
    	DATA_WORKSHOP + " " + VERSION + "\n" +
	    "written by " + AUTHOR + "\n" +
	    HOMEPAGE;
    
    /******************************************************************************
     *	Action Strings
     */
    String DATA = "Data";
     
    //Buttons
    String EXPORT_BUTTON_NAME = "Export";
    String SAVE_BUTTON_NAME = "Save";
    String CLOSE_BUTTON_NAME = "Close";
    String CANCEL_BUTTON_NAME = "Cancel";
    String OK_BUTTON_NAME = "Ok";
    String QUERY_BUTTON_NAME = "Query";
    String DIFF_DATA_BUTTON_NAME = "Diff Data";
    String CONVERT_TO_DATA_BUTTON_NAME = "Convert To Data";
    String SET_TO_ZERO_BUTTON_NAME = "Set to Zero";
    String CLEAR_BUTTON_NAME = "Clear";
    
    String REPEAT = "Repeat";
	String REPEAT_TILL_OFFSET = "Repeat till Offset";
    String LENGTH = "Length";
    String OFFSET = "Offset";
    String BIT_SIZE = "Bitsize";
    String NAME = "Name";
    String ALIGNMENT_BIT_SIZE = "Alignment";
    String ALIGN_RELATIVE_TO = "Align relative to";
    String EVALUTE_FIELD = "Evaluate Field";
    String FIELD_VALUE = "Field Value";
    String REMAINING_BITS = "Remaining Bits";
    
    String RENDER_OFFSET = "Render Offset";
    String RENDER_SIZE = "Render Size";
    String OFFSET_SIZE_GRANULARITY = "Offset & Size Granularity";
    String BITS_PER_LINE = "Bits per Line";
    String LINES_PER_PAGE = "Lines per Page";
    String DATA_ENCODING = "Data Encoding";
    String DELIMITER_ENCODING = "Delimiter Encoding";
    String CLIPBOARD_DATA = "Clipboard Data";
    String LITTLE_ENDIAN = "Little Endian";
    String DATA_MAPPING = "Data Mapping";
    
    //Text for the StatusBar Label
    String RANGE_LABEL =  "Range";
    String OFFSET_LABEL = "Offset";
    String SIZE_LABEL = "Size";
    String NO_VALUE ="-- ";
    
    //Hashmap Keys for EditorOptions
    String LINE_SEPARATOR = "Line Separator";
    String FILE_SEPARATOR = "File Separator";
    String UNIT_SEPARATOR = "Unit Separator";
    
    //EditorOptionsDialog
    String SINGLE_LINE_VIEWS = "Single Line Views";
    String FONT_SIZE = "Font Size";
    String LOOK_AND_FEEL = "Look And Feel";
    
    /******************************************************************************
     *	Error Messages (Output Window)
     */
    String SOCKET_LISTEN_ERROR = "Socket Listen Error";
    String SOCKET_CLOSE_ERROR = "Socker Close Error";
    String DATA_VIEW_QUERY_ERROR = "Data View Query Error";
    String DATA_CONVERTER_ERROR = "Data Conversion Error";
    String LOOK_AND_FEEL_ERROR = "Could not change Look And Feel";
    String DEFINITION_NODE_APPLY_CHANGES_ERROR = "Could not apply changes";
    
    String ERROR_OPEN_STRUC = "Not a valid View Structure";
    String ERROR_SAVE_FILE = "Could not Save File";
    String ERROR_FILE_TOO_BIG = "File too big";
    String ERROR_JUMP_OUT_OF_RANGE = "Jump out of Range";
    String ERROR_OPEN_OPTIONS = "Could not open Options";
    String ERROR_COULD_NOT_FIND_DATA_CONVERTER = "Could not find DataConverter";
    String ERROR_COULD_NOT_FIND_DATA_FILTER = "Could not find DataFilter";
    String ERROR_DATA_BITSIZE_IS_NOT_ALIGNED_CORRECTLY = "Databitsize is not aligned correctly";
    String ERROR_TRANSFORMATION = "Could not transform Data";
    String ERROR_STYLSHEET = "Could not process Stylesheet";
    
    String INFO_NO_MATCH = "No Match";
    String INFO_REPLACE_ALL = "Replaced";
    String INFO_NO_DIFFERENCES = "No Differences";
    
    String INFO = "INFO";
    String FOUND = "Found";
    String NOT_FOUND = "Not Found";
    
    String SERIALIZED_DATAWORKSHOP = "dataWorkshop.xml";
    String RESTORED_DATAWORKSHOP =  FOUND + " " + SERIALIZED_DATAWORKSHOP;
    String NO_RESTORE_DATAWORKSHOP = "No " + SERIALIZED_DATAWORKSHOP + " found";
    
    String SERIALIZED_EDITOR = "editor.xml";
    String RESTORED_EDITOR =  FOUND + " " + SERIALIZED_EDITOR;
    String NO_RESTORE_EDITOR = "No " + SERIALIZED_EDITOR + " found";
    
    //
    String X_PATH_QUERY_RESULT = "Query Result";
    String FIND_ALL_RESULT = "Find All Result";
    
    //Dialog titles
	String STRUCTURE_DEBUGGER_DIALOG_TITLE = "Structure Debugger";
	String STRUCTURE_PALETTE_DIALOG_TITLE = "View Definition Palette";
    String ABOUT_DIALOG_TITLE = "About";
    String CREDIT_DIALOG_TITLE = "Credits";
    String ENCODING_CONVERTER_DIALOG_TITLE = "Encoding Converter";
    String EXPORT_DATA_VIEW_DIALOG_TITLE = "Export Data View";
    String SOCKET_DIALOG_TITLE = "Create Socket";
    String DEFINE_FIND_AND_REPLACE_DIALOG_TITLE = "Define Find And Replace Data";
    String DATA_CLIPBOARD_DIALOG_TITLE = "Data Clipboard";
    String DIFF_DATA_DIALOG_TITLE = "Diff Data";
    String STRUC_SAVE_DIALOG = "Save View Structure As";
    String STRUC_OPEN_DIALOG = "Open View Structure";
    String FILE_SAVE_DIALOG = "Save File As";
    String FILE_OPEN_DIALOG = "Open File";
    
    String PREFERENCES_DIALOG_TITLE = "Preferences";
    String CONFIGURE_DATA_VIEW_OPTIONS_DIALOG = "Data View Options";
    
    String CHOOSE_ENCODING_DIALOG_TITLE = "Choose Encoding";
    String CHOOSE_SOCKET_DIALOG_TITLE = "Choose Connection";
    String TEXT_CLIPBOARD_DIALOG_TITLE = "Text Clipboard";
    String DATA_VIEW_QUERY_DIALOG_TITLE = "DataView Query";
    
    String SIGNATURES = "Signatures";
    
     /******************************************************************************
     *	Message Formats
     */
     MessageFormat FIND_ALL_RESULT_ABOVE_LIMIT_MESSAGE = new MessageFormat("Find All returned {0} matches. Displaying them all will probably result in an out of memory exception if you dont have an insane amount of memory. How many matches do you want to display");
     
    MessageFormat DATA_CONVERTER_ILLEGAL_CHAR_MESSAGE = new MessageFormat("While decoding ''{0}'' an Error occured. Could not decode character ''{1}'' (ascii value {2})");
	MessageFormat DATA_CONVERTER_SUBVALUE_OUTSIDE_RANGE_MESSAGE = new MessageFormat("While decoding ''{0}'' an Error occured. {1} is not in the valid range {2} - {3}");
    MessageFormat DATA_CONVERTER_VALUE_OUTSIDE_RANGE_MESSAGE = new MessageFormat("While decoding ''{0}'' an Error occured. It is not in the valid range {1} - {2}");
    MessageFormat DATA_CONVERTER_MESSAGE = new MessageFormat("While decoding ''{0}'' an error occured");
    MessageFormat DATA_CONVERTER_NOT_ENOUGH_CHARS_MESSAGE = new MessageFormat("Could not decode text. Text length must be a multiple of {0}. {1} chararacters are missing");
	MessageFormat IMPORT_DATA_MESSAGE = new MessageFormat("Could not import {0}. File length is not a multiple of {1}");
	MessageFormat EXPORT_DATA_MESSAGE = new MessageFormat("Could not export {0}. Data bitSize is not a multiple of {1}");

    MessageFormat OPEN_FILE_TOO_BIG_MESSAGE = new MessageFormat("{0} is too big and could not be opened. Maxmimum Filesize is " + Data.MAX_SIZE_IN_BYTES + " bytes");
	MessageFormat OPEN_FILE_MESSAGE = new MessageFormat("{0} could not be opened");

	MessageFormat OPEN_VIEW_DEFINITION_MESSAGE = new MessageFormat("{0} view definition could not be opened");
	MessageFormat SAVE_VIEW_DEFINITION_MESSAGE = new MessageFormat("{0} view definition could not be saved");
		
	MessageFormat OPEN_STRUCTURE_MESSAGE = new MessageFormat("{0} structure could not be opened");
	MessageFormat SAVE_STRUCTURE_MESSAGE = new MessageFormat("{0} structure could not be saved");

	MessageFormat OPEN_DATA_OBJECT_MESSAGE = new MessageFormat("{0} could not be opened");
	MessageFormat SAVE_DATA_OBJECT_MESSAGE = new MessageFormat("{0} could not be saved");
}