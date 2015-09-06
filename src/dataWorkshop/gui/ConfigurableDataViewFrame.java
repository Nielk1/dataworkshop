package dataWorkshop.gui;

import java.awt.Component;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.ConfigurableDataView;

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
public class ConfigurableDataViewFrame extends JInternalFrame 
	implements ConfigurableDataView, InternalFrameListener
{
	private ConfigurableDataView configurableDataView;

	//private static DataModel EMPTY_DATAMODEL = new DataModel();

	/******************************************************************************
	 *	Constructors
	 */
	public ConfigurableDataViewFrame(ConfigurableDataView configurableDataView, String label)
	{
		super(label, true, true, true, true);
		this.configurableDataView = configurableDataView;
		/**
		 * :REFACTORING:Martin Pape:Jun 17, 2003
		 * this cast hints at bad design
		 */
		getRootPane().getContentPane().add((Component) configurableDataView);
		setVisible(true);
		setSize(200, 200);
		addInternalFrameListener(this);
	}

	/******************************************************************************
	*	InternalFrameListener Interface
	*/
	public void internalFrameClosed(InternalFrameEvent e)
	{
		removeInternalFrameListener(this);
		configurableDataView.setDataModel(new DataModel());
	}

	public void internalFrameOpened(InternalFrameEvent e)
	{
	}

	public void internalFrameActivated(InternalFrameEvent e)
	{
	}

	public void internalFrameDeiconified(InternalFrameEvent e)
	{
		//associate DataModel (turn on rendering)
		setDataModel(getDataModel());
	}

	public void internalFrameDeactivated(InternalFrameEvent e)
	{
	}

	public void internalFrameIconified(InternalFrameEvent e)
	{
		//deassociate DataModel (turn off rendering)
		// setDataModel(EMPTY_DATAMODEL);
	}

	public void internalFrameClosing(InternalFrameEvent e)
	{
	}

	/******************************************************************************
	*	ConfigurableDataView Interface
	*/
	public DataViewOption getDataViewOption() {
		return getConfigurableDataView().getDataViewOption();
	}
    
	public void setDataViewOption(DataViewOption options) {
		getConfigurableDataView().setDataViewOption(options);
	}
    
	public DataFrame getDataFrame() {
		return getConfigurableDataView().getDataFrame();
	}
	
	public void setDataModel(DataModel dataModel)
	{
		configurableDataView.setDataModel(dataModel);
	}

	public DataModel getDataModel()
	{
		return configurableDataView.getDataModel();
	}

	public boolean hasDataViewFocus() {
		return configurableDataView.hasDataViewFocus();
	}
    
    public BitRange getValidBitRange(BitRange range) {
    	return configurableDataView.getValidBitRange(range);
    }

	/******************************************************************************
	*	Protected Methods
	*/
	protected ConfigurableDataView getConfigurableDataView()
	{
		return configurableDataView;
	}
}