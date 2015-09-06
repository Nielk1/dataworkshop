package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import dataWorkshop.LocaleStrings;

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
public class ListDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "ListDialog";
    
    final JButton[] buttons = {okButton};
    
    JList list;
    private static ListDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public ListDialog() {
        super("Placeholder", true);
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
    
    public static ListDialog getInstance() {
        if (instance == null) {
            instance = new ListDialog();
              instance.buildDialog();
        }
        return instance;
    }
    
    public Object show(Frame owner, String title, Object[] items, Object defaultItem) {
        setLocationRelativeTo(owner);
        setTitle(title);
        list.setListData(items);
        list.setSelectedValue(defaultItem, true);
        setSize(getPreferredSize());
        
        this.setVisible(true);
        
       	Object[] o = list.getSelectedValues();
       	list.setListData(new Object[0]);
        return o[0];
    }
    
     /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
        
        list = new JList();
		MouseListener mouseListener = new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int index = list.locationToIndex(e.getPoint());
					list.setSelectedIndex(index);
				} else if (e.getClickCount() == 2) {
					 int index = list.locationToIndex(e.getPoint());
					 list.setSelectedIndex(index);
					 setVisible(false);
				  }
			 }
		 };
		 list.addMouseListener(mouseListener);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  
        JPanel pane = getMainPane();
        pane.setLayout(new BorderLayout());
        pane.add(new JScrollPane(list), BorderLayout.CENTER);
        
        setButtons(buttons);
        setDefaultButton(buttons[0]);
    }
}
