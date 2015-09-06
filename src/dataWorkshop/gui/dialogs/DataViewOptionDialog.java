package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.data.view.DataViewOptionPane;

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
public class DataViewOptionDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "DataViewOptionDialog";
    
    DataViewOptionPane dataViewOptionPane;
    
    JButton[] buttons = {okButton, cancelButton};
    private static DataViewOptionDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public DataViewOptionDialog() {
        super(CONFIGURE_DATA_VIEW_OPTIONS_DIALOG, true);
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
    
    public static DataViewOptionDialog getInstance() {
        if (instance == null) {
            instance = new DataViewOptionDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
    public DataViewOption show(Frame owner, DataViewOption options) {
        setLocationRelativeTo(owner);
		dataViewOptionPane.setDataViewOption(options);
        
        this.setVisible(true);
        
        if (wasButtonSelected(okButton)) {
			return dataViewOptionPane.getDataViewOption();
        }
        else {
            return null;
        }
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
        dataViewOptionPane = new DataViewOptionPane();
        
        JPanel pane = getMainPane();
        pane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pane.setLayout(new BorderLayout());
        pane.add(dataViewOptionPane, BorderLayout.CENTER);
        
        setButtons(buttons);
        setDefaultButton(okButton);
        setCancelButton(cancelButton);
        
        pack();
    }
}
