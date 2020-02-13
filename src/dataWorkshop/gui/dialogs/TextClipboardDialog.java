package dataWorkshop.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.gui.MyPopupMenu;
import dataWorkshop.gui.data.encoding.DataEncodingPane;
import dataWorkshop.gui.editor.Editor;

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
public class TextClipboardDialog extends DialogWindow implements LocaleStrings {
    
    public final static String CLASS_NAME = "TextClipboardDialog";
    
    JTextArea textArea;
    JCheckBox spaceBox;
    JCheckBox newLineBox;
    DataEncodingPane converterBox;
    
    Object[][] filterChars = {
        {"space", " "},
        {"new-line", "\n"}
    };
    
    JCheckBox[] filterCharBoxes;

	public JButton clearButton = new JButton(CLEAR_BUTTON_NAME);    
    public JButton generateDataButton = new JButton(CONVERT_TO_DATA_BUTTON_NAME);
    final JButton[] buttons = {clearButton, generateDataButton, closeButton};
    
    private static TextClipboardDialog instance;
    
    /******************************************************************************
     *	Constructors
     */
    public TextClipboardDialog() {
        super(TEXT_CLIPBOARD_DIALOG_TITLE, false);
    }
    
    /******************************************************************************
     *	XMLSerializable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    /******************************************************************************
     *	ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == clearButton) {
        	textArea.setText(new String());
        }	
        else if (source == generateDataButton) {
            String text = textArea.getText();
            StringBuffer buffer = new StringBuffer(text);
            for (int i = 0; i < filterCharBoxes.length; i++) {
                if (filterCharBoxes[i].isSelected()) {
                    int index = buffer.indexOf((String) filterChars[i][1]);
                    while (index != -1) {
                        buffer.deleteCharAt(index);
                        index = buffer.indexOf((String) filterChars[i][1], index);
                    }
                }
            }
            
            DataEncoding converter = converterBox.getDataEncoding();
            if (buffer.length() % converter.getDotSize() != 0) {
                Object[] args = new Object[2];
                args[0] = Integer.valueOf(converter.getDotSize());
                args[1] = Integer.valueOf(converter.getDotSize() - (buffer.length() % converter.getDotSize()));
				ErrorDialogFactory.show(DATA_CONVERTER_NOT_ENOUGH_CHARS_MESSAGE.format(args), this);
            }
            else {
                try {
                    Data data = converter.decode(buffer.toString().toCharArray(), 0, buffer.length());
                    DataClipboardDialog.getInstance().setData(data);
					//Display the result to the user
					Editor.getInstance().fireActionEvent(Editor.DATA_CLIPBOARD_ACTION);
                }
                catch (DataEncodingException ee) {
					ErrorDialogFactory.show(DATA_CONVERTER_ERROR, ee, this);
                }
            }
        }
        else {
            super.actionPerformed(e);
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static void rebuild() {
        instance = null;
    }
    
    public static TextClipboardDialog getInstance() {
        if (instance == null) {
            instance = new TextClipboardDialog();
            instance.buildDialog();
        }
        return instance;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    private void buildDialog() {
        textArea = new JTextArea();
        JPopupMenu popup = new JPopupMenu();
		HashMap actionMap = new HashMap();
        Action[] actions = textArea.getActions();
        for (int i = 0; i < actions.length; i++) {
        	actionMap.put(actions[i].getValue(Action.NAME), actions[i]);
        }
        /**
         * :KLUDGE:Martin Pape:Jun 21, 2003
         *	This does not seem like a safe way to get the actions
         */
        Action a = (Action) actionMap.get("cut-to-clipboard");
        a.putValue(Action.NAME, "Cut");
        popup.add(a);
		a = (Action) actionMap.get("copy-to-clipboard");
		a.putValue(Action.NAME, "Copy");
		popup.add(a);
		a = (Action) actionMap.get("paste-from-clipboard");
		a.putValue(Action.NAME, "Paste");
		popup.add(a);
        
        MyPopupMenu popupMenu = new MyPopupMenu(popup);
        popupMenu.addComponent(textArea);
        
        JPanel filterPane = new JPanel();
        filterPane.setLayout(new BoxLayout(filterPane, BoxLayout.X_AXIS));
        filterPane.add(new JLabel("Ignore"));
		filterPane.add(Box.createRigidArea(new Dimension(6, 0)));
        
        filterCharBoxes = new JCheckBox[filterChars.length];
        for (int i = 0; i < filterCharBoxes.length; i++) {
            filterCharBoxes[i] = new JCheckBox((String) filterChars[i][0]);
            filterPane.add(filterCharBoxes[i]);
        }
        
		converterBox = new DataEncodingPane(DATA_ENCODING);
        
        JPanel northPane = new JPanel();
        northPane.setLayout(new BoxLayout(northPane, BoxLayout.Y_AXIS));
        northPane.add(filterPane);
        northPane.add(converterBox);
        
        JPanel main = getMainPane();
        main.setLayout(new BorderLayout());
        main.add(northPane, BorderLayout.NORTH);
        main.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        setButtons(buttons);
        setDefaultButton(buttons[1]);
        setCancelButton(buttons[2]);
        
        pack();
    }
}
