package dataWorkshop.data.view;

import org.w3c.dom.Element;

import dataWorkshop.Utils;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

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
public class DataViewOption implements XMLSerializeable {
    
    final static String CLASS_NAME = "DataViewOptions";
    
    public final static String BITS_PER_LINE_TAG = "bitsPerLine";
    public final static String LINES_PER_PAGE_TAG = "linesPerPage";
    public final static String RENDER_OFFSET_TAG = "renderOffset";
    public final static String RENDER_SIZE_TAG = "renderSize";
    
    //16 bytes per line
    int bitsPerLine = 16 * 8;
    int linesPerPage = 10;
    boolean displayInOneLine = false;
    boolean renderOffset = false;
    boolean renderSize = false;
    char[] unitSeparator = {' '};
    char[] lineSeparator = {'\n'};
    
    /******************************************************************************
     *	Constructors
     */
    public DataViewOption() {
    }
    
    /******************************************************************************
     *	XML Serializeable Interface
     */
    public String getClassName() {
        return CLASS_NAME;
    }
    
    public void serialize(Element context) {
        XMLSerializeFactory.setAttribute(context, BITS_PER_LINE_TAG, getBitsPerLine());
        XMLSerializeFactory.setAttribute(context, LINES_PER_PAGE_TAG, getLinesPerPage());
        XMLSerializeFactory.setAttribute(context, RENDER_OFFSET_TAG, isRenderOffset());
        XMLSerializeFactory.setAttribute(context, RENDER_SIZE_TAG, isRenderSize());
    }
    
    public void deserialize(Element context) {
        setBitsPerLine(XMLSerializeFactory.getAttributeAsInt(context, BITS_PER_LINE_TAG));
        setLinesPerPage(XMLSerializeFactory.getAttributeAsInt(context, LINES_PER_PAGE_TAG));
        setRenderOffset(XMLSerializeFactory.getAttributeAsBoolean(context, RENDER_OFFSET_TAG));
        setRenderSize(XMLSerializeFactory.getAttributeAsBoolean(context, RENDER_SIZE_TAG));
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public int getBitsPerLine() {
        return bitsPerLine;
    }
    
    public void setBitsPerLine(int bitsPerLine) {
        this.bitsPerLine = bitsPerLine;
    }
    
    public int getLinesPerPage() {
        return linesPerPage;
    }
    
    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }
    
    public boolean isRenderOffset() {
        return renderOffset;
    }
    
    public void setRenderOffset(boolean renderOffset) {
        this.renderOffset = renderOffset;
    }
    
    public boolean isRenderSize() {
        return renderSize;
    }
    
    public void setRenderSize(boolean renderSize) {
        this.renderSize = renderSize;
    }
    
    public DataEncodingFieldLayout createLayout(DataEncodingField field) {
        DataEncodingFieldLayout layout = new DataEncodingFieldLayout(field);
        layout.setUnitSeparator(unitSeparator);
        layout.setLineSeparator(lineSeparator);
        if (displayInOneLine) {
            layout.setUnitsPerLine(0);
        }
        else {
            int units = Utils.alignUp(getBitsPerLine(), field.getDataConverter().getBitSize()) / field.getDataConverter().getBitSize();
            layout.setUnitsPerLine(Math.min(units, (int) (field.getBitSize() / field.getDataConverter().getBitSize())));
        }
        layout.setLinesPerPage(linesPerPage);
        layout.setRenderOffset(renderOffset);
        layout.setRenderSize(renderSize);
        return layout;
    }
    
    public MapFieldLayout createLayout(MapField field) {
        MapFieldLayout layout = new MapFieldLayout(field);
        layout.setRenderOffset(renderOffset);
        layout.setRenderSize(renderSize);
        return layout;
    }
    
    public DataFrameLayout createLayout(DataFrame frame) {
        DataFrameLayout layout = new DataFrameLayout(frame);
        layout.setRenderOffset(renderOffset);
        layout.setRenderSize(renderSize);
        return layout;
    }
    
    public char[] getUnitSeparator() {
        return this.unitSeparator;
    }
    
    public void setUnitSeparator(char[] unitSeparator) {
        this.unitSeparator = unitSeparator;
    }
    
    public char[] getLineSeparator() {
        return this.lineSeparator;
    }
    
    public void setLineSeparator(char[] lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    public boolean isDisplayInOneLine() {
        return displayInOneLine;
    }
    
    public void setDisplayInOneLine(boolean displayInOneLine) {
        this.displayInOneLine = displayInOneLine;
    }
}