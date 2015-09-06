package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Element;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.SingleUnitView;

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
public class DataClipboardDialog extends DialogWindow
implements LocaleStrings {
    
    public final static String CLASS_NAME = "DataClipboardDialog";
    
    SingleUnitView singleUnitView;
    
    final JButton[] buttons = {closeButton};
    
    private static DataClipboardDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public DataClipboardDialog() {
        super(DATA_CLIPBOARD_DIALOG_TITLE, false);
    }
    
    /******************************************************************************
     *	XMLSerializable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        super.serialize(context);
    }
    
    public void deserialize(Element context) {
        super.deserialize(context);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static void rebuild() {
        if (instance != null) {
            boolean visible = instance.isVisible();
            instance.setVisible(false);
            Data data = instance.getData();
            String xml = instance.serialize();
            instance = new DataClipboardDialog();
            instance.buildDialog();
            instance.deserialize(xml);
            instance.setData(data);
            instance.setVisible(visible);
        }
    }
    
    public static DataClipboardDialog getInstance() {
        if (instance == null) {
            instance = new DataClipboardDialog();
            instance.buildDialog();
        }
        return instance;
    }
    
    public void setData(Data data) {
        DataModel dataModel = getDataModel();
        dataModel.paste(data, 0, dataModel.getBitSize());
    }
    
    public Data getData() {
        return getDataModel().getData();
    }
    
    public DataModel getDataModel() {
       return singleUnitView.getDataModel();
    }
    
    public void setDataModel(DataModel dataModel) {
        singleUnitView.setDataModel(dataModel);
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
        singleUnitView = new SingleUnitView(DATA_ENCODING);
        
        JPanel main = getMainPane();
        main.setLayout(new BorderLayout());
        main.add(singleUnitView, BorderLayout.CENTER);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
        setCancelButton(buttons[0]);
        
        pack();
    }
}
