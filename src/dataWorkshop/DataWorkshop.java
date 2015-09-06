package dataWorkshop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.DataRecognizer;
import dataWorkshop.data.DataTransformation;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.logging.Logger;
import dataWorkshop.logging.SimpleFormatter;
import dataWorkshop.number.IntegerFormat;
import dataWorkshop.number.IntegerFormatFactory;
import dataWorkshop.data.structure.compiler.CompilerOptions;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

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
public class DataWorkshop implements LocaleStrings, XMLSerializeable
{

	final static String HELP_SWITCH = "-h";
	final static String LOG_LEVEL_SWITCH = "-l";
	final static String[] ALLOWED_LOG_LEVELS =
		{
			Level.OFF.getName(),
			Level.SEVERE.getName(),
			Level.WARNING.getName(),
			Level.INFO.getName(),
			Level.CONFIG.getName(),
			Level.FINE.getName(),
			Level.FINER.getName(),
			Level.FINEST.getName(),
			Level.ALL.getName(),
			};

	final static String GUI_MODE = "gui";
	final static String CONVERT_MODE = "converter";
	final static String FILTER_MODE = "filter";

	final static String HELP = DATA_WORKSHOP + " " + VERSION + "\n" + "written by " + AUTHOR + "\n" + "\n" + "Usage: DataWorkshop [options]" + "\n" +
		//"       DataWorkshop " + CONVERT_MODE + " file inputConverter outputConverter [options]" + NEW_LINE +
		//"       DataWorkshop " + FILTER_MODE + " file inputConverter filterUnit [options]" + NEW_LINE +
	"\n" + "Options" + "\n" + HELP_SWITCH + " - display this Information" + "\n";

	/**
	 *  Singelton Pattern
	 */
	private static DataWorkshop instance;

	public final static String CLASS_NAME = "DataWorkshop";

	public final static String DEFAULT_ENCODING_TAG = "DefaultEncoding";

	CompilerOptions compilerOptions = new CompilerOptions();
	DataEncoding defaultUnit = DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED);

	DataViewOption dataViewOption;
	String structuresDir;
	String documentationDir;
	String dataViewStylesheetsDir;
	IntegerFormatFactory formattedNumberFactory;
	DataRecognizer dataRecognizer;

	//Hashmap Keys for EditorOptions
	public static final String LINE_SEPARATOR = "Line Separator";
	public static final String FILE_SEPARATOR = "File Separator";
	public static final String UNIT_SEPARATOR = "Unit Separator";
	public static final String STRUCTURE_EXTENSION = "str";
	public static final String VIEW_DEFINITION_EXTENSION = "def";

	public static final String DATA_VIEW_STYLESHEETS_DIR = "DataView Stylesheets Dir";
	public static final String TRANSFORMATION_DIR = "Transformation Dir";
	public static final String STRUCTURE_DIR = "Structure Dir";
	public static final String WORKING_DIR = "File Dir";
	public static final String USER_MANUAL_DIR = "User Manual Dir";
	public static final String NO_NAME_FILE = "No Name File";

	public final static int MAX_GRANULARITY = Integer.MAX_VALUE;

	//Number of bits the value is changed when the user clicks the up/down buttons
	public final static int OFFSET_STEP_SIZE = 8;

	/**
	 * 
	 */
	public final static long FIND_ALL_WARNING_LIMIT = 10000;

	/**
	 *  Performance
	 */
	public final static int BYTES_READ_AT_ONCE_FROM_SOCKET = 40000;
	public final static int BYTES_READ_AT_ONCE = 10000;
	//  The time a thread waits before polling for new data
	public final static long WAIT_MILLIS_BEFORE_POLLING_DATA = 500;
	public final static int MAX_CHARACTERS_PER_LINE_IN_DIALOG = 50;
	public final static int DYNAMIC_DATA_VIEW_COMPILE_DELAY_IN_MILLIS = 1000;

	HashMap properties = new HashMap();
	HashMap files = new HashMap();

	Stylesheet[] stylesheets;
	public final static String PRETTY_XML_TRANSFORMER = "xml.xml";
	public final static String XML_STRIPPED = "xml_stripped.xml";

	HashMap transformers = new HashMap();

	private static Logger logger = Logger.getLogger(DataWorkshop.class);
	static private Level logLevel = Level.INFO;

	/******************************************************************************
	 *	Constructors
	 */
	public DataWorkshop()
	{
		dataViewOption = new DataViewOption();
		dataViewOption.setBitsPerLine(8 * 16);
		dataViewOption.setLinesPerPage(10);

		compilerOptions.setPointerStructureThreshold(1000);
		compilerOptions.setPointerStructureThresholdEnabled(true);

		putProperty(FILE_SEPARATOR, System.getProperty("file.separator"));
		putProperty(LINE_SEPARATOR, System.getProperty("line.separator"));
		putProperty(UNIT_SEPARATOR, " ");
		putProperty(STRUCTURE_EXTENSION, "str");

		putFile(DATA_VIEW_STYLESHEETS_DIR, new File("dataViewStylesheets" + getProperty(FILE_SEPARATOR)));
		putFile(TRANSFORMATION_DIR, new File("transformations" + getProperty(FILE_SEPARATOR)));
		putFile(STRUCTURE_DIR, new File("structures" + getProperty(FILE_SEPARATOR)));
		putFile(WORKING_DIR, new File(getProperty(FILE_SEPARATOR)));
		putFile(USER_MANUAL_DIR, new File("doc" + getProperty(FILE_SEPARATOR)));
		putFile(NO_NAME_FILE, new File(getFile(WORKING_DIR) + "No Name"));

		formattedNumberFactory = new IntegerFormatFactory(IntegerFormatFactory.HEXADECIMAL, IntegerFormatFactory.BYTES_BITS);
		dataRecognizer = new DataRecognizer();

		ArrayList list = new ArrayList();
		File stylesheetDir = getFile(DataWorkshop.DATA_VIEW_STYLESHEETS_DIR);
		logger.info("Scanning " + stylesheetDir.getAbsolutePath() + " for Stylesheets");
		File[] file = stylesheetDir.listFiles();
		if (file == null)
		{
			logger.severe(stylesheetDir.getAbsolutePath() + " does not exist");
		}
		else
		{
			for (int i = 0; i < file.length; i++)
			{
				if (file[i].isFile())
				{
					try
					{
						Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(file[i]));
						list.add(new Stylesheet(file[i].getName(), transformer));
						logger.info("Found " + file[i].getName());
					}
					catch (TransformerConfigurationException e)
					{
						logger.warning("Could not open " + file[i].getName() + " stylesheet. Error: ", e);
					}
				}
			}
		}
		stylesheets = (Stylesheet[]) list.toArray(new Stylesheet[0]);

		File transformationDir = getFile(DataWorkshop.TRANSFORMATION_DIR);

		logger.info("Scanning " + transformationDir.getAbsolutePath() + " for DataTransformations");
		File[] transformationFiles = transformationDir.listFiles();
		if (transformationFiles == null)
		{
			logger.severe(transformationDir.getAbsolutePath() + " does not exist");
		}
		else
		{
			for (int i = 0; i < transformationFiles.length; i++)
			{
				if (transformationFiles[i].isFile())
				{
					DataTransformation dataTransformation = (DataTransformation) XMLSerializeFactory.getInstance().deserialize(transformationFiles[i]);
					if (dataTransformation != null)
					{
						transformers.put(transformationFiles[i].getName(), dataTransformation);
						logger.info("Found " + dataTransformation.toString());
					}
					else
					{
						logger.warning("Could not open " + transformationFiles[i].getName() + " DataTransformation");
					}
				}
			}
		}
	}

	/******************************************************************************
	 *	XML Serializeable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(org.w3c.dom.Element context)
	{
		XMLSerializeFactory.serialize(context, DEFAULT_ENCODING_TAG, getDefaultDataConverter());
		XMLSerializeFactory.serialize(context, getIntegerFormatFactory());
		XMLSerializeFactory.serialize(context, getCompilerOptions());
		XMLSerializeFactory.serialize(context, getDataRecognizer());
		XMLSerializeFactory.serialize(context, getDataViewOption());
	}

	public void deserialize(Element context)
	{
		setDefaultDataConverter((DataEncoding) XMLSerializeFactory.deserializeFirst(context, DEFAULT_ENCODING_TAG));
		setIntegerFormatFactory((IntegerFormatFactory) XMLSerializeFactory.deserializeFirst(context));
		setCompilerOptions((CompilerOptions) XMLSerializeFactory.deserializeFirst(context));
		setDataRecognizer((DataRecognizer) XMLSerializeFactory.deserializeFirst(context));
		setDataViewOption((DataViewOption) XMLSerializeFactory.deserializeFirst(context));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static DataWorkshop getInstance()
	{
		if (instance == null)
		{
			instance = new DataWorkshop();
		}
		return instance;
	}

	public DataRecognizer getDataRecognizer()
	{
		return dataRecognizer;
	}

	public void setDataRecognizer(DataRecognizer dataRecognizer)
	{
		this.dataRecognizer = dataRecognizer;
	}

	public Stylesheet[] getStylesheets()
	{
		return stylesheets;
	}

	public Stylesheet getStylesheet(String name)
	{
		for (int i = 0; i < stylesheets.length; i++)
		{
			if (stylesheets[i].getName().equals(name))
			{
				return stylesheets[i];
			}
		}
		return null;
	}

	public DataTransformation getDataTransformation(String filename)
	{
		DataTransformation transformation = (DataTransformation) transformers.get(filename);
		if (transformation == null)
		{
			logger.severe("No DataTransformation defined for " + filename);
		}
		return transformation;
	}

	public CompilerOptions getCompilerOptions()
	{
		return compilerOptions;
	}

	public void setCompilerOptions(CompilerOptions compilerOptions)
	{
		this.compilerOptions = compilerOptions;
	}

	public DataEncoding getDefaultDataConverter()
	{
		return defaultUnit;
	}

	public void setDefaultDataConverter(DataEncoding unit)
	{
		this.defaultUnit = unit;
	}

	public DataViewOption getDataViewOption()
	{
		return dataViewOption;
	}

	public void setDataViewOption(DataViewOption dataViewOption)
	{
		this.dataViewOption = dataViewOption;
	}

	public String getProperty(String key)
	{
		return (String) properties.get(key);
	}

	public File getFile(String key)
	{
		return (File) files.get(key);
	}

	public IntegerFormat getUnsignedOffsetNumber()
	{
		return formattedNumberFactory.getUnsignedOffset();
	}

	public IntegerFormat getSignedOffsetNumber()
	{
		return formattedNumberFactory.getSignedOffset();
	}

	public IntegerFormat getUnsignedCount()
	{
		return formattedNumberFactory.getUnsignedCount();
	}

	public IntegerFormat getSignedCount()
	{
		return formattedNumberFactory.getSignedCount();
	}

	public IntegerFormatFactory getIntegerFormatFactory()
	{
		return formattedNumberFactory;
	}

	public void setIntegerFormatFactory(IntegerFormatFactory formattedNumberFactory)
	{
		this.formattedNumberFactory = formattedNumberFactory;
	}

	/******************************************************************************
	 *	Main
	 */
	public static void main(String[] args)
	{

		//String mode = GUI_MODE;
		// File file = null;
		//String inputUnit = null;
		//String outputUnit = null;
		// String filter = null;
		int i = 0;

		/*
		 *  Decide on mode
		 *
		if (args.length > 0) {
		    if (args[0].equals(GUI_MODE)) {
		        mode = GUI_MODE;
		        i++;
		    }
		    else if (args[0].equals(CONVERT_MODE)) {
		        mode = CONVERT_MODE;
		        i++;
		    }
		}
		 
		if (mode == CONVERT_MODE) {
		    if (args.length >= i + 3) {
		        file = new File(args[i]);
		        inputUnit = args[i + 1];
		        outputUnit = args[i + 2];
		        i += 3;
		    }
		    else {
		        System.out.println(HELP);
		        System.exit(0);
		    }
		}
		else if (mode == FILTER_MODE) {
		    if (args.length >= i + 3) {
		        file = new File(args[i]);
		        inputUnit = args[i + 1];
		        filter = args[i + 2];
		        i += 3;
		    }
		    else {
		        System.out.println(HELP);
		        System.exit(0);
		    }
		}*/

		/*
		 *  Read in general options
		 */
		while (i < args.length)
		{
			if (args[i].equals(HELP_SWITCH))
			{
				System.out.println(HELP);
				System.exit(0);
			}
			else if (args[i].equals(LOG_LEVEL_SWITCH))
			{
				i++;
				try
				{
					logLevel = Level.parse(args[i]);
				}
				catch (Exception e)
				{
					System.out.println("Invalid log level " + args[i] + ". Allowed values are " + Arrays.asList(ALLOWED_LOG_LEVELS));
					System.exit(0);
				}
				i++;
			}
		}

		System.out.println(ABOUT_STRING);
		System.out.println("\n");

		//		This will propagate to all classes living in the dataWorkshop package (including the subpackages)
		Logger l = Logger.getLogger("dataWorkshop");
		l.setUseParentHandlers(false);
		l.setLevel(logLevel);
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(logLevel);
		console.setFormatter(new SimpleFormatter());
		l.addHandler(console);

		logger.info("LogLevel=" + logLevel);

		DataWorkshop options = DataWorkshop.getInstance();

		File optionsFile = new File(SERIALIZED_DATAWORKSHOP);
		if (optionsFile.exists())
		{
			XMLSerializeFactory.getInstance().deserialize(optionsFile, options);
			logger.info(RESTORED_DATAWORKSHOP);
		}
		else
		{
			logger.info(NO_RESTORE_DATAWORKSHOP);
		}

		/*
		 *  GUI Mode
		 */
		if (true)
		{
			Editor editor = Editor.getInstance();
			File workbenchFile = new File(SERIALIZED_EDITOR);
			if (workbenchFile.exists())
			{
				XMLSerializeFactory.getInstance().deserialize(workbenchFile, editor);
				logger.info(RESTORED_EDITOR);
			}
			else
			{
				logger.info(NO_RESTORE_EDITOR);
				editor.setSize(640, 480);
			}
			editor.build();

			//Now our gui is set up and we can display the messages as MessageBoxes
			//output.setOutput(editor);
			editor.setVisible(true);

			// kill java-vm if window is closed by button
			editor.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					Editor.getInstance().fireActionEvent(Editor.EXIT_ACTION);
				}
			});
		}

		/**
		 *  Convert Mode
		 *
		 * else if (mode == CONVERT_MODE) {
		 * DataConverter input = DataConverter.get(inputUnit);
		 * if (input == null) {
		 * Output.writeError(ERROR_COULD_NOT_FIND_DATA_CONVERTER + " " + inputUnit);
		 * System.exit(0);
		 * }
		 * DataConverter output = DataConverter.get(outputUnit);
		 * if (output == null) {
		 * Output.writeError(ERROR_COULD_NOT_FIND_DATA_CONVERTER + " " +outputUnit);
		 * System.exit(0);
		 * }
		 * System.out.println(Converter.convert(Converter.convert(file, input), output));
		 * System.exit(0);
		 * }
		 * else if (mode == FILTER_MODE) {
		 * DataConverter fileConverter = DataConverter.get(inputUnit);
		 * if (fileConverter == null) {
		 * Output.writeError(ERROR_COULD_NOT_FIND_DATA_CONVERTER + " " + inputUnit);
		 * System.exit(0);
		 * }
		 * DataTransformer dataFilter = DataConverterFactory.getInstance().getDataTransformer(filter);
		 * if (dataFilter == null) {
		 * Output.writeError(ERROR_COULD_NOT_FIND_DATA_FILTER + " " +dataFilter);
		 * System.exit(0);
		 * }
		 * Data d = Converter.convert(file, fileConverter);
		 * if (d.getBitSize() % dataFilter.getBitSize() != 0) {
		 * Output.writeError(ERROR_DATA_BITSIZE_IS_NOT_ALIGNED_CORRECTLY + ". Data needs to be divideable by " + dataFilter.getBitSize() + " bits");
		 * System.exit(0);
		 * }
		 * Data newData = Converter.transform(d, dataFilter);
		 * System.out.println(Converter.convert(newData, fileConverter));
		 *
		 * System.exit(0);
		 * }*/
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected void putProperty(String key, String value)
	{
		properties.put(key, value);
	}

	protected void putFile(String key, File file)
	{
		files.put(key, file);
	}
}