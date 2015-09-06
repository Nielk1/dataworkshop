package dataWorkshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.data.Data;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.BrowserView;
import dataWorkshop.gui.data.view.DefaultDynamicDataView;
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
public class ProjectPane extends JPanel
implements LocaleStrings, InternalFrameListener {
    
    File dataFile;
    MyThread thread;
    
    JDesktopPane desktop;
    
    DataModel dataModel;
    
    private HashSet stateChangeListener = new HashSet();
    
    public static String LOADING_DATA = "Loading Data";
    public static String SAVING_DATA = "Saving Data";
    public static String OK = "";
    
    String status = OK;
    int progressMaximum = 0;
    int progressValue = 0;
    private Logger logger;
    
    /******************************************************************************
     *	Constructors
     */
    public ProjectPane(File dataFile, DataModel dataModel)
    throws FileNotFoundException {
        this(dataModel);
        this.dataFile = dataFile;
        logger = Logger.getLogger(this.getClass());
        thread = new ReadFileThread(dataFile, dataModel);
    }
    
    public ProjectPane(DataModel dataModel) {
        this.dataFile = DataWorkshop.getInstance().getFile(DataWorkshop.NO_NAME_FILE);
        this.dataModel = dataModel;
        setBorder(BorderFactory.createLineBorder(Color.black));
        desktop = new JDesktopPane();
        
        setLayout(new BorderLayout());
        add(desktop, BorderLayout.CENTER);
        rebuild();
    }
    
    /******************************************************************************
     *	InternalFrameListener Interface
     */
    public void internalFrameClosed(InternalFrameEvent e) {
        e.getInternalFrame().removeInternalFrameListener(this);
    }
    
    public void internalFrameOpened(InternalFrameEvent e) {
    }
    
    public void internalFrameActivated(InternalFrameEvent e) {
        /*
         *  The selected Frame has changed
         */
        fireStateChanged();
    }
    
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }
    
    public void internalFrameDeactivated(InternalFrameEvent e) {
    }
    
    public void internalFrameIconified(InternalFrameEvent e) {
    }
    
    public void internalFrameClosing(InternalFrameEvent e) {
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public void addStateChangeListener(StateChangeListener l) {
        stateChangeListener.add(l);
    }
    
    public void removeStateChangeListener(StateChangeListener l) {
        stateChangeListener.remove(l);
    }
    
    public String getStatus() {
        return status;
    }
    
    public boolean isDynamicDataViewSelected() {
        JInternalFrame frame = desktop.getSelectedFrame();
        if (frame != null && frame instanceof DynamicDataViewFrame) {
            return true;
        }
        return false;
    }
    
    public boolean hasDataView() {
        return (desktop.getSelectedFrame() != null);
    }
    
    public void rebuild() {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            //((DefaultDynamicDataView) strucViews.get(i)).rebuild();
        }
    }
    
    public String getDataFileName() {
        String name = dataFile.getName();
        if (dataModel.hasDataChanged()) {
            name += " *";
        }
        return name;
    }
    
    public File getDataFile() {
        return dataFile;
    }
    
    public void setDataFile(File file) {
        dataFile = file;
        dataModel.setDataChanged(false);
        fireStateChanged();
    }
    
    public String getShortName() {
        return dataFile.getName();
    }
    
    public void close() {
        if (thread != null) {
            thread.kill();
            while (thread.isAlive()) {
            }
        }
    }
    
    public void saveData(File file) {
        thread = new SaveFileThread(file, dataModel);
        thread.start();
    }
    
    public void loadData() {
        thread.start();
    }
    
    /**
     *  Get Name
     *
     *  The Name is the FileName + * if the data has been modified
     */
    public String getName() {
        String name = getShortName();
        if (dataModel.hasDataChanged()) {
            name += "* ";
        }
        return name;
    }
    
    /**
     *  Remove all DynamicDataViews
     */
    public void setDynamicDataView(RootStatement[] structures) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            frames[i].doDefaultCloseAction();
        }
        
        Rectangle rec = desktop.getBounds();
        int maxWidth = (int) rec.getWidth();
        int height = (int) rec.getHeight();
        int y = 0;
        int x = 0;
        int prefWidth;
        
        for (int i = 0; i < structures.length; i++) {
            JInternalFrame frame = addDynamicDataView(structures[i]);
            prefWidth = (int) frame.getPreferredSize().getWidth();
            if (x > maxWidth) {
                x = 0;
                y += 40;
            }
            frame.setSize(new Dimension(prefWidth, height));
            frame.setLocation(x, y);
            x += prefWidth;
        }
    }
  
     /**
     *  Add a new StrucView to this desktop and move the newly
     *  created StrucView to the front and select it.
     */
    public DynamicDataViewFrame addDynamicDataView(RootStatement structure) {
        DefaultDynamicDataView view = new DefaultDynamicDataView(structure, dataModel);
        //structure.addStateChangeListener(this);
        DynamicDataViewFrame pane = new DynamicDataViewFrame(view);
        addFrame(pane);
        return pane;
    }
    
    public StaticDataViewFrame addStaticDataView(DataFrame root, String label) {
        BrowserView view = new BrowserView(root, dataModel, DataWorkshop.getInstance().getDataViewOption());
        StaticDataViewFrame pane = new StaticDataViewFrame(view, label);
        addFrame(pane);
        return pane;
    }
    
    public ConfigurableDataViewFrame getSelectedFrame() {
        return (ConfigurableDataViewFrame) desktop.getSelectedFrame();
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
    
    public void setProgressBar(int max, String status) {
        progressMaximum = max;
        progressValue = 0;
        this.status = status;
        fireStateChanged();
    }
    
    public void setStatus(String status) {
        this.status = status;
        fireStateChanged();
    }
    
    public void updateProgressBar(int value) {
        progressValue = value;
        fireStateChanged();
    }
    
    public int getProgressValue() {
        return progressValue;
    }
    
    public int getProgressMaximum() {
        return progressMaximum;
    }
    
    /******************************************************************************
     *	Protected Methods
     */
    protected void fireStateChanged() {
        Object[] listeners = stateChangeListener.toArray();
        StateChangeEvent changeEvent = new StateChangeEvent(this);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((StateChangeListener) listeners[i]).stateChanged(changeEvent);
        }
    }
    
    protected void addFrame(JInternalFrame frame) {
        frame.addInternalFrameListener(this);
        desktop.add(frame);
        frame.moveToFront();
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
        	Logger.getLogger(ProjectPane.class).severe(e);
        }
    }
    
    /******************************************************************************
     *	ReadDataThread
     */
    public class ReadFileThread extends MyThread {
        
        File file;
        DataModel dataModel;
        
        public ReadFileThread(File file, DataModel dataModel) {
            this.file = file;
            this.dataModel = dataModel;
        }
        
        public void run() {
            int bitSize = DataWorkshop.BYTES_READ_AT_ONCE * 8;
            byte[] byteData = new byte[DataWorkshop.BYTES_READ_AT_ONCE];
            long fileSize = file.length();
            long dataSize = fileSize * 8;
            
            Data data = null;
            
            try {
                long start = System.currentTimeMillis();
                setProgressBar((int) dataSize, ProjectPane.LOADING_DATA);
                
                FileInputStream fstream = new FileInputStream(file);
                BufferedInputStream in = new BufferedInputStream(fstream);
                
                data = new Data(dataSize);
                long bitOffset = 0;
                while (bitOffset + bitSize < dataSize && keepRunning) {
                    in.read(byteData);
                    data.replace(bitOffset, bitSize, new Data(bitSize, byteData));
                    bitOffset += bitSize;
                    updateProgressBar((int) bitOffset);
                }
                
                if (bitOffset != dataSize) {
                    byteData = new byte[(int) ((dataSize - bitOffset) / 8)];
                    in.read(byteData);
                    bitSize = byteData.length * 8;
                    data.replace(bitOffset, bitSize, new Data(bitSize, byteData));
                }
                
                in.close();
                fstream.close();
                
				logger.profile("Loading and converting data", start);
                updateProgressBar((int) dataSize);
            }
            catch (FileNotFoundException e) {
                System.out.println(e);
            }
            catch (IOException e) {
                System.out.println(e);
            }
            
            dataModel.append(data);
            dataModel.setDataChanged(false);
            
            /*
             * PENDING: MPA 2002-10-03
             *  Is this necessary to make this object garbage collectable
             */
            //thread = null;
        }
    }
    
    /******************************************************************************
     *	SaveFileThread
     */
    public class SaveFileThread extends MyThread {
        
        File file;
        DataModel dataModel;
        
        public SaveFileThread(File file, DataModel dataModel) {
            this.file = file;
            this.dataModel = dataModel;
        }
        
        public void run() {
            try {
                Data data = dataModel.getData();
                long dataBitSize = data.getBitSize();
                long bitSize = DataWorkshop.BYTES_READ_AT_ONCE * 8;
                
                long start = System.currentTimeMillis();
                setProgressBar((int) (dataBitSize / 8), ProjectPane.SAVING_DATA);
                
                FileOutputStream fstream = new FileOutputStream(file);
                BufferedOutputStream out = new BufferedOutputStream(fstream);
                
                long bitOffset = 0;
                while (bitOffset + bitSize < dataBitSize && keepRunning) {
                    out.write( data.copy(bitOffset, bitSize).toByteArray());
                    bitOffset += bitSize;
                    updateProgressBar((int) (bitOffset / 8));
                }
                
                if (bitOffset != dataBitSize) {
                    out.write(data.copy(bitOffset, dataBitSize - bitOffset).toByteArray());
                }
                
                out.close();
                fstream.close();
                updateProgressBar((int) (dataBitSize / 8));
                logger.profile("Saving and converting data", start);
                // setChanged(false);
                setDataFile(file);
            }
            catch (FileNotFoundException e) {
                System.out.println(e);
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}