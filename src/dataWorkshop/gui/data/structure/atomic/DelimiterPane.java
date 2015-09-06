package dataWorkshop.gui.data.structure.atomic;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.structure.atomic.DelimiterDefinition;
import dataWorkshop.gui.NumberPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.SingleUnitView;

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
public class DelimiterPane extends JPanel
    implements LocaleStrings
{
    SingleUnitView searchPane;
    NumberPane granularityPane;
    
    /******************************************************************************
     *	Constructors
     */
    public DelimiterPane(String label) {
        super();
        DataWorkshop options = DataWorkshop.getInstance();
        granularityPane = new NumberPane(options.getUnsignedOffsetNumber(), "Search StepSize");
        granularityPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        searchPane = new SingleUnitView(label);
        
        setLayout(new BorderLayout());
        //setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        add(granularityPane, BorderLayout.NORTH);
        add(searchPane, BorderLayout.CENTER);
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void setDelimiterDefinition(DelimiterDefinition delimiter) {
        DataModel dataModel = new DataModel(delimiter.getDelimiterData());
        searchPane.setDataModel(dataModel);
        searchPane.setDataConverter(delimiter.getDelimiterEncoding());
        granularityPane.setValue(delimiter.getStepSize());
    }
    
    public DelimiterDefinition getDelimiterDefinition() {
		DelimiterDefinition delimiter = new DelimiterDefinition();
		delimiter.setDelimiterEncoding(searchPane.getDataConverter());
		delimiter.setDelimiterData(searchPane.getDataModel().getData());
		delimiter.setStepSize((int) granularityPane.getValue());
		return delimiter;
    }
}