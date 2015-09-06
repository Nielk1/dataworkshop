package dataWorkshop.data.view;

import dataWorkshop.Stylesheet;
import dataWorkshop.number.IntegerFormatFactory;

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
public class ExportDataViewOptions {
    
    Stylesheet stylesheet;
    DataViewOption viewOptions;
    IntegerFormatFactory integerFormatFactory;
    
     /******************************************************************************
     *	Constructors
     */
    public ExportDataViewOptions(Stylesheet stylesheet) {
        this(stylesheet, new DataViewOption(), new IntegerFormatFactory());
    }
    
    public ExportDataViewOptions(Stylesheet stylesheet, DataViewOption viewOptions, IntegerFormatFactory integerFormatFactory) {
        this.stylesheet = stylesheet;
        this.viewOptions = viewOptions;
        this.integerFormatFactory = integerFormatFactory;
    }
    
     /******************************************************************************
     *	Public Methods
     */
    public Stylesheet getStylesheet() {
        return stylesheet;
    }
    
    public void setStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }
    
    public DataViewOption getDataViewOption() {
        return viewOptions;
    }
    
    public IntegerFormatFactory getIntegerFormatFactory() {
    	return integerFormatFactory;
    }
    
	public void setIntegerFormatFactory(IntegerFormatFactory integerFormatFactory) {
		   this.integerFormatFactory = integerFormatFactory;
	   }
}