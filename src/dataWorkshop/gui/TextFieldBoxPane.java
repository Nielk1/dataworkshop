package dataWorkshop.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

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
public class TextFieldBoxPane extends ActionPane
implements ActionListener {
    
    JComboBox box;
    JLabel label;
    int historyLength;
    
    /******************************************************************************
     *	Constructors
     */
    public TextFieldBoxPane() {
        this(new Object[0]);
    }
    
    public TextFieldBoxPane(Object[] history) {
        this(history, null);
    }
    
    public TextFieldBoxPane(Object[] history, String labelName) {
        super();
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        box = new JComboBox(history);
        box.setEditable(true);
        box.addActionListener(this);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (labelName != null) {
            label = new JLabel(labelName);
            add(label);
            add(Box.createRigidArea(new Dimension(6, 0)));
        }
        add(box);
        setBorder(null);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        ArrayList items = new ArrayList();
        Object selectedItem = box.getSelectedItem();
        int size = box.getItemCount();
        for (int i = 0; i < size; i++) {
            items.add(box.getItemAt(i));
        }
        if (!items.contains(selectedItem)) {
            items.add(selectedItem);
            Collections.sort(items);
            box.removeAllItems();
            for (int i = 0; i < items.size(); i++) {
                box.addItem(items.get(i));
            }
            box.setSelectedItem(selectedItem);
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setHistoryLength(int historyLength) {
        this.historyLength = historyLength;
    }
    
    public void setEnabled(boolean enabled) {
        label.setEnabled(enabled);
        box.setEnabled(enabled);
    }
    
    public String getText() {
        return (String) box.getSelectedItem();
    }
    
    public void setText(String text) {
    	box.setSelectedItem(text);
    }
    
    public String[] getHistory() {
        ArrayList items = new ArrayList();
        int size = box.getItemCount();
        for (int i = 0; i < size; i++) {
            items.add(box.getItemAt(i));
        }
        return (String[]) items.toArray(new String[0]);
    }
    
    public void setHistory(String[] history) {
        box.removeAllItems();
        java.util.List list = Arrays.asList(history);
        Collections.sort(list);
        for (int i = 0; i < history.length; i++) {
            box.addItem(list.get(i));
        }
    }
}