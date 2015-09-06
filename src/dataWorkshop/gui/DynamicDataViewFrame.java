package dataWorkshop.gui;

import java.util.HashSet;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.gui.data.view.DefaultDynamicDataView;
import dataWorkshop.gui.data.view.DynamicDataView;
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
public class DynamicDataViewFrame extends ConfigurableDataViewFrame 
	implements DynamicDataView, InternalFrameListener, StateChangeListener
{
	private HashSet stateChangeListener = new HashSet();
	transient boolean changed = false;

	/******************************************************************************
	 *	Constructors
	 */
	public DynamicDataViewFrame(DefaultDynamicDataView dynamicDataView)
	{
		super(dynamicDataView, dynamicDataView.getStructure().getLabel());
		dynamicDataView.addStateChangeListener(this);
	}

	/******************************************************************************
	*	InternalFrameListener Interface
	*/
	public void internalFrameClosed(InternalFrameEvent e)
	{
		((DefaultDynamicDataView) getConfigurableDataView()).removeStateChangeListener(this);
		super.internalFrameClosed(e);
	}

	/******************************************************************************
	 *	StateChangeListener Interface
	 */
	public void stateChanged(StateChangeEvent e)
	{
		setTitle(getStructure().getLabel());
	}

	/******************************************************************************
	 *	DynamicDataView Interface
	 */
	public void setTitle(String title) {
		if (hasChanged())
		{
			title += " *";
		}
		super.setTitle(title);
	}
	
	public boolean hasValidStructure()
	{
		return getDynamicDataView().hasValidStructure();
	}

	public long getBitOffset()
	{
		return getDynamicDataView().getBitOffset();
	}

	public RootStatement getStructure()
	{
		return getDynamicDataView().getStructure();
	}

	public void setStructure(RootStatement structure)
	{
		getDynamicDataView().setStructure(structure);
		setTitle(structure.getLabel());
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void addStateChangeListener(StateChangeListener l)
	{
		stateChangeListener.add(l);
	}

	public void removeStateChangeListener(StateChangeListener l)
	{
		stateChangeListener.remove(l);
	}

	public boolean hasChanged()
	{
		return changed;
	}

	public void setChanged(boolean b)
	{
		if (b != changed)
		{
			changed = b;
			fireStateChanged();
		}
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected void fireStateChanged()
	{
		Object[] listeners = stateChangeListener.toArray();
		StateChangeEvent changeEvent = new StateChangeEvent(this);
		for (int i = listeners.length - 1; i >= 0; i -= 1)
		{
			((StateChangeListener) listeners[i]).stateChanged(changeEvent);
		}
	}

	/******************************************************************************
		 *	Private Methods
		 */
	private DynamicDataView getDynamicDataView()
	{
		return (DynamicDataView) getConfigurableDataView();
	}
}