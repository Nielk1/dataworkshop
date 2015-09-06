/**
 *	@version 1.0 2000/07/27
 * 	@author martin.pape@gmx.net
 *
 *  
 */

package dataWorkshop.gui.data.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.encoding.DataEncodingPane;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;

/**
 *  When this Component is resized the DataFieldView gets adjusted to show
 *   the maximum amount of data
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
public class SingleLineView extends JPanel
    implements ActionListener, ComponentListener, DataModelListener, DataView
{
    DataEncodingPane unitBox;
    DataFieldEditor textView;
    DataModel dataModel;
    boolean followDataModelSelectionChange;

    /******************************************************************************
     *	Constructors
     */
    public SingleLineView(DataModel dataModel) {
        this(DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED), dataModel, true);
    }
    
    public SingleLineView(DataEncoding converter, DataModel dataModel, boolean useEditorDataModel) {
        super();
        this.dataModel = dataModel;
        this.followDataModelSelectionChange = true;
        unitBox = new DataEncodingPane();
        unitBox.setMaximumSize(unitBox.getPreferredSize());
        unitBox.setDataEncoding(converter);
        unitBox.addActionListener(this);
        
        DataEncodingField field = new DataEncodingField();
        field.setLabel("");
        field.setDataConverter(converter);
        field.setBitOffset(dataModel.getSelectionOffset());
        
        DataViewOption viewOptions = new DataViewOption();
        viewOptions.setLinesPerPage(1);
        
        if (useEditorDataModel) {
            textView = new DataFieldEditorWithEditorPopup(field, viewOptions, dataModel);
        }
        else {
            textView = new DataFieldEditorWithDataModelPopup(field, viewOptions, dataModel);
        }
        textView.setBorder((new JTextField()).getBorder());
        textView.displayMaxDataInOneLine();
        textView.render();
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(unitBox);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(textView);
        
        dataModel.addDataModelListener(this);
        addComponentListener(this);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == unitBox) {
            textView.setDataConverter(unitBox.getDataEncoding());
            /**
             * :TRICKY:Martin Pape:Sep 20, 2003
             *	The new DataEncodingPane might consume a different amount of space than the old one
             *  so we have to force the resizing of the components before calculating the maximal 
             *  data we can display in one line
             **/
            validate();
            textView.displayMaxDataInOneLine();
        }
    }
    
    /******************************************************************************
     *	Component Listener
     */
    public void componentShown(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
    }
    
    public void componentResized(ComponentEvent e) {
        textView.displayMaxDataInOneLine();
    }
    
    public void componentHidden(ComponentEvent e) {
    }
    
    /******************************************************************************
     *	DataModelListener Interface
     */
    public void selectionChanged(DataSelectionEvent e) {
        if (!textView.hasDataViewFocus() & followDataModelSelectionChange) {
            textView.setBitOffset(e.getNewBitRange().getStart());
        }
    }
    
    public void dataChanged(DataChangeEvent e) {
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
        unitBox.setDataEncoding(unit);
    }
    
    public DataEncoding getDataConverter() {
        return unitBox.getDataEncoding();
    }
    
    public boolean isFollowDataModelSelectionChange() {
        return followDataModelSelectionChange;
    }
    
    public void setFollowDataModelSelectionChange(boolean follow) {
        this.followDataModelSelectionChange = follow;
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textView.setEnabled(enabled);
    }
    
    public void setDataModel(DataModel s) {
        dataModel.removeDataModelListener(this);
        dataModel = s;
        dataModel.addDataModelListener(this);
        textView.setDataModel(dataModel);
        //textView.setEditable(true);
        textView.setEnabled(true);
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
}