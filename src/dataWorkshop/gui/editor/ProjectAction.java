package dataWorkshop.gui.editor;

import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.gui.ProjectPane;

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
public abstract class ProjectAction extends AbstractEditorAction 
    implements StateChangeListener {
    
    protected ProjectPane projectPane;
    
    /******************************************************************************
     *	Constructors
     */
    public ProjectAction(String actionCommand) {
        this(null, actionCommand);
    }
    
    public ProjectAction(ProjectPane projectPane, String actionCommand) {
        super(actionCommand);
        setProjectPane(projectPane);
    }
    
    public void stateChanged(StateChangeEvent e) {
        setEnabled(true);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setProjectPane(ProjectPane project) {
        if (this.projectPane != null) {
            this.projectPane.removeStateChangeListener(this);
        }
        
        this.projectPane = project;
        
        if (this.projectPane != null) {
            this.projectPane.addStateChangeListener(this);
            stateChanged(new StateChangeEvent(this));
        }
        else {
            setEnabled(false);
        }
    }
}
