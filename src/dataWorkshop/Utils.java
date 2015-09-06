package dataWorkshop;

import java.io.File;

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
public class Utils {
    
    public static long alignDown(long value, long granularity) {
        return (value / granularity) * granularity;
    }
    
    public static long alignUp(long value, long granularity) {
        if (value % granularity != 0) {
            return value - (value % granularity) + granularity;
        }
        return value;
    }
    
    public static int alignDown(int value, int granularity) {
        return (value / granularity) * granularity;
    }
    
    public static int alignUp(int value, int granularity) {
        if (value % granularity != 0) {
            return value - (value % granularity) + granularity;
        }
        return value;
    }
    
    /**
     *	Force Extension
     *
     *  Only force extension if file does not exist
     */
    public static File forceExtension(File file, String extension) {
        if (extension != null && !(file.exists())) {
            String name = file.getName();
            if (!name.endsWith(extension)) {
                file = new File(file.getAbsolutePath() + "." + extension);
            }
        }
        return file;
    }
}