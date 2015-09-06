package dataWorkshop.gui.dialogs;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.NumberPane;
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
public class NumberDialog extends JDialog implements LocaleStrings {
    
    NumberPane numberPane;
    JOptionPane optionPane;
    
    /******************************************************************************
     *	Constructors
     */
    public NumberDialog(Frame owner, String message, String numberLabel, long number, IntegerFormat integerFormat) {
        super(owner, "Change Number", true);
        
        numberPane = new NumberPane(integerFormat, numberLabel);
        numberPane.setValue(number);
        Object[] array = {message, numberPane};
        String[] buttons = {OK_BUTTON_NAME, SET_TO_ZERO_BUTTON_NAME};
        optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_OPTION,
            null, buttons, buttons[0]) {
            
            public int getMaxCharactersPerLineCount() {
                return DataWorkshop.MAX_CHARACTERS_PER_LINE_IN_DIALOG;
            }
        };
        
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                 Object value = optionPane.getValue();
                 /**
                 *:TRICKY: MPA 2002-10-03
                 * Reset the value so the next time a property change event is fired even if the user clicks on the same button again
                 */
                 optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                 if (value == SET_TO_ZERO_BUTTON_NAME) {
                    numberPane.setValue(0);
                 }
                setVisible(false);
            }
        });
        
        setContentPane(optionPane);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setMinimum(long minimum) {
    	numberPane.setMinimum(minimum);
    }
    
    public void setMaximum(long maximum) {
    	numberPane.setMaximum(maximum);
    }
    
    public long getNumber() {
        return numberPane.getValue();
    }
}
