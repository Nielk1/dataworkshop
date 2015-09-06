package dataWorkshop.gui.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import dataWorkshop.logging.Logger;

/**
 * This class is just a wrapper for all Actions and delegates to the real action
 * It is used to catch Exceptions
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
abstract public class AbstractEditorAction extends AbstractAction {
    
    protected Logger logger;
    
    public AbstractEditorAction(String actionCommand) {
        super(actionCommand);
        logger = Logger.getLogger(this.getClass());
    }
    
    abstract public void doAction();
    
    public void actionPerformed(ActionEvent e) {
        try {
            doAction();
            /**
             * :KLUDGE:Martin Pape:2002-08-17: Why is this necessary ?
             */
            Editor.getInstance().forceRepaint();
        }
        catch (Exception ee) {
            logger.warning(ee.toString(), ee);
        }
    }
}
