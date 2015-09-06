package dataWorkshop.gui.data.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.PlainDocument;

import dataWorkshop.LocaleStrings;
import dataWorkshop.Utils;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataEncodingFieldLayout;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.dialogs.ErrorDialogFactory;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.editor.MyKeyBinding;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.logging.Logger;

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
public class DataFieldTextPane extends JPanel implements DataModelListener, DataView, LocaleStrings
{

	protected boolean automaticLayout = false;
	protected boolean hasEdits = false;

	protected DataModel dataModel;
	
	protected DefaultHighlighter.DefaultHighlightPainter editPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
	protected DefaultHighlighter.DefaultHighlightPainter backupSelectionPainter;
	protected DefaultHighlighter.DefaultHighlightPainter selectionPainter;

	private HashSet stateChangeListeners = new HashSet();

	protected DataEncodingFieldLayout fieldLayout;
	protected int viewDotOffset = 0;
	//The absolute Dot Position
	protected int dotPosition = 0;
	protected JTextArea textArea;

	static char[] VALID_CHARS =
		{
			'!',
			'"',
			'#',
			'$',
			'%',
			'&',
			'\'',
			'(',
			')',
			'*',
			'+',
			',',
			'-',
			'.',
			'/',
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			':',
			';',
			'<',
			'=',
			'>',
			'?',
			'@',
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',
			'X',
			'Y',
			'Z',
			'[',
			'\\',
			']',
			'^',
			'_',
			'`',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'o',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'x',
			'y',
			'z',
			'{',
			'|',
			'}',
			'~',
			'?' };

	//Caret movement
	public static String NEXT_INPUT_POINT_ACTION = "next-input-point";
	public static String PREVIOUS_INPUT_POINT_ACTION = "previous-input-point";
	public static String NEXT_INPUT_LINE_ACTION = "next-input-line";
	public static String PREVIOUS_INPUT_LINE_ACTION = "previous-input-line";
	public static String START_OF_INPUT_LINE_ACTION = "start-of-input-line";
	public static String END_OF_INPUT_LINE_ACTION = "end-of-input-line";
	public static String FIRST_INPUT_POINT_ACTION = "first-input-point";
	public static String LAST_INPUT_POINT_ACTION = "last-input-point";
	public static String PAGE_UP_ACTION = "page-up";
	public static String PAGE_DOWN_ACTION = "page-down";

	//delete
	public static String DELETE_NEXT_CHARACTER_ACTION = "delete-next-character";
	public static String DELETE_PREVIOUS_CHARACTER_ACTION = "delete-previous-character";

	//Selection
	public static String EXTEND_SELECTION_RIGHT_ACTION = "extend-selection-right";
	public static String EXTEND_SELECTION_LEFT_ACTION = "extend-selection-left";
	public static String EXTEND_SELECTION_UP_ACTION = "extend-selection-up";
	public static String EXTEND_SELECTION_DOWN_ACTION = "extend-selection-down";
	public static String EXTEND_SELECTION_TO_END_OF_LINE_ACTION = "extend-selection-to-end-of-line";
	public static String EXTEND_SELECTION_TO_START_OF_LINE_ACTION = "extend-selection-to-start-of-line";
	public static String EXTEND_SELECTION_TO_END_OF_FIELD_ACTION = "extend-selection-to-end-of-field";
	public static String EXTEND_SELECTION_TO_START_OF_FIELD_ACTION = "extend-selection-to-start-of-field";
	public static String EXTEND_SELECTION_PAGE_UP_ACTION = "extend-selection-page-up";
	public static String EXTEND_SELECTION_PAGE_DOWN_ACTION = "extend-selection-page-down";

	public static String SELECT_LINE_ACTION = "select-line";
	public static String SELECT_FIELD_ACTION = "select-field";

	static MyKeyBinding[] DEFAULT_KEY_BINDINGS =
		{
			new MyKeyBinding(NEXT_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)),
			new MyKeyBinding(PREVIOUS_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
			new MyKeyBinding(NEXT_INPUT_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
			new MyKeyBinding(PREVIOUS_INPUT_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
			new MyKeyBinding(START_OF_INPUT_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0)),
			new MyKeyBinding(END_OF_INPUT_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_END, 0)),
			new MyKeyBinding(FIRST_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK)),
			new MyKeyBinding(LAST_INPUT_POINT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK)),
			new MyKeyBinding(DELETE_NEXT_CHARACTER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)),
			new MyKeyBinding(DELETE_PREVIOUS_CHARACTER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0)),
			new MyKeyBinding(PAGE_UP_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0)),
			new MyKeyBinding(PAGE_DOWN_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0)),
			new MyKeyBinding(EXTEND_SELECTION_RIGHT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_LEFT_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_UP_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_DOWN_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_TO_START_OF_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_TO_END_OF_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_TO_START_OF_FIELD_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.SHIFT_MASK + InputEvent.CTRL_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_TO_END_OF_FIELD_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.SHIFT_MASK + InputEvent.CTRL_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_PAGE_UP_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(EXTEND_SELECTION_PAGE_DOWN_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_MASK)),
			new MyKeyBinding(SELECT_LINE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK)),
			new MyKeyBinding(SELECT_FIELD_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK)),
			};

	static HashMap characterActions = new HashMap();
	static {
		//Create the actions for all the characters
		for (int i = 0; i < VALID_CHARS.length; i++)
		{
			Action a = new AbstractAction(Character.toString(VALID_CHARS[i]))
			{
				public void actionPerformed(ActionEvent e)
				{
					DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
					int dot = textPane.getCaretPosition();
					textPane.scrollDotVisible(dot, 0);
					textPane.replaceChar(((String) getValue(Action.NAME)).charAt(0));
					textPane.fireActionEvent(NEXT_INPUT_POINT_ACTION);
				}
			};
			a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VALID_CHARS[i]));
			characterActions.put(a.getValue(Action.NAME), a);
		}
	}

	static HashMap actions = new HashMap();
	static {
		Action a;
		a = new AbstractAction(NEXT_INPUT_POINT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.nextInputPoint(textPane.getCaretPosition());
				//we have to check if the inputPoint is valid because the field
				// might be larger than the actual data
				if (newDot != -1 && textPane.isInputPoint(newDot))
				{
					textPane.scrollDotVisible(newDot, layout.getLinesPerPage() - 1);
					textPane.setCaretPosition(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(PREVIOUS_INPUT_POINT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.previousInputPoint(textPane.getCaretPosition());
				if (newDot != -1)
				{
					textPane.scrollDotVisible(newDot, 0);
					textPane.setCaretPosition(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(NEXT_INPUT_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.nextInputLine(textPane.getCaretPosition());
				if (newDot != -1)
				{
					//we have to check if the inputPoint is valid because the field
					// might be larger than the actual data
					while (!textPane.isInputPoint(newDot))
					{
						newDot--;
					}
					textPane.scrollDotVisible(newDot, layout.getLinesPerPage() - 1);
					textPane.setCaretPosition(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(PREVIOUS_INPUT_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.previousInputLine(textPane.getCaretPosition());
				if (newDot != -1)
				{
					textPane.scrollDotVisible(newDot, 0);
					textPane.setCaretPosition(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(START_OF_INPUT_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.startOfInputLine(textPane.getCaretPosition());
				if (newDot != -1)
				{
					textPane.scrollDotVisible(newDot, layout.dotToLine(newDot - textPane.getDotOffset()));
					textPane.setCaretPosition(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(END_OF_INPUT_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.endOfInputLine(textPane.getCaretPosition());
				if (newDot == -1)
				{
					newDot = layout.lastInputPoint();
				}
				//we have to check if the inputPoint is valid because the field
				// might be larger than the actual data
				while (!textPane.isInputPoint(newDot))
				{
					newDot--;
				}
				textPane.scrollDotVisible(newDot, layout.dotToLine(newDot - textPane.getDotOffset()));
				textPane.setCaretPosition(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(FIRST_INPUT_POINT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.firstInputPoint();
				textPane.scrollDotVisible(newDot, 0);
				textPane.setCaretPosition(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(LAST_INPUT_POINT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.lastInputPoint();
				textPane.scrollDotVisible(newDot, layout.dotToLine(newDot - textPane.getDotOffset()));
				textPane.setCaretPosition(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(DELETE_NEXT_CHARACTER_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				textPane.deleteChar();
				textPane.fireActionEvent(NEXT_INPUT_POINT_ACTION);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(DELETE_PREVIOUS_CHARACTER_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.previousInputPoint(textPane.getCaretPosition());
				if (newDot != -1)
				{
					textPane.scrollDotVisible(newDot, 0);
					/**
					 * :KLUDGE:
					 *   We have to do it twice, cause the first
					 *  setCaretPosition may change the the bitselection, and the selectionChanged
					 *  event resets the caret to the start of the bitselection. With the
					 *  second setCaretPosition we set the caret to the real position
					 */
					textPane.setCaretPosition(newDot);
					textPane.setCaretPosition(newDot);
					textPane.deleteChar();
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(PAGE_UP_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.previousPage(oldDot);
				if (newDot == -1)
				{
					newDot = layout.firstInputPoint();
				}
				textPane.scrollDotVisible(newDot, layout.dotToLine(oldDot - textPane.getDotOffset()));
				textPane.setCaretPosition(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(PAGE_DOWN_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.nextPage(oldDot);
				if (newDot == -1)
				{
					newDot = layout.lastInputPoint();
				}
				//we have to check if the inputPoint is valid because the field
				// might be larger than the actual data
				while (!textPane.isInputPoint(newDot))
				{
					newDot--;
				}
				textPane.scrollDotVisible(newDot, layout.dotToLine(oldDot - textPane.getDotOffset()));
				textPane.setCaretPosition(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_RIGHT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = oldDot;
				while (newDot != -1 && textPane.isSameUnit(newDot, oldDot))
				{
					newDot = layout.nextInputPoint(newDot);
				}
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_LEFT_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = oldDot;
				while (newDot != -1 && textPane.isSameUnit(newDot, oldDot))
				{
					newDot = layout.previousInputPoint(newDot);
				}
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_UP_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.previousInputLine(oldDot);
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_DOWN_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.nextInputLine(oldDot);
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_PAGE_UP_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.previousPage(oldDot);
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_PAGE_DOWN_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.nextPage(oldDot);
				if (newDot != -1)
				{
					textPane.selectData(newDot);
				}
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_TO_START_OF_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.startOfInputLine(oldDot);
				textPane.selectData(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_TO_END_OF_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int oldDot = textPane.getCaretPosition();
				int newDot = layout.endOfInputLine(oldDot);
				textPane.selectData(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_TO_START_OF_FIELD_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.firstInputPoint();
				textPane.selectData(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(EXTEND_SELECTION_TO_END_OF_FIELD_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				int newDot = layout.lastInputPoint();
				//we have to check if the inputPoint is valid because the field
				// might be larger than the actual data
				while (!textPane.isInputPoint(newDot))
				{
					newDot--;
				}
				textPane.selectData(newDot);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(SELECT_LINE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataEncodingFieldLayout layout = textPane.getDataFieldLayout();
				DataModel dataModel = textPane.getDataModel();
				textPane.fireActionEvent(START_OF_INPUT_LINE_ACTION);
				int dot = textPane.getCaretPosition();
				long bitOffset = layout.dotToBitOffset(dot);
				dataModel.setSelection(bitOffset, Math.min(layout.getBitsPerLine(), layout.getBitRange().getEnd() - bitOffset));
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(SELECT_FIELD_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				DataFieldTextPane textPane = (DataFieldTextPane) e.getSource();
				DataModel dataModel = textPane.getDataModel();
				textPane.fireActionEvent(FIRST_INPUT_POINT_ACTION);
				dataModel.setBitRange(textPane.getDataFieldLayout().getBitRange());
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
	public DataFieldTextPane(DataEncodingFieldLayout fieldLayout, DataModel dataModel)
	{
		super();
		this.fieldLayout = fieldLayout;
		this.dataModel = dataModel;

		textArea = new JTextArea();
		textArea.setLineWrap(false);
		textArea.setFont(Editor.getInstance().getPlainFont());
		textArea.setToolTipText(fieldLayout.getDataConverter().toString());

		setBackground(textArea.getBackground());

		//selectionPainter = new DefaultHighlighter.DefaultHighlightPainter((new JTextArea()).getSelectedTextColor());
		selectionPainter = new DefaultHighlighter.DefaultHighlightPainter(null);
		//selectionPainter = (new JTextArea()).getHighlighter();
		backupSelectionPainter = selectionPainter;
		StrucViewCaret caret = new StrucViewCaret();
		/**
		 * :KLUDGE:Martin Pape:May 16, 2003
		 * For some strange reasons the blinkrate is zero
		 */
		caret.setBlinkRate((new JTextField()).getCaret().getBlinkRate());
		textArea.setCaret(caret);
		dataModel.addDataModelListener(this);

		//Register action and keys
		textArea.setActionMap(new ActionMap());
		textArea.setInputMap(JComponent.WHEN_FOCUSED, new InputMap());
		Iterator it = actions.values().iterator();
		while (it.hasNext())
		{
			registerAction((Action) it.next());
		}
		it = characterActions.values().iterator();
		while (it.hasNext())
		{
			registerAction((Action) it.next());
		}

		setLayout(new BorderLayout());
		add(textArea, BorderLayout.CENTER);
	}

	/******************************************************************************
	 *	DataModelListener Interface
	 */
	public void selectionChanged(DataSelectionEvent e)
	{
		select(e.getNewBitRange());

		/**
		 *  :PENDING: (MPA 2002-09-05)
		 *
		 *  We need this check cause there are some DataFieldTextPane with a dotsize == 0
		 *  which arent displayed anymore and which shouldnt be listening to the datamodel.
		 *  They are not garbage collected because they are still referenced by the datamdodel ind
		 *  the dataModelListenerList. I forgot to dereference them from the dataModel but I could not
		 *  find out where. So this is a temporary fix.
		 */
		if (fieldLayout.getDotSize() > 0)
		{
			if (fieldLayout.getBitRange().contains(e.getNewBitRange()))
			{
				long bitStart = dataModel.getSelectionOffset() - fieldLayout.getBitOffset();
				long bitSize = dataModel.getSelectionSize();
				int unitBitSize = fieldLayout.getDataConverter().getBitSize();
				if (bitStart % unitBitSize == 0 && bitSize % unitBitSize == 0)
				{
					/**
					 *  We only scroll if
					 *  a) the selection change didnt originate from this
					 *     because if it did the scrolling was already done
					 *  b) the selection is not the whole field
					 *     because before editing is started in TreeView, the node is selected
					 *     and selecting the node means selecting the whole field. Then after the whole
					 *     field was selected the selection is changed to the mouseclick position.
					 *     If we scroll after the whole field was selected the position changed and we can
					 *     not calculate the orginal position the user clicked on
					 */
					if (!hasDataViewFocus() && !getDataFieldLayout().getBitRange().equals(e.getNewBitRange()))
					{
						int newDot;
						int oldDot;
						if (e.getOldBitRange().getStart() == e.getNewBitRange().getStart())
						{
							newDot = fieldLayout.bitToDotOffset(e.getNewBitRange().getEnd());
							oldDot = fieldLayout.bitToDotOffset(e.getOldBitRange().getEnd());
						}
						else
						{
							newDot = fieldLayout.bitToDotOffset(e.getNewBitRange().getStart());
							oldDot = fieldLayout.bitToDotOffset(e.getOldBitRange().getStart());
						}
						oldDot -= getDotOffset();
						int line = fieldLayout.dotToLine(oldDot);
						scrollDotVisible(newDot, line);

					}
				}
			}
		}
	}

	public void dataChanged(DataChangeEvent e)
	{
		if (e.wasResized())
		{
			/*
			 *  PENDING MPA 2002-08-20
			 *
			 *  We should do some performance optimization here
			 */
			renderData();
		}
		else
		{
			rerender(e.getBitRange());
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void startEditing()
	{
		/**
		 *  :REFACTORING: MPA 2002-11-02
		 *  This is unclear, I should not have to call two methods
		 */
		 ((StrucViewCaret) getCaret()).focusGained(new FocusEvent(this, 0));
		requestFocus();
		fireActionEvent(FIRST_INPUT_POINT_ACTION);
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		textArea.setEnabled(enabled);
	}

	public String toString()
	{
		DataEncodingField field = getDataFieldLayout().getDataField();
		return super.toString() + " : " + field.getLabel() + " " + field.getDataConverter();
	}

	public Document getDocument()
	{
		return textArea.getDocument();
	}

	public void setDocument(Document doc)
	{
		textArea.setDocument(doc);
	}

	public Caret getCaret()
	{
		return textArea.getCaret();
	}

	public void addStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.add(l);
	}

	public void removeStateChangeListener(StateChangeListener l)
	{
		stateChangeListeners.remove(l);
	}

	public static MyKeyBinding[] getKeyBindings()
	{
		Action[] a = (Action[]) (new ArrayList(actions.values())).toArray(new Action[0]);
		MyKeyBinding[] bindings = new MyKeyBinding[a.length];
		for (int i = 0; i < a.length; i++)
		{
			bindings[i] = new MyKeyBinding(a[i]);
		}
		return bindings;
	}

	public static void setKeyBindings(MyKeyBinding[] bindings)
	{
		for (int i = 0; i < bindings.length; i++)
		{
			Action a = (Action) actions.get(bindings[i].getActionName());
			if (a != null)
			{
				a.putValue(Action.ACCELERATOR_KEY, bindings[i].getKeyStroke());
			}
		}
	}

	public void registerAction(Action a)
	{
		InputMap keyMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap map = getActionMap();
		keyMap.put((KeyStroke) a.getValue(Action.ACCELERATOR_KEY), (String) a.getValue(Action.NAME));
		map.put((String) a.getValue(Action.NAME), a);
	}

	public DataEncodingFieldLayout getDataFieldLayout()
	{
		return fieldLayout;
	}

	public void setDataFieldLayout(DataEncodingFieldLayout fieldLayout)
	{
		this.fieldLayout = fieldLayout;
		textArea.setToolTipText(fieldLayout.getDataConverter().toString());
		setMinimumSize(new Dimension(fieldLayout.getDataConverter().encode().length * getFontMetrics(getFont()).charWidth('m'), 0));
		renderData();
	}

	/*
	 *  return int - absolute dot of first visible character
	 */
	public int getDotOffset()
	{
		return viewDotOffset;
	}

	public void displayMaxDataInOneLine()
	{
		setSize((int) getVisibleRect().getWidth(), getHeight());
		//We use 'm' to get the column width, cause 'm' is the thickest character
		int col = textArea.getWidth() / textArea.getFontMetrics(textArea.getFont()).charWidth('m');
		;
		long bitSize;
		if (col > 0)
		{
			bitSize = fieldLayout.calculateDisplayableBits(col);
		}
		else
		{
			bitSize = 0;
		}
		fieldLayout.setBitSize(bitSize);
		fieldLayout.setUnitsPerLine((int) (bitSize / fieldLayout.getDataConverter().getBitSize()));
		renderData();
	}

	public void displayMaxDataPerPage()
	{
		setSize((int) getVisibleRect().getWidth(), (int) getVisibleRect().getHeight());
		int col = textArea.getWidth() / textArea.getFontMetrics(textArea.getFont()).charWidth('m');
		;
		if (col > 0)
		{
			int unitsPerLine = (int) (fieldLayout.calculateDisplayableBits(col) / fieldLayout.getDataConverter().getBitSize());
			unitsPerLine = Math.max(unitsPerLine, 1);
			fieldLayout.setUnitsPerLine(unitsPerLine);

			int row = textArea.getHeight() / textArea.getFontMetrics(textArea.getFont()).getHeight();
			fieldLayout.setLinesPerPage(row);
		}
		renderData();
	}

	public void deleteChar()
	{
		replaceChar(fieldLayout.getDataConverter().getDeleteChar());
	}

	public boolean hasDataViewFocus()
	{
		return dataModel.hasDataViewFocus(this);
	}

	public void requestDataViewFocus()
	{
		dataModel.requestDataViewFocus(this);
	}

	/**
	 *	Set Data Model
	 *  <p>
	 *  Causes a rerendering of the whole indexField
	 */
	public void setDataModel(DataModel model)
	{
		dataModel.removeDataModelListener(this);
		dataModel = model;
		dataModel.addDataModelListener(this);
		setEdits(false);
		renderData();
	}

	public DataModel getDataModel()
	{
		return dataModel;
	}

	public int getCaretPosition()
	{
		return dotPosition;
	}

	/**
	 *	Set Caret Position
	 *
	 *  The position is only changed if the dot is a valid inputPoint
	 *  Changes the dataModel selection and scrolls the caret into view
	 *
	 *  @param dot - this is the absolute dot position and not the relativ on in the shown document
	 */
	public void setCaretPosition(int dot)
	{
		setCaretPosition(dot, true);
	}

	public void setCaretPosition(int dot, boolean changeSelection)
	{
		if (isInputPoint(dot))
		{
			if (!isSameUnit(dot, getCaretPosition()))
			{
				commitEdits();
			}

			/**
			 *  :REFACTORING: MPA 2002-08-20
			 *
			 *  This should not be done here but before calling this routine
			 */
			//  If the caret is at the end of a Unit, it is not a valid input point
			if (dot == fieldLayout.dotToUnitDotOffset(dot) + fieldLayout.getDataConverter().getDotSize())
			{
				dot = fieldLayout.previousInputPoint(dot);
			}

			dotPosition = dot;
			drawCaret(dot);

			if (changeSelection)
			{
				long bitStart = fieldLayout.dotToBitOffset(fieldLayout.dotToUnitDotOffset(dot));
				long bitSize = fieldLayout.getDataConverter().getBitSize();
				if (dataModel.getSelectionOffset() != bitStart || dataModel.getSelectionSize() != bitSize)
				{
					dataModel.setSelection(bitStart, bitSize);
				}
			}
		}
	}

	/**
	 *  @param dot - dot to make visible
	 *  @param scrollToLine - line in document where the dot should be scrolled to
	 */
	public void scrollDotVisible(int dot, int scrollToLine)
	{
		scrollToLine = Math.max(0, scrollToLine);
		scrollToLine = Math.min(fieldLayout.getLinesPerPage() - 1, scrollToLine);

		int relativeDot = dot - viewDotOffset;
		//If visible do nothing
		if (relativeDot < 0 || relativeDot >= getDocument().getLength())
		{
			int viewLine = dot / fieldLayout.getDotsPerLine();
			viewLine -= scrollToLine;
			setLine(viewLine);
		}
	}

	public BitRange getValidBitRange(BitRange oldBitRange)
	{
		BitRange newBitRange = fieldLayout.getBitRange().intersection(oldBitRange);
		long unitBitSize = fieldLayout.getDataConverter().getBitSize();
		long bitSize = newBitRange.getSize();
		bitSize = Math.max(unitBitSize, bitSize);
		while (bitSize % unitBitSize != 0)
		{
			bitSize++;
		}
		long bitOffset = newBitRange.getStart();
		bitOffset = Math.min(fieldLayout.getBitRange().getEnd() - unitBitSize, bitOffset);
		while ((bitOffset - fieldLayout.getBitOffset()) % unitBitSize != 0)
		{
			bitOffset--;
		}

		return new BitRange(bitOffset, bitSize);
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected boolean isSameUnit(int dot1, int dot2)
	{
		if (fieldLayout.dotToUnitDotOffset(dot1) == fieldLayout.dotToUnitDotOffset(dot2))
		{
			return true;
		}
		return false;
	}

	protected void fireActionEvent(String action)
	{
		((Action) actions.get(action)).actionPerformed(new ActionEvent(this, 0, action));
	}

	protected void renderData()
	{
		PlainDocument doc = new PlainDocument();
		try
		{
			long bitStart = fieldLayout.dotToBitOffset(viewDotOffset);
			long bitEnd = Math.min(fieldLayout.getBitOffset() + fieldLayout.getBitSize(), fieldLayout.getBitsPerPage() + bitStart);
			long bitSize = fieldLayout.getBitsPerLine();

			DataEncodingFieldLayout.RenderedNode text;
			String renderedData;
			long i = bitStart;
			while (i + bitSize < bitEnd)
			{
				renderedData = fieldLayout.render(dataModel.getData(), new BitRange(i, bitSize)).getString();
				doc.insertString(doc.getLength(), renderedData + "\n", null);
				i += bitSize;
			}

			if (i != bitEnd)
			{
				text = fieldLayout.render(dataModel.getData(), new BitRange(i, bitEnd - i));
				doc.insertString(doc.getLength(), text.getString(), null);
			}
		}
		catch (BadLocationException e)
		{
			Logger.getLogger(DataFieldTextPane.class).warning(e);
		}

		setDocument(doc);
		select(dataModel.getBitRange());
		drawCaret(getCaretPosition());
	}

	protected void setLine(int line)
	{
		if (line != getLine())
		{
			line = Math.max(0, line);
			if (fieldLayout.getLines() < fieldLayout.getLinesPerPage())
			{
				line = 0;
			}
			else
			{
				line = Math.min(line, fieldLayout.getLines() - fieldLayout.getLinesPerPage());
			}
			viewDotOffset = line * fieldLayout.getDotsPerLine();
			renderData();
			fireStateChanged();
		}
	}

	protected int getLine()
	{
		if (fieldLayout.getDotsPerLine() == 0)
		{
			return 0;
		}
		return Utils.alignUp(viewDotOffset, fieldLayout.getDotsPerLine()) / fieldLayout.getDotsPerLine();
	}

	protected BitRange getVisibleBitRange()
	{
		long bitStart = fieldLayout.dotToBitOffset(viewDotOffset);
		return new BitRange(bitStart, Math.min((long) fieldLayout.getBitsPerPage(), dataModel.getBitSize() - bitStart));
	}

	protected DotRange getVisibleDotRange()
	{
		return new DotRange(getDotOffset(), Math.min(fieldLayout.getDotsPerPage(), getDocument().getLength()));
	}

	protected void select(BitRange bitRange)
	{
		Highlighter highlighter = textArea.getHighlighter();
		highlighter.removeAllHighlights();
		if (bitRange.getSize() > 0)
		{
			int dotStart = fieldLayout.bitToDotOffset(bitRange.getStart());
			int dotEnd = fieldLayout.bitToDotOffset(bitRange.getEnd());
			if (dotStart != -1 && dotEnd != -1)
			{
				dotStart = Math.max(0, dotStart - viewDotOffset);

				//We have to kill the unitSeparator/LineSeparator at the end
				// of the selection which is included in dotEnd.
				//If the end of the selection is the end of the field
				// nothing should happen when executing this
				dotEnd -= fieldLayout.getDataConverter().getDotSize();
				dotEnd = fieldLayout.dotToUnitDotOffset(dotEnd);
				dotEnd += fieldLayout.getDataConverter().getDotSize();

				/*if (dotEnd < dataPane.getDocument().getLength()) {
				    dotEnd -= field.getUnitSeparator().length;
				}*/

				dotEnd = Math.min(getDocument().getLength(), dotEnd - viewDotOffset);
				/**
				 *  WORKAROUND:
				 *
				 *  If this is not visible, addHighlight crashes
				 *   trying to do a repaint as BasicTextUI.getVisibleEditorRect() returns
				 *  null. We dont bother and resume because the Highlight is added correctly before
				 *  the crash and the repaint happens automaticly when this gets into view.
				 *
				 *  2001-01-19
				 */
				try
				{
					highlighter.addHighlight(dotStart, dotEnd, selectionPainter);
				}
				catch (Exception e)
				{
					Logger.getLogger(DataFieldTextPane.class).warning(e);
				}
			}
		}
	}

	/*
	 *  checks if the dot is inside the the field && if the datasize is large enough
	 */
	protected boolean isInputPoint(int dot)
	{
		if (dot >= 0 && dot < fieldLayout.getDotSize())
		{
			long bitOffset = fieldLayout.dotToBitOffset(dot);
			if (bitOffset != -1 && bitOffset + fieldLayout.getDataConverter().getBitSize() <= dataModel.getBitSize())
			{
				return true;
			}
		}
		return false;
	}

	protected void selectData(int dot)
	{
		commitEdits();
		long bitOffset = dataModel.getSelectionOffset();
		long bitSize = dataModel.getSelectionSize();
		long bitEnd = dataModel.getBitRange().getEnd();
		long caretBitOffset = fieldLayout.dotToBitOffset(getCaretPosition());
		long newBitOffset = fieldLayout.dotToBitOffset(dot);
		long unitBitSize = fieldLayout.getDataConverter().getBitSize();

		if (newBitOffset != -1)
		{
			int newDot = fieldLayout.bitToDotOffset(newBitOffset);
			/**
			 *  We have to separate this case, because if only one unit is selected there
			 *  is no switch if the caret moves from start of selection to end of selection
			 *  or vice versa
			 */
			if (bitSize == unitBitSize)
			{
				//extend selection to the right
				if (caretBitOffset < newBitOffset)
				{
					dataModel.setBitRange(new BitRange(caretBitOffset, (newBitOffset + unitBitSize) - caretBitOffset));
					scrollDotVisible(newDot, fieldLayout.getLinesPerPage() - 1);
				}
				//extend selection to the left
				else
				{
					dataModel.setBitRange(new BitRange(newBitOffset, (caretBitOffset + unitBitSize) - newBitOffset));
					scrollDotVisible(newDot, 0);
				}
			}
			else
			{
				//Caret is at start of selection
				if (bitOffset == caretBitOffset)
				{
					//switch selection
					if (newBitOffset > bitEnd)
					{
						dataModel.setBitRange(new BitRange(bitEnd, (newBitOffset + unitBitSize) - bitEnd));
						scrollDotVisible(newDot, fieldLayout.getLinesPerPage() - 1);
					}
					else
					{
						dataModel.setBitRange(new BitRange(newBitOffset, bitEnd - newBitOffset));
						scrollDotVisible(newDot, 0);
					}
				}
				//Caret is at end of selection
				else
				{
					//switch selection
					if (newBitOffset < bitOffset)
					{
						dataModel.setBitRange(new BitRange(newBitOffset, bitOffset - newBitOffset));
						scrollDotVisible(newDot, 0);
					}
					else
					{
						dataModel.setBitRange(new BitRange(bitOffset, (newBitOffset + unitBitSize) - bitOffset));
						scrollDotVisible(newDot, fieldLayout.getLinesPerPage() - 1);
					}
				}
			}
			setCaretPosition(newDot, false);
		}
	}

	protected void rerender(BitRange bitRange)
	{
		bitRange = getVisibleBitRange().intersection(bitRange);

		if (bitRange.getSize() > 0)
		{

			DataEncodingFieldLayout.RenderedNode text = fieldLayout.render(dataModel.getData(), bitRange);
			PlainDocument doc = (PlainDocument) getDocument();
			try
			{
				//Save Highlight & Dot
				int oldDot = getCaretPosition();
				Highlighter.Highlight[] high = textArea.getHighlighter().getHighlights();
				DotRange[] intervals = new DotRange[high.length];
				for (int i = 0; i < high.length; i++)
				{
					intervals[i] = new DotRange(high[i].getStartOffset(), high[i].getEndOffset() - high[i].getStartOffset());
				}

				doc.remove(text.getOffset() - viewDotOffset, text.getString().length());
				doc.insertString(text.getOffset() - viewDotOffset, text.getString(), null);
				// doc.replace(text.getOffset(), text.getString().length(), text.getString(), null);

				//Restore Highlights & dot
				Highlighter highlighter = textArea.getHighlighter();
				highlighter.removeAllHighlights();
				for (int i = 0; i < intervals.length; i++)
				{
					try
					{
						highlighter.addHighlight((int) intervals[i].getStart(), (int) intervals[i].getEnd(), selectionPainter);
					}
					catch (Exception e)
					{
						Logger.getLogger(DataFieldTextPane.class).warning(e);
					}
				}
				getCaret().setDot(oldDot);

			}
			catch (BadLocationException e)
			{
				System.out.println("FieldDataRenderer.rerender(bitstart: " + bitRange.getStart() + " bitSize: " + bitRange.getSize() + ") ERROR: " + e);
			}
		}
	}

	/**
	 *  assumes the current dot is visible
	 */
	protected void replaceChar(char key)
	{
		PlainDocument doc = (PlainDocument) getDocument();
		int dot = getCaretPosition();
		try
		{
			int relativeDot = dot - getDotOffset();
			if (doc.getText(relativeDot, 1).charAt(0) != key)
			{
				char[] s = new char[1];
				s[0] = key;
				//We have to do it like this, so the Highlighter
				// doesnt fuck up the position trackers
				doc.insertString(relativeDot + 1, new String(s), null);
				doc.remove(relativeDot, 1);
				//doc.replace(dot, 1, new String(s), null);
				setCaretPosition(dot);
				setEdits(true);
			}
		}
		catch (BadLocationException e)
		{
			Logger.getLogger(DataFieldTextPane.class).warning(e);
			//System.out.println("ConverterFieldEditor.edit( dot: " + dot + ") ERROR: " + e);
		}
	}

	/**
	 * 	Commit Edits
	 *
	 *	Write unitChanges to Data
	 */
	protected void commitEdits()
	{
		if (hasEdits())
		{
			PlainDocument doc = (PlainDocument) getDocument();
			setEdits(false);
			DataEncoding unit = fieldLayout.getDataConverter();
			int unitDotOffset = fieldLayout.dotToUnitDotOffset(getCaretPosition());
			long unitBitOffset = fieldLayout.dotToBitOffset(unitDotOffset);

			Data d = null;
			try
			{
				d = unit.decode(doc.getText(unitDotOffset - getDotOffset(), unit.getDotSize()).toCharArray());
			}
			catch (BadLocationException e)
			{
				Logger.getLogger(DataFieldTextPane.class).warning(e);
			}
			catch (DataEncodingException e)
			{
				ErrorDialogFactory.show(DATA_CONVERTER_ERROR, e, this);
			}

			if (d != null)
			{
				//Is this necessary ?
				dataModel.setSelection(unitBitOffset, unit.getBitSize());
				dataModel.paste(d);
			}
			else
			{
				rerender(new BitRange(unitBitOffset, unit.getBitSize()));
			}
		}
	}

	protected boolean hasEdits()
	{
		return hasEdits;
	}

	protected void setEdits(boolean edits)
	{
		if (hasEdits != edits)
		{
			if (hasEdits && !edits)
			{
				selectionPainter = backupSelectionPainter;
			}
			else if (!hasEdits && edits)
			{
				selectionPainter = editPainter;
			}
			select(dataModel.getBitRange());
			hasEdits = edits;
		}
	}

	protected void drawCaret(int dot)
	{
		if (hasDataViewFocus() && getVisibleDotRange().contains(dot))
		{
			getCaret().setVisible(true);
			getCaret().setDot(dot - getDotOffset());
		}
		else
		{
			getCaret().setVisible(false);
		}
	}

	protected void fireStateChanged()
	{
		Object[] listeners = stateChangeListeners.toArray();
		StateChangeEvent changeEvent = new StateChangeEvent(this);
		for (int i = listeners.length - 1; i >= 0; i -= 1)
		{
			((StateChangeListener) listeners[i]).stateChanged(changeEvent);
		}
	}

	/******************************************************************************
	 *	StrucViewCaret Class
	 */
	public class StrucViewCaret extends DefaultCaret
	{

		/******************************************************************************
		 *	FocusListener
		 */
		public void focusGained(FocusEvent e)
		{
			super.focusGained(e);
			if (!hasDataViewFocus())
			{
				requestDataViewFocus();
				int dot = getCaretPosition();
				long bitStart = fieldLayout.dotToBitOffset(fieldLayout.dotToUnitDotOffset(dot));
				long bitSize = fieldLayout.getDataConverter().getBitSize();
				if (dataModel.getSelectionOffset() != bitStart || dataModel.getSelectionSize() != bitSize)
				{
					dataModel.setSelection(bitStart, bitSize);
				}
			}
		}

		public void focusLost(FocusEvent e)
		{
			commitEdits();
			super.focusLost(e);
		}

		/******************************************************************************
		 *	MouseListener
		 */
		public void mousePressed(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				if (e.isShiftDown())
				{
					mouseDragged(e);
				}
				else
				{
					JTextArea component = (JTextArea) getComponent();

					if (component.getDocument().getLength() > 0)
					{
						int pos = component.viewToModel(new Point(e.getX(), e.getY())) + getDotOffset();
						// if (!hasDataViewFocus()) {
						//System.out.println("Do we have focus: " + component.hasFocus());
						if (!component.hasFocus())
						{
							component.requestFocusInWindow();
						}
						//System.out.println("Do we have DataViewFocus: " + hasDataViewFocus() + "\n");
						if (!hasDataViewFocus())
						{
							requestDataViewFocus();
						}
						
						if (isInputPoint(pos))
						{
							setCaretPosition(pos);
						}
						else
						{
							setCaretPosition(getDataFieldLayout().lastInputPoint());
						}
					}
				}
			}
		}

		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				JTextArea component = (JTextArea) getComponent();
				if (component.getDocument().getLength() > 0)
				{
					if (e.getClickCount() == 2)
					{
						int pos = component.viewToModel(new Point(e.getX(), e.getY())) + getDotOffset();
						if (isInputPoint(pos))
						{
							fireActionEvent(SELECT_LINE_ACTION);
						}
					}
					else if (e.getClickCount() == 3)
					{
						fireActionEvent(SELECT_FIELD_ACTION);
					}
				}
			}
		}

		/******************************************************************************
		 *	MouseMotionListener
		 */
		public void mouseDragged(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				JTextArea component = (JTextArea) getComponent();
				if (component.getDocument().getLength() > 0)
				{
					Point point = new Point(e.getX(), e.getY());
					int pos = component.viewToModel(point);
					if (pos == 0)
					{
						pos -= getDataFieldLayout().getDotsPerLine();
					}
					else if (pos == getDocument().getLength())
					{
						pos += getDataFieldLayout().getDotsPerLine();
					}
					pos += getDotOffset();
					if (isInputPoint(pos))
					{
						selectData(pos);
					}
				}
			}
		}
	}
}