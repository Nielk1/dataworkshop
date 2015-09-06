package dataWorkshop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import dataWorkshop.number.IntegerFormat;

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
public class CheckBoxNumberPane extends ActionPane 
    implements ActionListener {
    
    JCheckBox checkBox;
    NumberPane numberPane;
    
    /**
     * true - selectedCheckBox means enabled numberPane
     * false - selectedCheckBox means disabled numberPane
     */
    boolean checkBoxEnablesNumberPane = true;
    
    /******************************************************************************
     *	Constructors
     */
    public CheckBoxNumberPane(IntegerFormat numberFormat, String label) {
    	this(numberFormat, label, true);
    }
    
    public CheckBoxNumberPane(IntegerFormat numberFormat, String label, boolean checkBoxEnablesNumberPane) {
        super();
        this.checkBoxEnablesNumberPane = checkBoxEnablesNumberPane;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        checkBox = new JCheckBox(label);
        checkBox.addActionListener(this);
        numberPane = new NumberPane(numberFormat);
        numberPane.addActionListener(this);                
        add(checkBox);
        add(numberPane);
    }
    
	/******************************************************************************
	   *	Action Listener
	   */
	  public void actionPerformed(ActionEvent e) {
		  Object source = e.getSource();
			if (source == checkBox) {
				if (checkBoxEnablesNumberPane) {
			   		numberPane.setEnabled(checkBox.isSelected());
				}
				else {
					numberPane.setEnabled(!checkBox.isSelected());	
				}
			}
		  else if (source == numberPane) {
		  		fireActionEvent();
		  }
	  }
	  
    /******************************************************************************
     *	Public Methods
     */
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
		checkBox.setEnabled(enabled);
		if (enabled) {
			if (checkBox.isSelected() && checkBoxEnablesNumberPane) {
				numberPane.setEnabled(true);
			}
			else if (!checkBox.isSelected() && !checkBoxEnablesNumberPane) {
				numberPane.setEnabled(true);
			}
		}
		else {
			numberPane.setEnabled(false);
		}
    }
    
    public void setMinimum(long min) {
    	numberPane.setMinimum(min);
    }
    
    public boolean isSelected() {
        return checkBox.isSelected();
    }
    
    public void setSelected(boolean selected) {
        checkBox.setSelected(selected);
        numberPane.setEnabled(selected);
    }
    
    public long getValue() {
        return numberPane.getValue();
    }
    
    public void setValue(long value) {
        numberPane.setValue(value);
    }
    
    public void setStepSize(int value) {
    	numberPane.setStepSize(value);
    }
}