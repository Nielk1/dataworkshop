package dataWorkshop.gui.editor;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

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
public class ExtensionFileFilter extends FileFilter {

    private Hashtable filters = new Hashtable();
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /******************************************************************************
     *	Constructors	
     */
    public ExtensionFileFilter() {
    	this.filters = new Hashtable();
    }

    public ExtensionFileFilter(String extension) {
    	this(extension,null);
    }

    public ExtensionFileFilter(String extension, String description) {
    	this();
    	if(extension!=null) {
    		addExtension(extension);
    	}
    	if(description!=null){
    		setDescription(description);
    	}
    }

    public ExtensionFileFilter(String[] filters) {
    	this(filters, null);
    }

    public ExtensionFileFilter(String[] f, String description) {
    	this();
    	for (int i = 0; i < f.length; i++) {
    		addExtension(f[i]);
    	}
    	if(description!=null) {
    		setDescription(description);
    	}
    }

    /******************************************************************************
     *	Public Methods	
     */
    public boolean accept(File file) {
	if(file != null) {
	    if(file.isDirectory()) {
			return true;
	    }
	    String extension = getExtension(file);
	    if(extension != null && filters.get(getExtension(file)) != null) {
			return true;
	    }
	}
	return false;
    }

    public String getExtension(File file) {
    	if(file != null) {
    		String filename = file.getName();
    		int i = filename.lastIndexOf('.');
    		if(i > 0 && i < filename.length() - 1) {
    			return filename.substring(i+1).toLowerCase();
    		}
    	}
    	return null;
    }

    public void addExtension(String extension) {
    	filters.put(extension.toLowerCase(), this);
    	fullDescription = null;
    }

    public String getDescription() {
    	if (fullDescription == null) {
    		if (description == null || isExtensionListInDescription()) {
    			fullDescription = description == null ? "(" : description + " (";
    			Enumeration extensions = filters.keys();
    			if (extensions != null & extensions.hasMoreElements()) {
    				fullDescription += "." + (String) extensions.nextElement();
    				while (extensions.hasMoreElements()) {
    					fullDescription += ", " + (String) extensions.nextElement();
    				}
    			}
    			fullDescription += ")";
    		} else {
    			fullDescription = description;
    		}
    	}
    	return fullDescription;
    }

    public void setDescription(String description) {
    	this.description = description;
    	fullDescription = null;
    }

    public void setExtensionListInDescription(boolean b) {
    	useExtensionsInDescription = b;
    	fullDescription = null;
    }

    public boolean isExtensionListInDescription() {
    	return useExtensionsInDescription;
    }
}