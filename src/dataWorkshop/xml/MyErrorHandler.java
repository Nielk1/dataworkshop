package dataWorkshop.xml;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
public class MyErrorHandler implements ErrorHandler
{
	private Logger logger;

	public MyErrorHandler()
	{
		logger = Logger.getLogger(this.getClass().getName());
	}

	public void warning(SAXParseException e) throws SAXException
	{
		logger.log(Level.WARNING, e.toString());
	}

	public void error(SAXParseException e) throws SAXException
	{
		logger.log(Level.SEVERE, e.toString());
	}

	public void fatalError(SAXParseException e) throws SAXException
	{
		logger.log(Level.SEVERE, e.toString());
	}
}
