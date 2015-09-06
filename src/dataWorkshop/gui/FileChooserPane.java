package dataWorkshop.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataWorkshop.LocaleStrings;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;

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
public class FileChooserPane extends JPanel implements ActionListener, LocaleStrings {
    JTextField field;
    JButton fileChooserButton;
    JFileChooser fileChooser;
    
    private HashSet stateChangeListener = new HashSet();
    
    /******************************************************************************
     *	Constructors
     */
    public FileChooserPane(String label) {
        super();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        fileChooser = new JFileChooser();
        
        field = new JTextField();
        field.addActionListener(this);
        fileChooserButton = new JButton("...");
        fileChooserButton.addActionListener(this);
        
        if (label != null) {
            add(new JLabel(label + " "), BorderLayout.WEST);
        }
        add(field, BorderLayout.CENTER);
        add(fileChooserButton, BorderLayout.EAST);
    }
    
    /******************************************************************************
     *	Action Listener Interface
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileChooserButton) {
            File file = getFile();
            //If there was no previous file we drop the user into his home directory
            if (file == null) {
                file = new File(System.getProperty("user.home"));
            }
            fileChooser.setCurrentDirectory(file.getParentFile());
            fileChooser.setSelectedFile(file);
            fileChooser.rescanCurrentDirectory();
            int i = fileChooser.showSaveDialog(null);
            if (i == JFileChooser.APPROVE_OPTION) {
                setFile(fileChooser.getSelectedFile());
            }
        }
        fireStateChanged();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public File getFile() {
        return new File(field.getText());
    }
    
    public void setFile(File file) {
        field.setText(file.getPath());
    }
    
    public boolean hasFile() {
        return !(field.getText().equals(new String()));
    }
    
    public void addStateChangeListener(StateChangeListener l) {
        stateChangeListener.add(l);
    }
    
    public void removeStateChangeListener(StateChangeListener l) {
        stateChangeListener.remove(l);
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    protected void fireStateChanged() {
        Object[] listeners = stateChangeListener.toArray();
        StateChangeEvent changeEvent = new StateChangeEvent(this);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((StateChangeListener) listeners[i]).stateChanged(changeEvent);
        }
    }
}