package dataWorkshop.gui.editor;

import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataTransformation;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.dialogs.DataClipboardDialog;
import dataWorkshop.gui.dialogs.ErrorDialogFactory;
import dataWorkshop.gui.event.DataSelectionEvent;

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
public class CalculatorAction extends DataModelAction implements LocaleStrings
{

	private DataTransformation transformation;

	public CalculatorAction(DataModel dataModel, DataTransformation transformation)
	{
		super(dataModel, transformation.toString());
		this.transformation = transformation;
	}

	public void doAction()
	{
		try
		{
			Data result = transformation.transform(dataModel.getData(), dataModel.getSelectionOffset(), dataModel.getSelectionSize());
			if (result != null)
			{
				DataClipboardDialog.getInstance().setData(result);
				//Display the result to the user
				Editor.getInstance().fireActionEvent(Editor.DATA_CLIPBOARD_ACTION);
			}
			else {
				ErrorDialogFactory.show(ERROR_TRANSFORMATION, Editor.getInstance());
			}
		}
		catch (Exception e)
		{
			ErrorDialogFactory.show(ERROR_TRANSFORMATION, e, Editor.getInstance());
		}
	}

	public void selectionChanged(DataSelectionEvent e)
	{
		long range = e.getNewBitRange().getSize();
		if (range > 0 && (range % transformation.getBitSize() == 0))
		{
			setEnabled(true);
		}
		else
		{
			setEnabled(false);
		}
	}

	public DataTransformation getDataTransformation()
	{
		return transformation;
	}
}
