package dataWorkshop.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * Wrapper around JComboBox, adding a label in front of JComboBox
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
public class ComboPane extends ActionPane
implements ActionListener {
    JComboBox box;
    JLabel label;
    
    /******************************************************************************
     *	Constructors
     */
    public ComboPane() {
        this(new Object[0]);
    }
    
    public ComboPane(Object[] items) {
        this(items, null);
    }
    
    public ComboPane(Object[] items, String labelName) {
        super();
        
        box = new JComboBox(items);
        box.addActionListener(this);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (labelName != null) {
            label = new JLabel(labelName);
            //l.setAlignmentX(Container.LEFT_ALIGNMENT);
            add(label);
            add(Box.createRigidArea(new Dimension(6, 0)));
        }
        add(box);
        setItems(items);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        fireActionEvent();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setEnabled(boolean enabled) {
        label.setEnabled(enabled);
        box.setEnabled(enabled);
    }
    
    public Object getSelectedItem() {
        return box.getSelectedItem();
    }
    
    public int getSelectedIndex() {
        return box.getSelectedIndex();
    }
    
    public void setSelectedItem(Object o) {
        box.setSelectedItem(o);
    }
    
    public void setItems(Object[] items) {
        box.removeAllItems();
        for (int i = 0; i < items.length; i++) {
            box.addItem(items[i]);
        }
		setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), (int) getPreferredSize().getHeight()));
    }
}