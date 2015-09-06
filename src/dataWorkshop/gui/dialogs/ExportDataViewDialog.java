package dataWorkshop.gui.dialogs;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.Stylesheet;
import dataWorkshop.data.view.ExportDataViewOptions;
import dataWorkshop.gui.ComboPane;
import dataWorkshop.gui.IntegerFormatFactoryPane;
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
public class ExportDataViewDialog extends DialogWindow
implements LocaleStrings {
    
    public final static String CLASS_NAME = "ExportDataViewDialog";
    
    JButton exportButton = new JButton(EXPORT_BUTTON_NAME);
    JButton[] buttons = {exportButton, cancelButton};
    
    IntegerFormatFactoryPane integerFormatFactoryPane;
    ComboPane stylesheetBox;
    NumberPane bitsPerLinePane;
    
    private static ExportDataViewDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public ExportDataViewDialog() {
        super(EXPORT_DATA_VIEW_DIALOG_TITLE, true);
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
    
    public static ExportDataViewDialog getInstance() {
        if (instance == null) {
            instance = new ExportDataViewDialog();
            instance.buildDialog();
        }
        return instance;
    }
    
    public ExportDataViewOptions show(Frame owner) {
        setLocationRelativeTo(owner);
        
        pack();
        
        this.setVisible(true);
        if (wasButtonSelected(exportButton)) {
            ExportDataViewOptions options = new ExportDataViewOptions((Stylesheet) stylesheetBox.getSelectedItem());
			options.getDataViewOption().setBitsPerLine((int) bitsPerLinePane.getValue());
			options.setIntegerFormatFactory(integerFormatFactoryPane.getIntegerFormatFactory());
            
            return options;
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
        
        integerFormatFactoryPane = new IntegerFormatFactoryPane();
		integerFormatFactoryPane.setIntegerFormatFactory(options.getIntegerFormatFactory());
        bitsPerLinePane = new NumberPane(options.getUnsignedOffsetNumber(), "Bits Per Line");
        stylesheetBox = new ComboPane(options.getStylesheets(), "XSLT Stylesheet");
        
        JPanel pane = getMainPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(integerFormatFactoryPane);
        pane.add(Box.createRigidArea(new Dimension(0, 6)));
        pane.add(bitsPerLinePane);
        pane.add(Box.createRigidArea(new Dimension(0, 6)));
        pane.add(stylesheetBox);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        setCancelButton(buttons[1]);
    }
}
