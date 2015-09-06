package dataWorkshop.gui;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.gui.data.DataModel;

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
public class DiffDataOptions {
    DataModel model;
    String dataFileName;
    DataEncoding unit;
    int granularity;
    
    /******************************************************************************
     *	Constructors
     */
    public DiffDataOptions(DataModel m, String name, DataEncoding u, int gran) {
        dataFileName = name;
        model = m;
        unit = u;
        granularity = gran;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public DataModel getDataModel() {
        return model;
    }
    
    public String getName() {
        return dataFileName;
    }
    
    public DataEncoding getDataConverter() {
        return unit;
    }
    
    public int getGranularity() {
        return granularity;
    }
}