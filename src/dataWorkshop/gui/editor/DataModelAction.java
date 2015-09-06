package dataWorkshop.gui.editor;

import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;

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
public abstract class DataModelAction extends AbstractEditorAction
implements StateChangeListener, DataModelListener {
    
    protected DataModel dataModel;
    
    /******************************************************************************
     *	Constructors
     */
    public DataModelAction(String actionCommand) {
        this(null, actionCommand);
    }
    
    public DataModelAction(DataModel dataModel, String actionCommand) {
        super(actionCommand);
        setDataModel(dataModel);
    }
    
    public void stateChanged(StateChangeEvent e) {}
    
    public void selectionChanged(DataSelectionEvent e) {}
    
    public void dataChanged(DataChangeEvent e) {}
    
    /******************************************************************************
     *	Public Methods
     */
    public void setDataModel(DataModel dataModel) {
        if (this.dataModel != null) {
            this.dataModel.removeDataModelListener(this);
            this.dataModel.removeStateChangeListener(this);
        }
        this.dataModel = dataModel;
        if (this.dataModel != null) {
            this.dataModel.addDataModelListener(this);
            this.dataModel.addStateChangeListener(this);
            stateChanged(new StateChangeEvent(this));
            selectionChanged(new DataSelectionEvent(this, dataModel.getBitRange(), dataModel.getBitRange()));
        }
        else {
            setEnabled(false);
        }
    }
}
