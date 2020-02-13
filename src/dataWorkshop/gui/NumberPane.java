/**
 *	@version 1.0 2000/03/03
 * 	@author metheus@gmx.net
 *

 */

package dataWorkshop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dataWorkshop.data.DataNumber;
import dataWorkshop.gui.data.encoding.DataEncodingField;
import dataWorkshop.gui.editor.MyKeyBinding;
import dataWorkshop.number.IntegerFormat;

/**
 *  A JSpinner with a ShortDataConverterEditor as an Editor. If Spinner is used to change value Editor receives focus
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
public class NumberPane extends ActionPane implements ChangeListener, FocusListener
{
	DataEncodingField encodingField;
	DataNumber number;

	long minimum;
	long maximum;

	JLabel nameLabel;
	JSpinner spinner;
	JLabel postFixLabel;

	public static String CANCEL_EDITS_ACTION = "cancel-edits";
	public static String COMMIT_EDITS_ACTION = "commit-edits";
	public static String INCREASE_NUMBER_BY_ONE_ACTION = "increase-number-by-one";
	public static String DECREASE_NUMBER_BY_ONE_ACTION = "decrease-number-by-one";
	public static String INCREASE_NUMBER_BY_STEP_SIZE_ACTION = "increase-number-by-step-size";
	public static String DECREASE_NUMBER_BY_STEP_SIZE_ACTION = "decrease-number-by-step-size";

	static MyKeyBinding[] DEFAULT_KEY_BINDINGS =
		{
			new MyKeyBinding(CANCEL_EDITS_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)),
			new MyKeyBinding(COMMIT_EDITS_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)),
			new MyKeyBinding(INCREASE_NUMBER_BY_ONE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
			new MyKeyBinding(DECREASE_NUMBER_BY_ONE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
			new MyKeyBinding(INCREASE_NUMBER_BY_STEP_SIZE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0)),
			new MyKeyBinding(DECREASE_NUMBER_BY_STEP_SIZE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0)),
			};

	static HashMap actions = new HashMap();
	static {
		Action a;
		a = new AbstractAction(INCREASE_NUMBER_BY_STEP_SIZE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				NumberPane numberPane = (NumberPane) e.getSource();
				numberPane.setValue(numberPane.getValue() + numberPane.getStepSize());
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(DECREASE_NUMBER_BY_STEP_SIZE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				NumberPane numberPane = (NumberPane) e.getSource();
				numberPane.setValue(numberPane.getValue() - numberPane.getStepSize());
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(INCREASE_NUMBER_BY_ONE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				NumberPane numberPane = (NumberPane) e.getSource();
				numberPane.setValue(numberPane.getValue() + 1);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(DECREASE_NUMBER_BY_ONE_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				NumberPane numberPane = (NumberPane) e.getSource();
				numberPane.setValue(numberPane.getValue() - 1);
			}
		};
		actions.put(a.getValue(Action.NAME), a);

		a = new AbstractAction(COMMIT_EDITS_ACTION)
		{
			public void actionPerformed(ActionEvent e)
			{
				NumberPane numberPane = (NumberPane) e.getSource();
				numberPane.encodingField.commitEdits();
				numberPane.fireActionEvent();
			}
		};
		actions.put(a.getValue(Action.NAME), a);
		
		a = new AbstractAction(CANCEL_EDITS_ACTION)
			{
				public void actionPerformed(ActionEvent e)
				{
					NumberPane numberPane = (NumberPane) e.getSource();
					numberPane.encodingField.cancelEdits();
					numberPane.fireActionEvent();
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
	public NumberPane(IntegerFormat numberFormat)
	{
		this(numberFormat, null);
	}

	public NumberPane(IntegerFormat numberFormat, String label)
	{
		super();
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		this.number = numberFormat.getDataNumber();

		nameLabel = new JLabel();

		minimum = number.getMin();
		maximum = number.getMax();

		encodingField = new DataEncodingField(numberFormat.getDataConverter());
		encodingField.setMaximumSize(encodingField.getPreferredSize());
		encodingField.setMinimumSize(encodingField.getPreferredSize());

		spinner = new JSpinner();
		spinner.setEditor(encodingField);
		long value = Math.min(0, maximum);
		value = Math.max(minimum, value);
		spinner.setModel(new SpinnerNumberModel(Long.valueOf(value), Long.valueOf(minimum), Long.valueOf(maximum), Long.valueOf(1)));
		/**
		 * :KLUDGE:Martin Pape:Jun 12, 2003
		 * with j2sdk1.4.2beta we must not set the Border to null as the default border is ok
		 * but previous versions had a nested border (at least the metal L&F) which we do not want
		 */
		//spinner.setBorder(null);
		spinner.addChangeListener(this);
		spinner.setMaximumSize(spinner.getPreferredSize());
		spinner.setMinimumSize(spinner.getPreferredSize());

		postFixLabel = new JLabel((Character.valueOf(numberFormat.getPostfix())).toString());

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setLabel(label);
		add(nameLabel);
		add(spinner);
		add(postFixLabel);
		setBorder(null);
		setAlignmentX(JComponent.CENTER_ALIGNMENT);

		Iterator it = actions.values().iterator();
		while (it.hasNext())
		{
			registerAction((Action) it.next());
		}

		encodingField.addFocusListener(this);
		calcToolTip();
	}

	/******************************************************************************
	 *	ChangeListener Interface
	 */
	public void stateChanged(ChangeEvent changeEvent)
	{
		/*
		 *  Editor does not seem to get focus, but the focus from the previous
		 *  component is lost and that is what we want.
		 *
		 *  MPA 2002-08-13
		 *
		editor.requestFocus();
		 */
		setValue(((Long) spinner.getValue()).longValue());
		fireActionEvent();
	}

	/******************************************************************************
	 *	FocusListener Methods
	 */
	public void focusGained(FocusEvent e)
	{
	}

	public void focusLost(FocusEvent e)
	{
		if (encodingField.hasEdits())
		{
			//this will fire an action event
			setValue(getValue());
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
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

	public void setLabel(String name)
	{
		if (name == null)
		{
			nameLabel.setBorder(null);
		}
		else
		{
			nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
		}
		nameLabel.setText(name);
	}

	public void setEditable(boolean editable)
	{
		encodingField.setEditable(editable);
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		nameLabel.setEnabled(enabled);
		encodingField.setEnabled(enabled);
		spinner.setEnabled(enabled);
		postFixLabel.setEnabled(enabled);
	}

	public void setValue(long value)
	{
		value = Math.max(minimum, value);
		value = Math.min(maximum, value);
		spinner.setValue(Long.valueOf(value));
		encodingField.setData(number.encode(value));
		fireActionEvent();
	}

	public long getValue()
	{
		return number.decode(encodingField.getData());
	}

	public void setMaximum(long max)
	{
		maximum = max;
		((SpinnerNumberModel) spinner.getModel()).setMaximum(Long.valueOf(max));
		setValue(getValue());
		calcToolTip();
	}

	public void setMinimum(long min)
	{
		minimum = min;
		((SpinnerNumberModel) spinner.getModel()).setMinimum(Long.valueOf(min));
		setValue(getValue());
		calcToolTip();
	}

	public void setStepSize(int gran)
	{
		((SpinnerNumberModel) spinner.getModel()).setStepSize(Long.valueOf(gran));
	}

	public int getStepSize()
	{
		return ((Long) ((SpinnerNumberModel) spinner.getModel()).getStepSize()).intValue();
	}

	private void calcToolTip()
	{
		String s = encodingField.getDataEncoding().toString();
		String minString = new String(encodingField.getDataEncoding().encode(number.encode(minimum)));
		String maxString = new String(encodingField.getDataEncoding().encode(number.encode(maximum)));
		s += " MinValue " + minString +  ", MaxValue " + maxString;
		setToolTipText(s);
		encodingField.setToolTipText(s);
		if (nameLabel != null)
		{
			nameLabel.setToolTipText(s);
		}
	}
}