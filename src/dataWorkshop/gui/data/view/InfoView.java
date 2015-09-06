package dataWorkshop.gui.data.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.BitRange;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
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
public class InfoView extends JPanel
implements ActionListener, LocaleStrings, DataModelListener {
    
    NumberPane rangeField;
    NumberPane offsetField;
    NumberPane fileSizeField;
    
    boolean isFileSizeEditable = true;
    
    DataModel dataModel;
    
    Border loweredBorder = BorderFactory.createLoweredBevelBorder();
    
    /******************************************************************************
     *	Constructors
     */
    public InfoView(DataModel dataModel) {
        super();
        this.dataModel = dataModel;
        
        DataWorkshop options = DataWorkshop.getInstance();
        
        offsetField = new NumberPane(options.getUnsignedOffsetNumber(), OFFSET_LABEL);
        //PENDING: Not necessary ?
        offsetField.setMinimum(0);
        offsetField.setStepSize(DataWorkshop.OFFSET_STEP_SIZE);
        offsetField.addActionListener(this);
        
        rangeField = new NumberPane(options.getUnsignedOffsetNumber(), RANGE_LABEL);
        //PENDING: Not necessary ?
        rangeField.setMinimum(0);
        rangeField.setStepSize(DataWorkshop.OFFSET_STEP_SIZE);
        rangeField.addActionListener(this);
        
        fileSizeField = new NumberPane(options.getUnsignedOffsetNumber(), SIZE_LABEL);
        //PENDING: Not necessary ?
        fileSizeField.setMinimum(0);
        fileSizeField.setStepSize(DataWorkshop.OFFSET_STEP_SIZE);
        fileSizeField.addActionListener(this);
        
        JPanel infoPane = new JPanel();
        infoPane.add(Box.createHorizontalGlue());
        infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.X_AXIS));
        infoPane.add(offsetField);
        infoPane.add(Box.createRigidArea(new Dimension(12, 0)));
        infoPane.add(rangeField);
        infoPane.add(Box.createRigidArea(new Dimension(12, 0)));
        infoPane.add(fileSizeField);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(infoPane);
        if (dataModel != null) {
            dataModel.addDataModelListener(this);
        }
        setDataModel(dataModel);
    }
    
    /******************************************************************************
     *	ActionListener Interface
     */
    public void actionPerformed(ActionEvent e) {
        //dataModel.requestDataViewFocus(this);
        Object source = e.getSource();
        if (source == rangeField) {
            dataModel.setSelectionSize(rangeField.getValue());
        } else if (source == offsetField) {
            dataModel.setSelectionOffset(offsetField.getValue());
        } else if (source == fileSizeField) {
            dataModel.setDataSize(fileSizeField.getValue());
        }
    }
    
    /******************************************************************************
     *	DataModelListener Interface
     */
    public void selectionChanged(DataSelectionEvent e) {
        BitRange bitRange = e.getNewBitRange();
        
        //disable the Listener, because the ActionEvent will generate a new DataSelectionEvent
        rangeField.removeActionListener(this);
        offsetField.removeActionListener(this);
        rangeField.setValue(bitRange.getSize());
        offsetField.setValue(bitRange.getStart());
        
        rangeField.addActionListener(this);
        offsetField.addActionListener(this);
    }
    
    public void dataChanged(DataChangeEvent e) {
		fileSizeField.removeActionListener(this);
         //Bitsize may have changed
         fileSizeField.setValue(getDataModel().getBitSize());
		fileSizeField.addActionListener(this);
    }
    
    /******************************************************************************
     *	DataSelection Interface
     */
    public void setDataModel(DataModel s) {
        dataModel.removeDataModelListener(this);
        dataModel = s;
        rangeField.setEditable(true);
        offsetField.setEditable(true);
        if (isFileSizeEditable()) {
            fileSizeField.setEditable(true);
        }
        rangeField.setValue(dataModel.getSelectionSize());
        offsetField.setValue(dataModel.getSelectionOffset());
        fileSizeField.setValue(dataModel.getBitSize());
        dataModel.addDataModelListener(this);
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        rangeField.setEnabled(enabled);
        offsetField.setEnabled(enabled);
        fileSizeField.setEnabled(enabled);
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
    
    public boolean isFileSizeEditable() {
        return isFileSizeEditable;
    }
    
    public void setFileSizeEditable(boolean editable) {
        isFileSizeEditable = editable;
        if (!isFileSizeEditable) {
            fileSizeField.setEditable(false);
        }
    }
}