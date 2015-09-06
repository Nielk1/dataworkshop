package dataWorkshop.logging;

import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

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
public class Logger
{
	private java.util.logging.Logger logger;

	private Logger(String name)
	{
		logger = java.util.logging.Logger.getLogger(name);
	}

	/**
	 * @return java.util.logging.Logger
	 */
	public synchronized static java.util.logging.Logger getAnonymousLogger()
	{
		return Logger.getAnonymousLogger();
	}

	/**
	 * @param resourceBundleName
	 * @return java.util.logging.Logger
	 */
	public synchronized static java.util.logging.Logger getAnonymousLogger(String resourceBundleName)
	{
		return Logger.getAnonymousLogger(resourceBundleName);
	}

	/**
	 * @param name
	 * @return java.util.logging.Logger
	 */
	public synchronized static Logger getLogger(String name)
	{
		return new Logger(name);
	}

	/**
	 * @param klass
	 * @return java.util.logging.Logger
	 */
	public synchronized static Logger getLogger(Class klass)
	{
		return new Logger(klass.getName());
	}

	/**
	 * @param handler
	 * @throws java.lang.SecurityException
	 */
	public synchronized void addHandler(Handler handler) throws SecurityException
	{
		logger.addHandler(handler);
	}

	/**
	 * @param msg
	 */
	public void config(String msg)
	{
		logger.config(msg);
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 */
	public void entering(String sourceClass, String sourceMethod)
	{
		logger.entering(sourceClass, sourceMethod);
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param param1
	 */
	public void entering(String sourceClass, String sourceMethod, Object param1)
	{
		logger.entering(sourceClass, sourceMethod, param1);
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param params
	 */
	public void entering(String sourceClass, String sourceMethod, Object[] params)
	{
		logger.entering(sourceClass, sourceMethod, params);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		return logger.equals(obj);
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 */
	public void exiting(String sourceClass, String sourceMethod)
	{
		logger.exiting(sourceClass, sourceMethod);
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param result
	 */
	public void exiting(String sourceClass, String sourceMethod, Object result)
	{
		logger.exiting(sourceClass, sourceMethod, result);
	}

	/**
	 * @param msg
	 */
	public void fine(String msg)
	{
		logger.fine(msg);
	}

	/**
	 * @param msg
	 */
	public void finer(String msg)
	{
		logger.finer(msg);
	}

	/**
	 * @param msg
	 */
	public void finest(String msg)
	{
		logger.finest(msg);
	}

	/**
	 * @return java.util.logging.Filter
	 */
	public Filter getFilter()
	{
		return logger.getFilter();
	}

	/**
	 * @return java.util.logging.Handler[]
	 */
	public synchronized Handler[] getHandlers()
	{
		return logger.getHandlers();
	}

	/**
	 * @return java.util.logging.Level
	 */
	public Level getLevel()
	{
		return logger.getLevel();
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return logger.getName();
	}

	/**
	 * @return java.util.logging.Logger
	 */
	public java.util.logging.Logger getParent()
	{
		return logger.getParent();
	}

	/**
	 * @return java.util.ResourceBundle
	 */
	public ResourceBundle getResourceBundle()
	{
		return logger.getResourceBundle();
	}

	/**
	 * @return java.lang.String
	 */
	public String getResourceBundleName()
	{
		return logger.getResourceBundleName();
	}

	/**
	 * @return boolean
	 */
	public synchronized boolean getUseParentHandlers()
	{
		return logger.getUseParentHandlers();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return logger.hashCode();
	}

	/**
	 * @param msg
	 */
	public void info(String msg)
	{
		logger.info(msg);
	}

	/**
	 * @param level
	 * @return boolean
	 */
	public boolean isLoggable(Level level)
	{
		return logger.isLoggable(level);
	}

	/**
	 * @param level
	 * @param msg
	 */
	public void log(Level level, String msg)
	{
		logger.log(level, msg);
	}

	/**
	 * @param level
	 * @param msg
	 * @param param1
	 */
	public void log(Level level, String msg, Object param1)
	{
		logger.log(level, msg, param1);
	}

	/**
	 * @param level
	 * @param msg
	 * @param params
	 */
	public void log(Level level, String msg, Object[] params)
	{
		logger.log(level, msg, params);
	}

	/**
	 * @param level
	 * @param msg
	 * @param thrown
	 */
	public void log(Level level, String msg, Throwable thrown)
	{
		logger.log(level, msg, thrown);
	}

	/**
	 * @param record
	 */
	public void log(LogRecord record)
	{
		logger.log(record);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg)
	{
		logger.logp(level, sourceClass, sourceMethod, msg);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 * @param param1
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, param1);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 * @param params
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, params);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param msg
	 * @param thrown
	 */
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown)
	{
		logger.logp(level, sourceClass, sourceMethod, msg, thrown);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param bundleName
	 * @param msg
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param bundleName
	 * @param msg
	 * @param param1
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param bundleName
	 * @param msg
	 * @param params
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
	}

	/**
	 * @param level
	 * @param sourceClass
	 * @param sourceMethod
	 * @param bundleName
	 * @param msg
	 * @param thrown
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown)
	{
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
	}

	/**
	 * @param handler
	 * @throws java.lang.SecurityException
	 */
	public synchronized void removeHandler(Handler handler) throws SecurityException
	{
		logger.removeHandler(handler);
	}

	/**
	 * @param newFilter
	 * @throws java.lang.SecurityException
	 */
	public void setFilter(Filter newFilter) throws SecurityException
	{
		logger.setFilter(newFilter);
	}

	/**
	 * @param newLevel
	 * @throws java.lang.SecurityException
	 */
	public void setLevel(Level newLevel) throws SecurityException
	{
		logger.setLevel(newLevel);
	}

	/**
	 * @param parent
	 */
	public void setParent(java.util.logging.Logger parent)
	{
		logger.setParent(parent);
	}

	/**
	 * @param useParentHandlers
	 */
	public synchronized void setUseParentHandlers(boolean useParentHandlers)
	{
		logger.setUseParentHandlers(useParentHandlers);
	}

	/**
	 * @param msg
	 */
	public void severe(String msg)
	{
		logger.severe(msg);
	}

	public void severe(String msg, Throwable thrown)
	{
		logger.log(Level.SEVERE, msg, thrown);
	}
	
	public void severe(Throwable thrown) {
		logger.log(Level.SEVERE, "Exception", thrown);
	}

	public void profile(String message, long startTime)
	{
		finest(message + ": " + (System.currentTimeMillis() - startTime) + " ms");
	}

	/**
	 * @param sourceClass
	 * @param sourceMethod
	 * @param thrown
	 */
	public void throwing(String sourceClass, String sourceMethod, Throwable thrown)
	{
		logger.throwing(sourceClass, sourceMethod, thrown);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return logger.toString();
	}

	/**
	 * @param msg
	 */
	public void warning(String msg)
	{
		logger.warning(msg);
	}

	public void warning(String msg, Throwable thrown)
	{
		logger.log(Level.WARNING, msg, thrown);
	}
	
	public void warning(Throwable thrown) {
			logger.log(Level.WARNING, "Exception", thrown);
		}
}
