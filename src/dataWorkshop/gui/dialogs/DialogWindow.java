/**

 * 
 */

package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.w3c.dom.Element;

import dataWorkshop.LocaleStrings;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

/**
 *  Abstract base class for all Dialog which are serialized/deserialized. All Dialogs implement the singelton pattern.
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
abstract public class DialogWindow extends JDialog
implements ActionListener, XMLSerializeable, LocaleStrings {
    
    public final static String WIDTH = "width";
    public final static String HEIGHT = "height";
    public final static String X_POSITION = "xPosition";
    public final static String Y_POSITION = "yPosition";
    
    JButton okButton = new JButton(OK_BUTTON_NAME);
    JButton cancelButton = new JButton(CANCEL_BUTTON_NAME);
    JButton closeButton = new JButton(CLOSE_BUTTON_NAME);
    
    JPanel mainPane = new JPanel();
    JPanel buttonPane = new JPanel();
    JButton[] buttons = new JButton[0];
    JButton selectedButton = null;
    
    /******************************************************************************
     *	Constructors
     */
    public DialogWindow(String title, boolean modal) {
        super((Frame) null, title, modal);
        //setLocationRelativeTo(Editor.getInstance());
        
        JPanel southPane = new JPanel();
        southPane.setLayout(new BoxLayout(southPane, BoxLayout.Y_AXIS));
        southPane.add(Box.createRigidArea(new Dimension(0, 12)));
        southPane.add(buttonPane);
        
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pane.setLayout(new BorderLayout());
        pane.add(mainPane, BorderLayout.CENTER);
        pane.add(southPane, BorderLayout.SOUTH);
        setContentPane(pane);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        for (int i = 0; i < buttons.length; i++) {
            if (action == buttons[i].getActionCommand()) {
                selectedButton = (JButton) e.getSource();
                setVisible(false);
            }
        }
    }
    
      /******************************************************************************
     *	XMLSerializable Interface
     */
    public void serialize(Element context) {
        Dimension dim = getSize();
        XMLSerializeFactory.setAttribute(context, WIDTH, dim.getWidth());
        XMLSerializeFactory.setAttribute(context, HEIGHT, dim.getHeight());
        XMLSerializeFactory.setAttribute(context, X_POSITION, getX());
        XMLSerializeFactory.setAttribute(context, Y_POSITION, getY());
    }
    
    public void deserialize(Element context) {
        setSize(new Dimension(
            (int) XMLSerializeFactory.getAttributeAsDouble(context, WIDTH),
            (int) XMLSerializeFactory.getAttributeAsDouble(context, HEIGHT)
        ));
        setLocation(
            XMLSerializeFactory.getAttributeAsInt(context, X_POSITION),
            XMLSerializeFactory.getAttributeAsInt(context, Y_POSITION)
        );
    }
    
     /******************************************************************************
     *	Public Methods
     */
     public String serialize() {
        return XMLSerializeFactory.getInstance().serialize(this);
    }
    
    public void deserialize(String xml){
        XMLSerializeFactory.getInstance().deserialize(xml, this, false);
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    /**
     *	Set Buttons
     */
    protected void setButtons(JButton[] b) {
        //Button Pane
        buttonPane.removeAll();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].removeActionListener(this);
        }
        buttons = b;
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.add(Box.createHorizontalGlue());
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(this);
            buttonPane.add(buttons[i]);
            if (i < buttons.length -1) {
                buttonPane.add(Box.createRigidArea(new Dimension(6, 6)));
            }
        }
    }

    protected JPanel getMainPane() {
        return mainPane;
    }

    protected void setDefaultButton(JButton button) {
        getRootPane().setDefaultButton(button);
    }
    
    protected void setCancelButton(JButton button) {
        getRootPane().registerKeyboardAction(this, button.getActionCommand(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    protected boolean wasButtonSelected(JButton button) {
        /*
         *  Do a check if this button is valid
         */
        boolean foundButton = false;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == button) {
                foundButton = true;
            }
        }
        if (!foundButton) {
            throw new RuntimeException("wasButtonSelected(), " + button.getActionCommand() + " not defined in dialog");
        }
        return selectedButton == button;
    }
}


