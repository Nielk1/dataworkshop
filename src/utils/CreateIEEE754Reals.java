package utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class CreateIEEE754Reals {
    
    static float[] floats = {
        (float) 0.0,
        (float) 0.5,
        (float) 0.333,
        (float) 0.001,
        (float) -0.0,
        (float) -0.5,
        (float) -0.333,
        (float) -0.001,
    };
    
    static int[] ints = {
        1,
        -1,
    };
    
    /******************************************************************************
     *	Constructors
     */
    public static void main(String[] args) {
        try {
            File file = new File("testReals.bin");
            FileOutputStream fstream = new FileOutputStream(file);
            DataOutputStream out = new DataOutputStream(fstream);
            for (int i = 0; i < floats.length; i++) {
                out.writeFloat(floats[i]);
            }
			for (int i = 0; i < floats.length; i++) {
				out.writeDouble((double) floats[i]);
			}
            for (int i = 0; i < ints.length; i++) {
                out.writeInt(ints[i]);
            }
            out.close();
            fstream.close();
            System.out.println("Finished");
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}