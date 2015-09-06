package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.SocketInformation;
import dataWorkshop.gui.NumberPane;

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
public class SocketDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "SocketDialog";
    
    final JButton[] buttons = {okButton, cancelButton};
    
    NumberPane portPane;
    JTextField hostField;

    private static SocketDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public SocketDialog() {
        super(SOCKET_DIALOG_TITLE, true);
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
    
    public static SocketDialog getInstance() {
        if (instance == null) {
            instance = new SocketDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
    public SocketInformation show(Frame owner) {
          setLocationRelativeTo(owner);
        this.setVisible(true);
        if (wasButtonSelected(okButton)) {
            return new SocketInformation(hostField.getText(), (int) portPane.getValue());
        }
        else {
            return null;
        }
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
         DataWorkshop options = DataWorkshop.getInstance();
        
        hostField = new JTextField();
        portPane = new NumberPane(options.getUnsignedCount(), "Port");
        
        JPanel northPane = new JPanel();
		northPane.setLayout(new BoxLayout(northPane, BoxLayout.X_AXIS));
		northPane.add(new JLabel("Host"));
		northPane.add(Box.createRigidArea(new Dimension(6, 0)));
		northPane.add(hostField);
		northPane.add(Box.createRigidArea(new Dimension(12, 0)));
		northPane.add(portPane);
		
        JPanel pane = getMainPane();
        pane.setLayout(new BorderLayout());
        pane.add(northPane, BorderLayout.NORTH);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        setCancelButton(buttons[1]);
        
        pack();
    }
}
