package dataWorkshop.gui.data.encoding;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.encoding.Base64;
import dataWorkshop.data.encoding.EBCDIC;
import dataWorkshop.data.encoding.IEEE754Decoded;
import dataWorkshop.data.encoding.IEEE754Raw;
import dataWorkshop.data.encoding.IntegerEncoding;
import dataWorkshop.data.encoding.MSDOSTimeDate;
import dataWorkshop.data.encoding.TimeInMillis;
import dataWorkshop.data.encoding.USAscii;
import dataWorkshop.gui.ActionPane;

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
public class DataEncodingPane extends ActionPane implements ActionListener {
    
    JLabel label;
    JComboBox classBox;
    DataEncodingConfigurationPane configurationPane;
    
    public HashMap converterMap = new HashMap();
    
    /******************************************************************************
     *	Constructors
     */
    public DataEncodingPane() {
        this(null);
    }
    
    public DataEncodingPane(String labelName) {
        super();
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        converterMap.put(IntegerEncoding.NAME, new IntegerConfigurationPane());
        converterMap.put(USAscii.NAME, new USAsciiConfigurationPane());
        converterMap.put(EBCDIC.NAME,  new EBCDICConfigurationPane());
        converterMap.put(TimeInMillis.NAME,  new TimeInMillisConfigurationPane());
        converterMap.put(Base64.NAME,  new Base64ConfigurationPane());
        converterMap.put(IEEE754Raw.NAME, new IEEE754RawConfigurationPane());
        converterMap.put(IEEE754Decoded.NAME, new IEEE754DecodedConfigurationPane());
        converterMap.put(MSDOSTimeDate.NAME, new MSDOSTimeDateConfigurationPane());
        
        classBox = new JComboBox(converterMap.keySet().toArray(new String[0]));
        classBox.setMaximumSize(classBox.getMinimumSize());
        classBox.addActionListener(this);
        
        configurationPane = (DataEncodingConfigurationPane) converterMap.get(classBox.getSelectedItem());
        configurationPane.addActionListener(this);
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (labelName != null) {
            label = new JLabel(labelName);
            add(label);
            add(Box.createRigidArea(new Dimension(6, 0)));
        }
        add(classBox);
        add(configurationPane);
        setBorder(null);
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        configurationPane.removeActionListener(this);
        remove(configurationPane);
        configurationPane = (DataEncodingConfigurationPane) converterMap.get(classBox.getSelectedItem());
        add(configurationPane);
        configurationPane.addActionListener(this);
        revalidate();
        fireActionEvent();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setEnabled(boolean enabled) {
        classBox.setEnabled(enabled);
        configurationPane.setEnabled(enabled);
        if (label != null) {
            label.setEnabled(enabled);
        }
    }
    
    public DataEncoding getDataEncoding() {
        return configurationPane.getDataEncoding();
    }
    
    public void setDataEncoding(DataEncoding converter) {
        classBox.setSelectedItem(converter.getName());
        configurationPane.setDataEncoding(converter);
    }
}