package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.encoding.DataEncodingPane;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;

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
public class SingleUnitView extends JPanel
implements ActionListener, DataModelListener, DataView {
    DataEncodingPane box;
    DataFieldEditorWithDataModelPopup textView;
    InfoView infoView;
    DataModel dataModel;
    
    /******************************************************************************
     *	Constructors
     */
    public SingleUnitView() {
        this(null);
    }
    
    public SingleUnitView(String label) {
        super();
        dataModel = new DataModel();
        dataModel.addDataModelListener(this);
        
        DataWorkshop options = DataWorkshop.getInstance();
        
        box = new DataEncodingPane(label);
        box.setDataEncoding(options.getDefaultDataConverter());
        box.addActionListener(this);
        
        DataEncodingField field = new DataEncodingField();
        field.setLabel("");
        field.setDataConverter(options.getDefaultDataConverter());
        field.setBitSize(dataModel.getBitSize());

        DataViewOption viewOptions = new DataViewOption();
        
        //Build TextPane
        textView = new DataFieldEditorWithDataModelPopup(field, viewOptions, dataModel);
        textView.setAutomaticLayout(true);
        textView.render();
        textView.setBorder((new JTextField()).getBorder());
        
        infoView = new InfoView(dataModel);
        
        //Build Main Pane
        setLayout(new BorderLayout());
        add(box, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(0, 6)));
        //add(new JScrollPane(textView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        add(textView);
        add(infoView, BorderLayout.SOUTH);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == box) {
            textView.setDataConverter(box.getDataEncoding());
        }
    }
    
    /******************************************************************************
     *	DataModelListener
     */
    public void selectionChanged(DataSelectionEvent e) {
    }
    
    public void dataChanged(DataChangeEvent e) {
        if (e.wasResized()) {
            textView.setBitSize(getDataModel().getBitSize());
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public boolean hasDataViewFocus() {
        return dataModel.hasDataViewFocus(this);
    }
    
     public BitRange getValidBitRange(BitRange bitRange) {
        return textView.getValidBitRange(bitRange);
     }
    
    public void setDataConverter(DataEncoding unit) {
        box.setDataEncoding(unit);
        textView.setDataConverter(unit);
    }
    
    public DataEncoding getDataConverter() {
        return box.getDataEncoding();
    }
    
    public void setDataModel(DataModel model) {
        dataModel.removeDataModelListener(this);
        dataModel = model;
        dataModel.addDataModelListener(this);
        textView.setDataModel(dataModel);
        textView.setBitSize(dataModel.getBitSize());
        infoView.setDataModel(dataModel);
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
}