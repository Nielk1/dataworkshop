package dataWorkshop.gui.dialogs;

import java.awt.Component;

import javax.swing.JOptionPane;

import dataWorkshop.DataWorkshop;
import dataWorkshop.gui.editor.Editor;

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
public class ErrorDialogFactory
{
	public static void show(String error, Exception e) {
		ErrorDialogFactory.show(error + "\n\n" + e.getMessage(), Editor.getInstance());
	}
	
	public static void show(String error, Exception e, Component parent) {
		ErrorDialogFactory.show(error + "\n\n" + e.getMessage(), parent);
	}

	public static void show(String error, Component parent) {
		   JOptionPane.showMessageDialog(parent, ErrorDialogFactory.breakLines(error, DataWorkshop.MAX_CHARACTERS_PER_LINE_IN_DIALOG), "Error", JOptionPane.ERROR_MESSAGE);
	   }
	   
	public static String breakLines(String text, int maxLineSize) {
			String s = new String();
			int start = 0;
			int end = 0;
			while (end != -1) {
				end = text.indexOf(' ', start);
				while (end != -1 && text.indexOf(' ', end) - start < maxLineSize) {
					end = text.indexOf(' ', end);
					if (end != -1) {
						end++;
					}
				}
				if (end != -1) {
					s += text.substring(start, end) + "\n";
					start = end;
				}
			}
			s += text.substring(start, text.length());
			return s;
		}
}
