package dataWorkshop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

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
public class ActionPane extends JPanel {
    
    String actionCommand = null;
    private HashSet actionListeners = new HashSet();
    transient ActionEvent actionEvent = null;
    
    /******************************************************************************
     *	Constructors
     */
    public ActionPane() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }
    
    public void removeActionListener(ActionListener l) {
        actionListeners.remove(l);
    }
    
    public void setActionCommand(String s) {
        actionCommand = s;
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    protected void fireActionEvent() {
        Object[] listeners = actionListeners.toArray();
        for (int i = listeners.length-1; i>=0; i-=1) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0 , actionCommand);
                }
                ((ActionListener) listeners[i]).actionPerformed(actionEvent);
        }
    }
    
}