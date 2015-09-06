package dataWorkshop.data.view;

import dataWorkshop.Utils;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;

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
public class DataEncodingFieldLayout extends DataFrameLayout {
    
    protected int unitsPerLine;
    protected int linesPerPage;
    
    protected int dotsPerLine;
    protected int bitsPerLine;
    protected int bitsPerPage;
    protected int dotSize;
    protected int lines;
    
    protected char[] unitSeparator = {' '};
    protected char[] lineSeparator = {'\n'};
    
    /******************************************************************************
     *	Constructors
     */
    public DataEncodingFieldLayout() {
        this(new DataEncodingField());
    }
    
    public DataEncodingFieldLayout(DataEncodingField field) {
        super(field);
    }
    
    /******************************************************************************
     *	Public Methods
     */
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
    
    public DataEncodingField getDataField() {
        return (DataEncodingField) getDataFrame();
    }
    
    public long getBitSize() {
        return dataFrame.getBitSize();
    }
    
    public void setBitSize(long bitSize) {
        dataFrame.setBitSize(bitSize);
        updateLayoutInformation();
    }
    
    public long getBitOffset() {
        return dataFrame.getBitOffset();
    }
    
    public void setBitOffset(long bitOffset) {
        dataFrame.setBitOffset(bitOffset);
    }
    
    public BitRange getBitRange() {
        return dataFrame.getBitRange();
    }
    
    public void setDataConverter(DataEncoding converter) {
        getDataField().setDataConverter(converter);
        updateLayoutInformation();
    }
    
    public DataEncoding getDataConverter() {
        return getDataField().getDataConverter();
    }
    
    public int getUnitsPerLine() {
        return unitsPerLine;
    }
    
    public void setUnitsPerLine(int unitsPerLine) {
        this.unitsPerLine = unitsPerLine;
        updateLayoutInformation();
    }
    
    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        updateLayoutInformation();
    }
    
    public int getDotsPerPage() {
        return dotsPerLine * linesPerPage;
    }
    
    public int getLines() {
        return lines;
    }
    
    public int getDotsPerLine() {
        return dotsPerLine;
    }
    
    public int getDotSize() {
        return dotSize;
    }
    
    public int getLinesPerPage() {
        return linesPerPage;
    }
    
    public int getBitsPerLine() {
        return bitsPerLine;
    }
    
    public int getBitsPerPage() {
        return bitsPerPage;
    }
    
    public int nextInputPoint(int dot) {
        dot++;
        while (dot < dotSize) {
            if (isInputPoint(dot)) {
                return dot;
            }
            dot++;
        }
        return -1;
    }
    
    public int previousInputPoint(int dot) {
        dot--;
        while (dot >= 0) {
            if (isInputPoint(dot)) {
                return dot;
            }
            dot--;
        }
        return -1;
    }
    
    /*
     *  If not possible return -1
     */
    public int nextInputLine(int d) {
        d += dotsPerLine;
        if (d < dotSize) {
            return d;
        }
        return -1;
    }
    
    public int previousInputLine(int d) {
        d -= dotsPerLine;
        if (d >= 0) {
            return d;
        }
        return -1;
    }
    
    public int startOfInputLine(int dot) {
        return Utils.alignDown(dot, dotsPerLine) + getDataConverter().nextInputPoint(-1);
    }
    
    public int endOfInputLine(int dot) {
        dot = startOfInputLine(dot) + ((unitsPerLine - 1) * (getDataConverter().getDotSize() + getUnitSeparator().length));
        return dot + getDataConverter().previousInputPoint(getDataConverter().getDotSize());
    }
    
    public int nextPage(int dot) {
        dot += dotsPerLine * linesPerPage;
        if (dot > dotSize) {
            return -1;
        }
        return dot;
    }
    
    public int previousPage(int dot) {
        dot -= dotsPerLine * linesPerPage;
        if (dot < 0) {
            return -1;
        }
        return dot;
    }
    
    public int firstInputPoint() {
        int dot = 0;
        while (dot < getDotSize() && !isInputPoint(dot)) {
            dot++;
        }
        if (isInputPoint(dot)) {
            return dot;
        }
        return -1;
    }
    
    public int lastInputPoint() {
        int dot = getDotSize();
        while (dot >= 0 && !isInputPoint(dot)) {
            dot--;
        }
        if (isInputPoint(dot)) {
            return dot;
        }
        return -1;
    }
    
    public boolean hasInputPoint() {
        if (getBitSize() >= getDataConverter().getBitSize()) {
            return getDataConverter().hasInputPoint();
        }
        return false;
    }
    
    /**
     *	Is Input Point
     */
    public boolean isInputPoint(int dot) {
        if (dotsPerLine > 0) {
            int i = dot % dotsPerLine;
            if (i >= 0 & i < dotsPerLine) {
                i = i % (getDataConverter().getDotSize() + getUnitSeparator().length);
                if (i < getDataConverter().getDotSize()) {
                    return getDataConverter().isInputPoint(i);
                }
            }
        }
        return false;
    }
    
    /**
     *	Dot To Bit Offset
     */
    public long dotToBitOffset(int dot) {
        if (dot < dotSize) {
            int i = dot % dotsPerLine;
            if (i >= 0 & i < dotsPerLine) {
                long bitOffset = (dot / dotsPerLine) * bitsPerLine;
                //System.out.println("bitOffset " + bitOffset + " = (dot " + dot + " / dotsPerLine " + dotsPerLine + " ) * bitsPerLine " + bitsPerLine);
                bitOffset += ((dot % dotsPerLine) / (getDataConverter().getDotSize()  + getUnitSeparator().length)) * getDataConverter().getBitSize();
                if (bitOffset - getBitOffset() <= getBitSize()) {
                    return bitOffset + getBitOffset();
                }
            }
        }
        return -1;
    }
    
    public int bitToDotOffset(long bitOffset) {
        bitOffset -= getBitOffset();
        if (bitOffset >= 0 && bitOffset <= getBitSize() && bitsPerLine > 0) {
            if (bitOffset == Utils.alignDown(bitOffset, getDataConverter().getBitSize())) {
                int dotOffset = (int) ((bitOffset / bitsPerLine) * dotsPerLine);
                dotOffset += ((bitOffset % bitsPerLine) / getDataConverter().getBitSize()) * (getDataConverter().getDotSize() + getUnitSeparator().length);
                return dotOffset;
            }
        }
        return -1;
    }
    
    /**
     *	Dot to Unit DotOffset
     *
     *	Dot Must be valid DotOffset
     */
    public int dotToUnitDotOffset(int d) {
        if (dotsPerLine > 0) {
            int lineOffset = (d % dotsPerLine);
            lineOffset = Utils.alignDown(lineOffset, getDataConverter().getDotSize() + getUnitSeparator().length);
            return lineOffset + (d - (d % dotsPerLine));
        }
        return -1;
    }
    
    public int dotToLine(int d) {
        return d / dotsPerLine;
    }
    
    /**
     *  Calulate the number of bits rounded to unitBitSize which can be displayed in
     *  in a give dotsize
     */
    public long calculateDisplayableBits(int dotSize) {
        char[] unitSeparator = getUnitSeparator();
        return ((dotSize + unitSeparator.length) / (getDataConverter().getDotSize() + unitSeparator.length)) * getDataConverter().getBitSize();
    }
    
    /**
     *  Render
     *
     *	bitSizes are rounded to unitBounderies
     */
    public RenderedNode render(Data data, BitRange bitRange) {
        DataEncoding converter = getDataConverter();
        
        if (bitRange.getSize() == 0) {
            return new RenderedNode(0, new String());
        }
        
        if (dotSize <= 0) {
            return new RenderedNode(0, new String());
        }
        
        long bitStart = bitRange.getStart();
        long bitEnd = bitRange.getEnd();
        
        //Align bitStart (down) & bitEnd (up) to Unit Bounderies
        int unitBitSize = converter.getBitSize();
        long bitOffset = Utils.alignDown(bitStart - getBitOffset(), unitBitSize) + getBitOffset();
        bitEnd = Utils.alignUp(bitEnd - getBitOffset(), unitBitSize) + getBitOffset();
        
        //Calc char[].length
        char[] renderedData;
        int dotStart = dotToUnitDotOffset(bitToDotOffset(bitOffset));
        //Does bitEnd point into last unit
        if (bitEnd > getBitOffset() + getBitSize() - unitBitSize) {
            renderedData = new char[(dotSize - dotStart)];
        }
        else {
            renderedData = new char[bitToDotOffset(bitEnd) - dotStart - getUnitSeparator().length];
        }
        
        int units = (dotStart % dotsPerLine) / (converter.getDotSize() + getUnitSeparator().length);
        long maxOffset = Math.min(data.getBitSize(), getBitSize() + getBitOffset()) - unitBitSize;
        int unitDotSize = converter.getDotSize();
        
        int dot = 0;
        while (dot < renderedData.length) {
            if (bitOffset <= maxOffset) {
                converter.encode(data, bitOffset, renderedData, dot);
            } else {
                converter.encode(renderedData, dot);
            }
            dot += unitDotSize;
            bitOffset += unitBitSize;
            units++;
            if (dot < renderedData.length) {
                if (units == unitsPerLine) {
                    units = 0;
                    System.arraycopy(lineSeparator, 0, renderedData, dot, lineSeparator.length);
                    dot += lineSeparator.length;
                }  else {
                    System.arraycopy(unitSeparator, 0, renderedData, dot, unitSeparator.length);
                    dot += unitSeparator.length;
                }
            }
        }
        
        return new RenderedNode(dotStart, new String(renderedData));
    }
    
    public String toString() {
        String s = getBitRange().toString();
        s += " UnitsPerLine: " + unitsPerLine + " LinesPerPage: " + linesPerPage + " DotsPerLine: " + dotsPerLine + " Lines: " + lines + " DotSize: " + dotSize;
        return s;
    }
    
    /******************************************************************************
     *	Private Methods
     */
    private void updateLayoutInformation() {
        char[] lineSeparator = getLineSeparator();
        char[] unitSeparator = getUnitSeparator();
        
        long bitSize = getBitSize();
        DataEncoding converter = getDataConverter();
        
        //Calc Dots Per Line
        if (unitsPerLine == 0) {
            dotsPerLine = (int) Utils.alignUp(bitSize, converter.getBitSize()) / converter.getBitSize();
        }
        else {
            dotsPerLine = unitsPerLine;
        }
        dotsPerLine *= (converter.getDotSize() + unitSeparator.length);
        dotsPerLine -= unitSeparator.length;
        dotsPerLine += lineSeparator.length;
        
        //Calc Bits Per Line
        if (unitsPerLine == 0) {
            bitsPerLine = (int) getBitSize();
        }
        else {
            bitsPerLine = unitsPerLine * converter.getBitSize();
        }
        
        //Calc Dot Size of Field
        long size = Utils.alignUp(bitSize, converter.getBitSize());
        if (bitsPerLine > 0) {
            dotSize = (int) ((size / bitsPerLine) * dotsPerLine);
            if (size % bitsPerLine != 0) {
                dotSize += ((size % bitsPerLine) / converter.getBitSize()) * (converter.getDotSize() + unitSeparator.length);
                dotSize -= unitSeparator.length;
            }
            else {
                dotSize -= lineSeparator.length;	//Kill NewLine
            }
        }
        else {
            dotSize = 0;
        }
        
        if (dotSize > 0) {
            lines = Utils.alignUp(dotSize, dotsPerLine) / dotsPerLine;
        }
        else {
            lines = 0;
        }
        
        bitsPerPage = (int) Math.min((long) linesPerPage * bitsPerLine, bitSize);
    
        /*
        if (getDataField().getLabel().equals("Filename")) {
             System.out.println("Caught");
        }
        
        if (getDataConverter().getClassName().equals(USAscii.CLASS_NAME)) {
            System.out.println("Caught");
        }
         */
    }
    
    /******************************************************************************
     *	Rendered Field Class
     */
    public class RenderedNode {
        int dotOffset;
        String renderedNode;
        
        public RenderedNode(int offset, String s) {
            dotOffset = offset;
            renderedNode = s;
        }
        
        public int getOffset() {
            return dotOffset;
        }
        
        public String getString() {
            return renderedNode;
        }
    }
}