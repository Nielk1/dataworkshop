package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.logging.Logger;

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
public class ManualDialog extends DialogWindow
implements ActionListener, HyperlinkListener, LocaleStrings {
    
    public final static String CLASS_NAME = "ManualDialog";
    
    JEditorPane manualPane;
    
    JButton[] buttons = {closeButton};
     private static ManualDialog instance;
    /******************************************************************************
     *	Constructors
     */
    public ManualDialog() {
        super("User Manual", false);
    }
    
    /******************************************************************************
     *	HyperLinke Listener
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                manualPane.setPage(e.getURL());
            }
            catch (IOException ee) {
                System.out.println(ee);
            }
        }
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
    
    public static ManualDialog getInstance() {
        if (instance == null) {
            instance = new ManualDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
         //Build ContentPane
        manualPane = new JEditorPane();
        manualPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        manualPane.setEditable(false);
        manualPane.addHyperlinkListener(this);
        JScrollPane mainPane = new JScrollPane(manualPane);
        manualPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        try {
            URL toc = new URL("file:" + DataWorkshop.getInstance().getFile(DataWorkshop.USER_MANUAL_DIR).getAbsolutePath()
            + File.separator + "toc.html");
            manualPane.setPage(toc);
        }
        catch (IOException e) {
			Logger.getLogger(ManualDialog.class).severe("Could not open documentation", e);
        }
        
        JPanel pane = getMainPane();
        pane.setLayout(new BorderLayout());
        pane.add(mainPane, BorderLayout.CENTER);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        
        setSize(400, 400);
    }
}
