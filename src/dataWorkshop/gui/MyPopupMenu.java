package dataWorkshop.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Convenience Class to add a JPopupmenu to a JComponent.  
 * The MouseListener is handled in this Class and the JComponent class is not messed up
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
public class MyPopupMenu 
    implements MouseListener {
        
    JPopupMenu popupMenu;

    /******************************************************************************
     *	Constructors
     */
    public MyPopupMenu(JPopupMenu menu) {
        popupMenu = menu;
    }

     /******************************************************************************
     *	Public Methods
     */
    public void addComponent(JComponent component) {
        component.addMouseListener(this);
    }
    
    public void add(Action action) {
        popupMenu.add(action);
    }
    
    public void addSeparator() {
        popupMenu.addSeparator();
    }
    
     /******************************************************************************
     *	MouseListener Interface
     */
    public void mousePressed(MouseEvent e) {
        popup(e);
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
        popup(e);
    }
    
    /**
     *  If e.isPopupTrigger() grabFocus for the Component and display popupmenu
     */
    public void popup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            //processFocusEvent(new FocusEvent(this, FocusEvent.FOCUS_GAINED));
            JComponent component = (JComponent) e.getComponent();
            //component.grabFocus();
            popupMenu.show(component, e.getX(), e.getY());
        }
    }
}
