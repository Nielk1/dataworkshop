package dataWorkshop.gui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.MySocket;
import dataWorkshop.data.Data;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.dialogs.*;

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
public class SocketProjectPane extends ProjectPane {
    
    MySocket socket;
    ReadSocketThread readSocketThread;
    
    /******************************************************************************
     *	Constructors
     */
    public SocketProjectPane(MySocket socket, DataModel dataModel) throws
        IOException 
    {
        super(dataModel);
        this.socket = socket;
        
        readSocketThread = new ReadSocketThread(new BufferedInputStream(socket.getSocket().getInputStream()), dataModel);
        readSocketThread.start();
    }
    
    /******************************************************************************
     *	Public Methods
     */
    public MySocket getSocket() {
        return socket;
    }
    
    public String getShortName() {
        return socket.toString();
    }
    
    public void close() {
        readSocketThread.kill();
        while (readSocketThread.isAlive()) {
        }
        super.close();
    }
    
      /******************************************************************************
     *	ReadDataThread
     */
    public class ReadSocketThread extends Thread implements LocaleStrings {
        
        InputStream inputStream;
        DataModel dataModel;
        boolean keepRunning = true;
        
        public ReadSocketThread(InputStream inputStream, DataModel dataModel) {
            this.inputStream = inputStream;
            this.dataModel = dataModel;
        }
        
        public void kill() {
            keepRunning = false;
        }
        
        public void run() {
            byte[] bytes = new byte[DataWorkshop.BYTES_READ_AT_ONCE_FROM_SOCKET];
            int bytesRead;
            
            try {
                setStatus("Listening ...");
                while (keepRunning) {
                    sleep(DataWorkshop.WAIT_MILLIS_BEFORE_POLLING_DATA);
                    bytesRead = inputStream.available();
                    if (bytesRead > 0) {
                        bytesRead = inputStream.read(bytes, 0, Math.min(bytesRead, bytes.length));
                        dataModel.append(new Data(bytesRead * 8, bytes));
                    }
                }
            } catch (IOException e) {
				ErrorDialogFactory.show(SOCKET_LISTEN_ERROR, e);
            } catch (InterruptedException e) {
				ErrorDialogFactory.show(SOCKET_LISTEN_ERROR, e);
            }
        }
    }
}