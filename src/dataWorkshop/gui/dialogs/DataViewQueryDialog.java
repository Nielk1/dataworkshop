package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.TextFieldBoxPane;
import dataWorkshop.xml.XMLSerializeFactory;

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
public class DataViewQueryDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "DataViewQueryDialog";
    final static String HISTORY_TAG = "History";
    final static String QUERY_TAG = "Query";
    
    final static int DATA_VIEW_QUERY_HISTORY = 20;
    
    JButton queryButton = new JButton(QUERY_BUTTON_NAME);
    final JButton[] buttons = {queryButton, cancelButton};
    
    TextFieldBoxPane queryPane;
    private static DataViewQueryDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public DataViewQueryDialog() {
        super(DATA_VIEW_QUERY_DIALOG_TITLE, true);
        queryPane = new TextFieldBoxPane(new String[0], "Query");
    }
    
     /******************************************************************************
     *	XMLSerializable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        super.serialize(context);
        XMLSerializeFactory.serialize(context, HISTORY_TAG, QUERY_TAG, getHistory());
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
        setHistory(XMLSerializeFactory.deserializeAsStringArray(context, HISTORY_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static void rebuild() {
        if (instance != null) {
            String xml = instance.serialize();
            instance = new DataViewQueryDialog();
            instance.buildDialog();
            instance.deserialize(xml);
        }
    }
    
    public static DataViewQueryDialog getInstance() {
        if (instance == null) {
            instance = new DataViewQueryDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
    public String show(Frame owner) {
        setLocationRelativeTo(owner);
		pack();
        //queryPane.setText(query);
        this.setVisible(true);
        if (wasButtonSelected(queryButton)) {
            return queryPane.getText();
        }
        else {
            return null;
        }
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
        queryPane.setHistoryLength(DATA_VIEW_QUERY_HISTORY);
        
        JPanel pane = getMainPane();
        pane.setLayout(new BorderLayout());
        pane.add(queryPane, BorderLayout.NORTH);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        setCancelButton(buttons[1]);
    }
    
    private String[] getHistory() {
        return queryPane.getHistory();
    }
    
    private void setHistory(String[] history) {
        queryPane.setHistory(history);
        pack();
    }
}
