package dataWorkshop.gui.data;

import java.util.ArrayList;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.gui.data.view.DataView;
import dataWorkshop.logging.Logger;

/**
 * Wrapper around Data. DataModel keeps track of Undos/Redos
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
public class DataModel 
{
    long selectionOffset = 0;
    long selectionSize = 0;
    ArrayList undos = new ArrayList();
    ArrayList redos = new ArrayList();
    boolean hasDataChanged = false;
    
    Data data;
    /**
     *  it is important that the data modelListeners keep their order
     */
    private ArrayList dataModelListener = new ArrayList();
    private ArrayList stateChangeListener = new ArrayList();
    private DataView focusedDataView;
    
    /******************************************************************************
     *	Constructors
     */
    public DataModel() {
        this(new Data());
    }
    
    public DataModel(Data data) {
        this.data = data;
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void addDataModelListener(DataModelListener l) {
        dataModelListener.add(l);
    }
    
    public void removeDataModelListener(DataModelListener l) {
        if (!dataModelListener.contains(l)) {
            Logger.getLogger(this.getClass()).warning("DataModelListener " + l + " could not be remove.", new Exception());
        }
        else {
            dataModelListener.remove(l);
        }
    }
    
    public void addStateChangeListener(StateChangeListener l) {
        stateChangeListener.add(l);
    }
    
    public void removeStateChangeListener(StateChangeListener l) {
        stateChangeListener.remove(l);
    }
    
    /*
     *  DataViewFocus should be gained if DataView gains focus
     *  but is only lost if another DataView gains Focus.
     *
     *  We need this because the normal focus is not suffient for
     *  our needs. (e.g. SingleLineView needs to know where the dataSelectionChange
     *  originated. The change is messaged before the DataView has gained focus,
     *  and we cannot be sure when the focus is gained.)
     */
    public void requestDataViewFocus(DataView view) {
        focusedDataView = view;
    }
    
    public boolean hasDataViewFocus(DataView view) {
        return view == focusedDataView;
    }
    
    public DataView getFocusedDataView() {
        return focusedDataView;
    }
    
    public boolean hasDataChanged() {
        return hasDataChanged;
    }
    
    public void setDataChanged(boolean b) {
        if (b != hasDataChanged) {
            hasDataChanged = b;
            fireStateChanged();
        }
    }
    
    /*
     *  Does not change selection
     */
    public void append(Data newData) {
        long size = data.getBitSize();
        data.setBitSize(size + newData.getBitSize());
        data.replace(size, newData.getBitSize(), newData);
        fireDataChanged(0, 0, true);
    }
    
    public Data getData() {
        return data;
    }
    
    public long getBitSize() {
        return data.getBitSize();
    }
    
    public boolean isSelection() {
        if (selectionSize != 0) {
            return true;
        }
        return false;
    }
    
    public Data getSelection() {
        return getSelection(selectionOffset, selectionSize);
    }
    
    public Data getSelection(BitRange bitRange) {
        if ((new BitRange(0, getBitSize()).contains(bitRange))) {
            return data.copy(bitRange.getStart(), bitRange.getSize());
        }
        return null;
    }
    
    public Data getSelection(long bitOffset, long bitSize) {
        return getSelection(new BitRange(bitOffset, bitSize));
    }
    
    public BitRange getBitRange() {
        return new BitRange(selectionOffset, selectionSize);
    }
    
    public void setBitRange(BitRange bitRange) {
        setSelection(bitRange.getStart(), bitRange.getSize());
    }
    
    /*
     *  Fires a selectionChangeEvent only if the new selection is
     *  different from the old selection
     */
    public void setSelection(long offset, long size) {
        long oldOffset = getSelectionOffset();
        long oldSize = getSelectionSize();
        selectionOffset = Math.min(offset, getBitSize());
        selectionSize = size;
        if (selectionOffset + selectionSize > getBitSize()) {
            selectionSize = getBitSize() - selectionOffset;
        }
        //if (selectionOffset != oldOffset || selectionSize != oldSize) {
            fireSelectionChanged(new BitRange(oldOffset, oldSize));
        //}
    }
    
    public long getSelectionSize() {
        return selectionSize;
    }
    
    public void setSelectionSize(long size) {
        setSelection(getSelectionOffset(), size);
    }
    
    public long getSelectionOffset() {
        return selectionOffset;
    }
    
    public void setSelectionOffset(long offset) {
        setSelection(offset, getSelectionSize());
    }
    
    /*
     *  Fires a dataResize event only if newDataSize != oldDataSize
     */
    public void setDataSize(long newSize) {
        long oldSize = data.getBitSize();
        
        if (oldSize > newSize) {
            Data d = data.copy(newSize, oldSize - newSize);
            data.delete(newSize, oldSize - newSize);
            addUndo(new DataUndo(newSize, 0, d));
        }
        else if (oldSize < newSize) {
            data.replace(oldSize, 0, new Data(newSize - oldSize));
            addUndo(new DataUndo(oldSize, newSize - oldSize, new Data()));
        }
        
        if (oldSize != newSize) {
            fireDataChanged(0, 0, true);
            long bitEnd = getSelectionOffset() + getSelectionSize();
            if (bitEnd > newSize) {
                //Adjust Selection to new Size
                setSelection(getSelectionOffset(), getSelectionSize());
            }
        }
    }
    
    public void paste(Data newData) {
        Data d = data.copy(selectionOffset, selectionSize);
        data.replace(selectionOffset, selectionSize, newData);
        addUndo(new DataUndo(selectionOffset, newData.getBitSize(), d));
        if (newData.getBitSize() == selectionSize) {
            fireDataChanged(selectionOffset, selectionSize, false);
        }
        else {
            fireDataChanged(selectionOffset, getBitSize() - selectionOffset, true);
            setSelectionSize(newData.getBitSize());
        }
    }
    
    public void paste(Data newData, long bitOffset, long bitSize) {
        setSelection(bitOffset, bitSize);
        paste(newData);
    }
    
    public void delete() {
        Data d = data.copy(selectionOffset, selectionSize);
        data.delete(selectionOffset, selectionSize);
        if (d.getBitSize() > 0) {
            addUndo(new DataUndo(selectionOffset, 0, d));
            fireDataChanged(selectionOffset, getBitSize() - selectionOffset, true);
            setSelectionSize(0);
        }
    }
    
    public boolean hasUndo() {
        return !undos.isEmpty();
    }
    
    public boolean hasRedo() {
        return !redos.isEmpty();
    }
    
    /**
     *	Undo
     *
     *	Callee must check for existence of Undo
     */
    public void undo() {
        DataUndo u = (DataUndo) undos.get(undos.size() - 1);
        undos.remove(undos.size() - 1);
        Data d = data.copy(u.getBitOffset(), u.getBitSize());
        redos.add(new DataUndo(u.getBitOffset(), u.getDataSize(), d));
        data.replace(u.getBitOffset(), u.getBitSize(), u.getData());
        if (u.getBitSize() == u.getDataSize()) {
            fireDataChanged(u.getBitOffset(), u.getBitSize(), false);
        }
        else {
            fireDataChanged(u.getBitOffset(), getBitSize() - u.getBitOffset(), true);
        }
        setSelection(u.getBitOffset(), u.getDataSize());
        if (undos.isEmpty()) {
            fireStateChanged();
        }
    }
    
    public void redo() {
        DataUndo u = (DataUndo) redos.get(redos.size() - 1);
        redos.remove(redos.size() - 1);
        Data d = data.copy(u.getBitOffset(), u.getBitSize());
        undos.add(new DataUndo(u.getBitOffset(), u.getDataSize(), d));
        data.replace(u.getBitOffset(), u.getBitSize(), u.getData());
        if (u.getBitSize() == u.getDataSize()) {
            fireDataChanged(u.getBitOffset(), u.getBitSize(), false);
        }
        else {
            fireDataChanged(u.getBitOffset(), getBitSize() - u.getBitOffset(), true);
        }
        setSelection(u.getBitOffset(), u.getDataSize());
        if (redos.isEmpty()) {
            fireStateChanged();
        }
    }
    
    public BitRange[] diff(Data d, int granularity) {
        return data.diff(d, getSelectionOffset(), granularity);
    }
    
    /**
     *  Searches for all occurences of d in this dataModel using the current selection
     */
    public BitRange[] findAll(Data d, long granularity) {
        long offset = getSelectionOffset();
        long maxOffset = offset + getSelectionSize() - d.getBitSize();
        ArrayList intervals = new ArrayList();
        
        offset = data.search(d, true, offset, granularity);
        while (offset != -1 && offset <= maxOffset) {
            intervals.add(new BitRange(offset, d.getBitSize()));
            offset += d.getBitSize();
            offset = data.search(d, true, offset, granularity);
        }
        
        return (BitRange[]) intervals.toArray(new BitRange[0]);
    }
    
    /**
     *  paste replaceData on all selections in intvals
     */
    public void replace(BitRange[] intervals, Data replaceData) {
        //We have to do it backwards in order not to fuck up our static interval offsets
        // in case the replaceData.getBitSize() != findData.getBitSize()
        for (int i = intervals.length - 1; i > -1; i--) {
            setSelection(intervals[i].getStart(), intervals[i].getSize());
            paste(replaceData);
        }
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    /**
     *	Add Undo
     *
     *	Clears Redos
     */
    protected void addUndo(DataUndo undo) {
        undos.add(undo);
        redos = new ArrayList();
    }
    
    protected void fireDataChanged() {
        fireDataChanged(0, getBitSize(), true);
    }
    
    protected void fireDataChanged(long offset, long size, boolean wasResized) {
        Object[] listeners = dataModelListener.toArray();
        DataChangeEvent dataEvent = new DataChangeEvent(this, new BitRange(offset, size), wasResized);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((DataModelListener) listeners[i]).dataChanged(dataEvent);
        }
        setDataChanged(true);
    }
    
    protected void fireSelectionChanged(BitRange oldBitRange) {
        Object[] listeners = dataModelListener.toArray();
        // System.out.println("fireSelecteionChanged. listener length: " + listeners.length);
        DataSelectionEvent dataEvent = new DataSelectionEvent(this, oldBitRange, getBitRange());
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            try {
                ((DataModelListener) listeners[i]).selectionChanged(dataEvent);
            } catch (Exception e) {
                System.out.println(e);
            }
            
        }
    }
    
    protected void fireStateChanged() {
        Object[] listeners = stateChangeListener.toArray();
        StateChangeEvent changeEvent = new StateChangeEvent(this);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((StateChangeListener) listeners[i]).stateChanged(changeEvent);
        }
    }
}