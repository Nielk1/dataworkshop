package dataWorkshop.gui.data.encoding;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.editor.MyKeyBinding;
import dataWorkshop.gui.dialogs.*;

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
public class DataEncodingField extends JTextField implements LocaleStrings {
    
    final DataEncoding dataEncoding;
    
    Color unvalidatedColor = Color.red;
    Color validatedColor = (new JTextField()).getBackground();
    
    Data validatedData;
    boolean hasEdits = false;
    
    static char[] VALID_CHARS = {
        '!','"','#','$','%','&','\'','(',')','*','+',',','-','.','/',
        '0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?',
        '@','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
        'P','Q','R','S','T','U','V','W','X','Y' ,'Z','[','\\',']','^','_',
        '`','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
        'p','q','r','s','t','u','v','w','x','y','z','{','|','}','~','?'
    };
    
    static HashMap characterActions = new HashMap();
    static {
        //Create the actions for all the characters
        for (int i = 0; i < VALID_CHARS.length; i++) {
            Action a = new AbstractAction(Character.toString(VALID_CHARS[i])) {
                public void actionPerformed(ActionEvent e) {
                    DataEncodingField field = (DataEncodingField) e.getSource();
                    field.replaceChar(((String) getValue(Action.NAME)).charAt(0));
                    field.fireActionEvent(NEXT_INPUT_POINT_ACTION);
                }
            };
            a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VALID_CHARS[i]));
            characterActions.put(a.getValue(Action.NAME), a);
        }
    }
    
    public static String NEXT_INPUT_POINT_ACTION = "next-input-point";
    public static String PREVIOUS_INPUT_POINT_ACTION = "previous-input-point";
    public static String DELETE_NEXT_CHARACTER_ACTION = "delete-next-character";
    public static String DELETE_PREVIOUS_CHARACTER_ACTION = "delete-previous-character";
    
    static MyKeyBinding[] DEFAULT_KEY_BINDINGS = {
        new MyKeyBinding(NEXT_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)),
        new MyKeyBinding(PREVIOUS_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
        new MyKeyBinding(DELETE_NEXT_CHARACTER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)),
        new MyKeyBinding(DELETE_PREVIOUS_CHARACTER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0)),
    };
    
    static HashMap actions = new HashMap();
    static {
        Action a;
        a = new AbstractAction(NEXT_INPUT_POINT_ACTION) {
            public void actionPerformed(ActionEvent e) {
                DataEncodingField field = (DataEncodingField) e.getSource();
                DataEncoding converter = field.getDataEncoding();
                int newDot = converter.nextInputPoint(field.getCaretPosition());
                if (newDot != -1) {
                    field.setCaretPosition(newDot);
                }
            }
        };
        actions.put(a.getValue(Action.NAME), a);
        
         a = new AbstractAction(PREVIOUS_INPUT_POINT_ACTION) {
            public void actionPerformed(ActionEvent e) {
                DataEncodingField field = (DataEncodingField) e.getSource();
                DataEncoding converter = field.getDataEncoding();
                int newDot = converter.previousInputPoint(field.getCaretPosition());
                if (newDot != -1) {
                    field.setCaretPosition(newDot);
                }
            }
        };
        actions.put(a.getValue(Action.NAME), a);
        
        a = new AbstractAction(DELETE_NEXT_CHARACTER_ACTION) {
            public void actionPerformed(ActionEvent e) {
                DataEncodingField field = (DataEncodingField) e.getSource();
                field.deleteChar();
                field.fireActionEvent(NEXT_INPUT_POINT_ACTION);
            }
        };
        actions.put(a.getValue(Action.NAME), a);
        
        a = new AbstractAction(DELETE_PREVIOUS_CHARACTER_ACTION) {
            public void actionPerformed(ActionEvent e) {
                DataEncodingField field = (DataEncodingField) e.getSource();
                DataEncoding converter = field.getDataEncoding();
                int newDot = converter.previousInputPoint(field.getCaretPosition());
                if (newDot != -1) {
                    field.setCaretPosition(newDot);
                    field.deleteChar();
                }
            }
        };
        actions.put(a.getValue(Action.NAME), a);
    }
    
     static {
        setKeyBindings(DEFAULT_KEY_BINDINGS);
    }
    
    /******************************************************************************
     *	Constructors
     */
     public DataEncodingField(DataEncoding converter) {
        super();
        
        this.dataEncoding = converter;
        this.validatedData = new Data(converter.getBitSize(), 0);
        setSelectionColor(null);
        setHorizontalAlignment(JTextField.LEFT);
        setText(new String(converter.encode(validatedData)));
        setFont(Editor.getInstance().getPlainFont());
        /**
         * Prevent mouse from setting caret in invalid position
         *
         *  :TODO: MPA 2002-09-05
         *  mousedragged events still can move the caret to invalid position
         *  via setDot(int, Position.Bias) which I am unable to override
         */
        setCaret(new DefaultCaret() {
            public void setDot(int dot) {
                if (((DataEncodingField) getComponent()).getDataEncoding().isInputPoint(dot)) {;
                    super.setDot(dot);
                }
            }
        });
		/**
		* :KLUDGE:Martin Pape:May 16, 2003
		* For some strange reasons the blinkrate is zero
		*/ 
		getCaret().setBlinkRate((new JTextField()).getCaret().getBlinkRate());
        
        //Register action and keys
        setActionMap(new ActionMap());
        setInputMap(JComponent.WHEN_FOCUSED, new InputMap());
        Iterator it = actions.values().iterator();
        while (it.hasNext()) {
            registerAction((Action) it.next());
        }
        it = characterActions.values().iterator();
        while (it.hasNext()) {
            registerAction((Action) it.next());
        }
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public static MyKeyBinding[] getKeyBindings() {
        Action[] a = (Action[]) (new ArrayList(actions.values())).toArray(new Action[0]);
        MyKeyBinding[] bindings = new MyKeyBinding[a.length];
        for (int i = 0; i < a.length; i++) {
            bindings[i] = new MyKeyBinding(a[i]);
        }
        return bindings;
    }
    
    public static void setKeyBindings(MyKeyBinding[] bindings) {
        for (int i = 0; i < bindings.length; i++) {
            Action a = (Action) actions.get(bindings[i].getActionName());
            if (a != null) {
                a.putValue(Action.ACCELERATOR_KEY, bindings[i].getKeyStroke());
            }
        }
    }
    
    public void registerAction(Action a) {
        InputMap keyMap = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap map = getActionMap();
        keyMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), (String) a.getValue(Action.NAME));
        map.put((String) a.getValue(Action.NAME), a);
    }
    
    public DataEncoding getDataEncoding() {
        return dataEncoding;
    }
    
    public void commitEdits() {
        if (hasEdits()) {
            hasEdits = false;
            try {
                Data d = dataEncoding.decode(getText().toCharArray());
                setData(d);
            }
            catch (DataEncodingException e) {
                setData(validatedData);
                ErrorDialogFactory.show(DATA_CONVERTER_ERROR, e, this);
            }
        }
    }
    
    public void cancelEdits() {
    	if (hasEdits()) {
    		setData(validatedData);
    	}
    }
    
    public boolean hasEdits() {
        return hasEdits;
    }
    
    public void deleteChar() {
        replaceChar(getDataEncoding().getDeleteChar());
    }
    
    public void replaceChar(char c) {
        int dot = getCaretPosition();
        hasEdits = true;
        char[] s = getText().toCharArray();
        s[dot] = c;
        setText(new String(s));
        setCaretPosition(dot);
    }
    
    public void paint(Graphics g) {
        if (hasEdits()) {
            setBackground(unvalidatedColor);
        }
        else {
            setBackground(validatedColor);
        }
        super.paint(g);
    }
    
    public void setData(Data data) {
        int dot = getCaretPosition();
        /**
         *  :NOTE: (MPA 2002-09-05)
         *
         *  The TextField-preferredsize does not include the caret widtg
         *  and if the caret is place at the end field the field is scrolled
         *  by the caret width to the right.
         *  when calling setData() the caret position is saved and restored. If during
         *  this process the caret happens to be at the end of the TextField the
         *  field is shifted back and forth which gives a blurred expression.
         *  So we make sure the caret is not at the end of the field.
         *
         */
        if (!dataEncoding.isInputPoint(dot)) {
            dot = dataEncoding.previousInputPoint(dot);
        }
        setText(new String(dataEncoding.encode(data)));
        validatedData = data;
        setCaretPosition(dot);
    }
    
    public Data getData() {
        commitEdits();
        return validatedData;
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    protected void fireActionEvent(String action) {
        ((Action) actions.get(action)).actionPerformed(new ActionEvent(this, 0 ,action));
    }
}