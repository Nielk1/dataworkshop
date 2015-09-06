package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.MySocket;
import dataWorkshop.gui.ComboPane;

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
public class ChooseSocketDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "ChooseSocketDialog";
    
    final JButton[] buttons = {okButton, cancelButton};
    
    ComboPane socketBox;
    private static ChooseSocketDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public ChooseSocketDialog() {
        super(CHOOSE_SOCKET_DIALOG_TITLE, true);
    }
    
        /******************************************************************************
     *	XMLSerializable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static void rebuild() {
        instance = null;
    }
    
    public static ChooseSocketDialog getInstance() {
        if (instance == null) {
            instance = new ChooseSocketDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
    public MySocket show(Frame owner, MySocket[] openSockets) {
        setLocationRelativeTo(owner);
        socketBox.setItems(openSockets);
        pack();
        
        this.setVisible(true);
        if (wasButtonSelected(okButton)) {
            return (MySocket) socketBox.getSelectedItem();
        }
        else {
            return null;
        }
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
           
        socketBox = new ComboPane();
        
        JPanel pane = getMainPane();
        pane.setLayout(new BorderLayout());
        pane.add(socketBox, BorderLayout.NORTH);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        setCancelButton(buttons[1]);
    }
}
