package dataWorkshop.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.DataNumber;
import dataWorkshop.data.DataRecognizer;
import dataWorkshop.data.DataSignatureTemplate;
import dataWorkshop.data.DataToDataMapping;
import dataWorkshop.data.DataTransformation;
import dataWorkshop.data.DefaultTemplate;
import dataWorkshop.data.FilenameSuffixTemplate;
import dataWorkshop.data.StringToDataMapping;
import dataWorkshop.data.encoding.Base64;
import dataWorkshop.data.encoding.EBCDIC;
import dataWorkshop.data.encoding.FractionEncoding;
import dataWorkshop.data.encoding.GroupedIntegerEncoding;
import dataWorkshop.data.encoding.IEEE754Decoded;
import dataWorkshop.data.encoding.IEEE754Raw;
import dataWorkshop.data.encoding.IntegerEncoding;
import dataWorkshop.data.encoding.MSDOSTimeDate;
import dataWorkshop.data.encoding.TimeInMillis;
import dataWorkshop.data.encoding.USAscii;
import dataWorkshop.data.transformer.AddValue;
import dataWorkshop.data.transformer.CRC32;
import dataWorkshop.data.transformer.Convert;
import dataWorkshop.data.transformer.Decrease;
import dataWorkshop.data.transformer.Fill;
import dataWorkshop.data.transformer.FindAndReplace;
import dataWorkshop.data.transformer.Increase;
import dataWorkshop.data.transformer.Negate;
import dataWorkshop.data.transformer.Not;
import dataWorkshop.data.transformer.Rotate;
import dataWorkshop.data.transformer.Shift;
import dataWorkshop.data.transformer.SwapByteOrder;
import dataWorkshop.data.view.DataEncodingField;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.MapField;
import dataWorkshop.data.structure.CaseStatement;
import dataWorkshop.data.structure.DataEncodingFieldDefinition;
import dataWorkshop.data.structure.MapFieldDefinition;
import dataWorkshop.data.structure.RepeatStatement;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.structure.UntilDelimiterFieldDefinition;
import dataWorkshop.data.structure.UntilDelimiterRepeatStatement;
import dataWorkshop.data.structure.UntilOffsetFieldDefinition;
import dataWorkshop.data.structure.UntilOffsetRepeatStatement;
import dataWorkshop.data.structure.atomic.CaseDefinition;
import dataWorkshop.data.structure.atomic.DelimiterDefinition;
import dataWorkshop.data.structure.atomic.EncodingDefinition;
import dataWorkshop.data.structure.atomic.LocationDefinition;
import dataWorkshop.data.structure.atomic.ModificationDefinition;
import dataWorkshop.data.structure.atomic.PointerDefinition;
import dataWorkshop.data.structure.atomic.PointerLengthDefinition;
import dataWorkshop.data.structure.atomic.SimpleModificationDefinition;
import dataWorkshop.data.structure.atomic.StaticLengthDefinition;
import dataWorkshop.data.structure.atomic.StringToDataMappingDefinition;
import dataWorkshop.data.structure.compiler.CompilerOptions;
import dataWorkshop.gui.dialogs.AboutDialog;
import dataWorkshop.gui.dialogs.DataClipboardDialog;
import dataWorkshop.gui.dialogs.DataViewOptionDialog;
import dataWorkshop.gui.dialogs.DiffDataDialog;
import dataWorkshop.gui.dialogs.ExportDataViewDialog;
import dataWorkshop.gui.dialogs.FindAndReplaceDialog;
import dataWorkshop.gui.dialogs.ManualDialog;
import dataWorkshop.gui.dialogs.PreferencesDialog;
import dataWorkshop.gui.dialogs.TextClipboardDialog;
import dataWorkshop.gui.dialogs.StructureDefinitionPaletteDialog;
import dataWorkshop.gui.editor.DataTransformationMenu;
import dataWorkshop.gui.editor.Editor;
import dataWorkshop.gui.editor.MyKeyBinding;
import dataWorkshop.logging.Logger;
import dataWorkshop.number.IntegerFormatFactory;

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
public class XMLSerializeFactory
{

	private static Logger logger = Logger.getLogger(XMLSerializeFactory.class);

	public final static String DTD_START = "<!DOCTYPE ";
	public final static String DTD_MIDDLE = " SYSTEM '";
	public final static String DTD_END = "'>";

	public final static String XML_HEADER = "<?xml version='1.0' encoding='utf-8'?>";

	public final static String PRETTY_XML_STYLESHEET =
		XML_HEADER
			+ "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'  version='1.0'>"
			+ "<xsl:output method='xml' indent='yes'/>"
			+ "<xsl:template match='@*|*'>"
			+ "<xsl:copy>"
			+ "<xsl:apply-templates select='@*|node()'/>"
			+ "</xsl:copy>"
			+ "</xsl:template>"
			+ "</xsl:stylesheet>";

	public final static char[] ILLEGAL_TAGNAME_CHARS = { ' ', '<', '>', '/', '(', ')' };

	final static String EMPTY_STRING = new String();
	final static String NUMBER_SEPARATOR = ",";

	//public final static String CLASS_NAME_TAG = "class";

	final static String ERROR_DESERIALIZING_WRONG_CLASS = "ERROR - wrong class";

	final static String ERROR_DESERIALIZING = "ERROR - deserializing";
	final static String TRUE = "true";

	//Init the mapping table to circument obfuscation
	/**
	 *  We have to create a mapping for all obfuscated classes which
	 *  are automatically serialized/deserialied. The Mapping table maps
	 *  the classname in the xml file to the obfuscated classname
	 *
	 *  !!Order is important as some of the classes here are interdependent
	 *  !! So we try to first initialize the more basic classes and later
	 *  !! the more complex dialogs.
	 *
	 *  Furthermore this serves as a reminder that each XMLSeriablizable class
	 *  needs an empty Constructor
	 */
	static HashMap obfuscatedClasses = new HashMap();
	static {
		//converter
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new Base64()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new MSDOSTimeDate()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new USAscii()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new IntegerEncoding()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new GroupedIntegerEncoding()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new EBCDIC()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new TimeInMillis()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new IEEE754Raw()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new IEEE754Decoded()));
		addXMLSerializeable(DataEncodingFactory.getDataEncoding(new FractionEncoding()));

		//dataView
		addXMLSerializeable(new DataViewOption());

		//pub
		addXMLSerializeable(new DefaultTemplate());
		addXMLSerializeable(new DataSignatureTemplate());
		addXMLSerializeable(new FilenameSuffixTemplate());
		addXMLSerializeable(new StringToDataMapping());
		addXMLSerializeable(new DataToDataMapping());
		addXMLSerializeable(new CompilerOptions());
		addXMLSerializeable(new DataTransformationMenu());
		addXMLSerializeable(new MyKeyBinding());
		addXMLSerializeable(new DataRecognizer());

		//Structure   
		addXMLSerializeable(new RootStatement());
		addXMLSerializeable(new CaseStatement());
		addXMLSerializeable(new RepeatStatement());
		addXMLSerializeable(new UntilOffsetRepeatStatement());
		addXMLSerializeable(new UntilDelimiterRepeatStatement());
		addXMLSerializeable(new MapFieldDefinition());
		addXMLSerializeable(new DataEncodingFieldDefinition());
		addXMLSerializeable(new UntilOffsetFieldDefinition());
		addXMLSerializeable(new UntilDelimiterFieldDefinition());

		//Atomic
		addXMLSerializeable(new CaseDefinition());
		addXMLSerializeable(new EncodingDefinition());
		addXMLSerializeable(new DelimiterDefinition());
		addXMLSerializeable(new LocationDefinition());
		addXMLSerializeable(new ModificationDefinition());
		addXMLSerializeable(new PointerDefinition());
		addXMLSerializeable(new PointerLengthDefinition());
		addXMLSerializeable(new StaticLengthDefinition());
		addXMLSerializeable(new StringToDataMappingDefinition());
		addXMLSerializeable(new SimpleModificationDefinition());

		//textNode
		addXMLSerializeable(new DataEncodingField());
		addXMLSerializeable(new DataFrame());
		addXMLSerializeable(new MapField());

		//transformers
		addXMLSerializeable(new AddValue());
		addXMLSerializeable(new Increase());
		addXMLSerializeable(new Decrease());
		addXMLSerializeable(new CRC32());
		addXMLSerializeable(new Convert());
		addXMLSerializeable(new Fill());
		addXMLSerializeable(new FindAndReplace());
		addXMLSerializeable(new Negate());
		addXMLSerializeable(new Not());
		addXMLSerializeable(new SwapByteOrder());
		addXMLSerializeable(new Shift());
		addXMLSerializeable(new dataWorkshop.data.transformer.Translate());
		addXMLSerializeable(new Rotate());

		//data
		addXMLSerializeable(new Data());
		addXMLSerializeable(new DataNumber());
		addXMLSerializeable(new IntegerFormatFactory());
		addXMLSerializeable(new DataTransformation());

		//dialogs
		addXMLSerializeable(new AboutDialog());
		addXMLSerializeable(new DataClipboardDialog());
		addXMLSerializeable(new DiffDataDialog());
		addXMLSerializeable(new PreferencesDialog());
		addXMLSerializeable(new ManualDialog());
		addXMLSerializeable(new FindAndReplaceDialog());
		addXMLSerializeable(new DataViewOptionDialog());
		addXMLSerializeable(new ExportDataViewDialog());
		addXMLSerializeable(new TextClipboardDialog());
		addXMLSerializeable(new StructureDefinitionPaletteDialog());

		//xml
		addXMLSerializeable(new MyFont());
	}

	private static HashMap DTD_REFERENCES = new HashMap();
	static {
		DTD_REFERENCES.put(DataTransformation.CLASS_NAME, "dtd/DataTransformation.dtd");
		DTD_REFERENCES.put(RootStatement.CLASS_NAME, "dtd/RootStatement.dtd");
		DTD_REFERENCES.put(DataFrame.CLASS_NAME, "dtd/DataFrame.dtd");
		DTD_REFERENCES.put(DataWorkshop.CLASS_NAME, "dtd/DataWorkshop.dtd");
		DTD_REFERENCES.put(Editor.CLASS_NAME, "dtd/Editor.dtd");
	}

	private static XMLSerializeFactory instance;
	DocumentBuilder validatingBuilder;
	DocumentBuilder builder;

	/******************************************************************************
	 *	Constructor
	 */
	private XMLSerializeFactory()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new MyErrorHandler());
			builder.setEntityResolver(new MyEntityResolver());

			factory.setValidating(true);
			validatingBuilder = factory.newDocumentBuilder();
			validatingBuilder.setErrorHandler(new MyErrorHandler());
		}
		catch (ParserConfigurationException e)
		{
			logger.severe(e.toString());
		}
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static void addXMLSerializeable(XMLSerializeable klasse)
	{
		if (obfuscatedClasses.containsKey(klasse.getClassName()))
		{
			throw new RuntimeException("Name collision between " + obfuscatedClasses.get(klasse.getClassName()) + " and " + ((Object) klasse).getClass());
		}

		obfuscatedClasses.put(klasse.getClassName(), ((Object) klasse).getClass());
		logger.finest("ObfuscationMapping: " + klasse.getClassName() + " <-> " + ((Object) klasse).getClass());
	}

	public static XMLSerializeFactory getInstance()
	{
		if (instance == null)
		{
			instance = new XMLSerializeFactory();
		}
		return instance;
	}

	/**
	 *  Serializes an XMlSerializable object into an xml string. The name of the
	 *  root node in the xml string is the classname.
	 */
	public String serialize(XMLSerializeable object)
	{
		Document doc = null;
		try
		{
			doc = builder.parse(new InputSource(new StringReader(XMLSerializeFactory.XML_HEADER + "<" + object.getClassName() + "/>")));
			Element root = doc.getDocumentElement();
			//setAttribute(root, CLASS_NAME_TAG, object.getClassName());
			object.serialize(root);

			StreamSource stylesheet = new StreamSource(new StringReader(PRETTY_XML_STYLESHEET));
			Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesheet);
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			return writer.toString();
		}
		catch (IOException e)
		{
			logger.severe(e);
		}
		catch (SAXException e)
		{
			logger.severe(XML_HEADER);
			logger.severe(e.getException());
			logger.severe(e.getMessage());
		}
		catch (TransformerConfigurationException e)
		{
			logger.severe(e);
		}
		catch (TransformerException e)
		{
			logger.severe(e);
		}
		return null;
	}

	public boolean serialize(XMLSerializeable serializable, File file)
	{
		try
		{
			long start = System.currentTimeMillis();
			FileWriter out = new FileWriter(file);
			String xml = serialize(serializable);
			String dtdReference = (String) DTD_REFERENCES.get(serializable.getClassName());
			if (dtdReference != null)
			{
				int headerEnd = xml.indexOf(XML_HEADER);
				headerEnd += XML_HEADER.length() + 1;
				xml = xml.substring(0, headerEnd) + "\n" + DTD_START + serializable.getClassName() + DTD_MIDDLE + dtdReference + DTD_END + xml.substring(headerEnd);
			}
			if (xml != null)
			{
				out.write(xml);
			}
			out.close();
			logger.profile("Serializing " + ((Object) serializable).toString(), start);
			return true;
		}
		catch (FileNotFoundException ee)
		{
			System.out.println(ee);
		}
		catch (IOException ee)
		{
			System.out.println(ee);
		}
		return false;
	}

	/**
	 * Uses a validating parser
	 */
	public XMLSerializeable deserialize(File file)
	{
		String xml = createString(file);
		if (xml != null)
		{
			return deserialize(xml, true);
		}
		return null;
	}

	/**
	* Uses a validating parser
	*/
	public boolean deserialize(File file, XMLSerializeable serializeable)
	{
		String xml = createString(file);
		if (xml != null)
		{
			return deserialize(xml, serializeable, true);
		}
		return false;
	}

	/**
	 *  Restore instance from xml string
	 *
	 *  @param  String - xml data
	 *  @return boolean - true if success
	 */
	public XMLSerializeable deserialize(String xml, boolean validate)
	{
		Document doc = createDocument(xml, validate);
		if (doc != null)
		{
			return deserialize(doc.getDocumentElement());
		}
		return null;
	}

	/**
	 *  Restore instance from xml string
	 *
	 *  @param xml data
	 *  @return true if success
	 */
	public boolean deserialize(String xml, XMLSerializeable serializable, boolean validate)
	{
		Document doc = createDocument(xml, validate);
		if (doc != null)
		{
			long start = System.currentTimeMillis();
			boolean success = deserialize(doc.getDocumentElement(), serializable);
			logger.profile("Deserializing " + ((Object) serializable).toString(), start);
			return success;
		}
		return false;
	}

	public static void serialize(Element context, String tag, String valueTag, String[] values)
	{
		Element root = XMLSerializeFactory.addElement(context, tag);
		for (int i = 0; i < values.length; i++)
		{
			XMLSerializeFactory.serialize(root, valueTag, values[i]);
		}
	}

	public static String[] deserializeAsStringArray(Element context, String tag)
	{
		Element root = XMLSerializeFactory.getElement(context, tag);
		Element[] elem = XMLSerializeFactory.getChildElements(root);
		String[] values = new String[elem.length];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = getNodeValue(elem[i]);
		}
		context.removeChild(root);
		return values;
	}

	public static String deserializeAsString(Element context)
	{
		/**
		 * :TODO: this is probably very slow
		 */
		Element[] elem = getChildElements(context);
		context.removeChild(elem[0]);
		return getNodeValue(elem[0]);
	}

	public static void serialize(Element context, String tag, XMLSerializeable object)
	{
		XMLSerializeFactory.serialize(XMLSerializeFactory.addElement(context, tag), object);
	}

	public static void serialize(Element context, XMLSerializeable object)
	{
		Element root = XMLSerializeFactory.addElement(context, object.getClassName());
		//setAttribute(root, CLASS_NAME_TAG, object.getClassName());
		object.serialize(root);
	}

	public static void serialize(Element context, String tag, XMLSerializeable[] objects)
	{
		Element root = XMLSerializeFactory.addElement(context, tag);
		for (int i = 0; i < objects.length; i++)
		{
			serialize(root, objects[i]);
		}
	}

	public static Object deserializeFirst(Element context, String tag)
	{
		Element child = getElement(context, tag);
		context.removeChild(child);
		if (child != null)
		{
			return deserializeFirst(child);
		}
		return null;
	}

	public static boolean deserializeFirst(Element context, XMLSerializeable serializeable)
	{
		return deserializeFirst(context, serializeable.getClassName(), serializeable);
	}

	public static boolean deserializeFirst(Element context, String tag, XMLSerializeable serializeable)
	{
		try
		{
			NodeList nodeList = XPathAPI.selectNodeList(context, tag);
			int len = nodeList.getLength();
			if (len == 1)
			{
				return deserialize((Element) nodeList.item(0), serializeable);
			}
			else
			{
				logger.warning(ERROR_DESERIALIZING + " Found " + tag + " " + len + " times");
			}
		}
		catch (TransformerException e)
		{
			logger.warning(e.toString());
		}
		return false;
	}

	public static Object deserializeFirst(Element context)
	{
		Element[] elem = getChildElements(context);
		context.removeChild(elem[0]);
		return deserialize(elem[0]);
	}

	public static Object[] deserializeAll(Element context, String tag)
	{
		Element element = getElement(context, tag);
		if (element != null)
		{
			context.removeChild(element);
			return deserializeAll(element);
		}
		return null;
	}

	public static Object[] deserializeAll(Element context)
	{
		Element[] elem = getChildElements(context);
		Object[] objects = new Object[elem.length];
		for (int i = 0; i < elem.length; i++)
		{
			context.removeChild(elem[i]);
			objects[i] = deserialize(elem[i]);
		}
		return objects;
	}

	public static Element getElement(Element context, String tag)
	{
		NodeList list = context.getChildNodes();
		int max = list.getLength();
		int i = 0;
		while (i < max)
		{
			Node node = list.item(i);
			if (node.getNodeName().equals(tag))
			{
				return (Element) node;
			}
			i++;
		}
		logger.warning("Could not find unique " + tag + " in " + context);
		return null;
	}

	public static int deserializeAsInt(Element context)
	{
		return Integer.parseInt(deserializeAsString(context));
	}

	public static int[] deserializeAsIntArray(Element context)
	{
		return parseIntArray(deserializeAsString(context));
	}

	public static long deserializeAsLong(Element context)
	{
		return Long.parseLong(deserializeAsString(context));
	}

	public static double deserializeAsDouble(Element context)
	{
		return Double.parseDouble(deserializeAsString(context));
	}

	public static boolean deserializeAsBoolean(Element context)
	{
		//Somehow the Boolean.getBoolean() java code is fucked up and always returns false
		//anyway, our code is faster too
		return toBoolean(deserializeAsString(context));
	}

	public static int getAttributeAsInt(Element context, String element)
	{
		return Integer.parseInt(context.getAttribute(element));
	}

	public static long getAttributeAsLong(Element context, String element)
	{
		return Long.parseLong(context.getAttribute(element));
	}

	public static double getAttributeAsDouble(Element context, String element)
	{
		return Double.parseDouble(context.getAttribute(element));
	}

	public static boolean getAttributeAsBoolean(Element context, String element)
	{
		return toBoolean(context.getAttribute(element));
	}

	public static int[] getAttributeAsIntArray(Element context, String element)
	{
		return parseIntArray(getAttribute(context, element));
	}

	public static String getAttribute(Element context, String element)
	{
		return context.getAttribute(element);
	}

	public static void setAttribute(Element element, String attribute, String value)
	{
		assert value != null;
		assert attribute != null;
		assert element != null;
		element.setAttribute(attribute, value);
	}

	public static void setAttribute(Element element, String attribute, int value)
	{
		element.setAttribute(attribute, Integer.toString(value));
	}

	public static void setAttribute(Element element, String attribute, long value)
	{
		element.setAttribute(attribute, Long.toString(value));
	}

	public static void setAttribute(Element element, String attribute, double value)
	{
		element.setAttribute(attribute, Double.toString(value));
	}

	public static void setAttribute(Element element, String attribute, boolean value)
	{
		element.setAttribute(attribute, (new Boolean(value)).toString());
	}

	public static void setAttribute(Element element, String attribute, int[] values)
	{
		element.setAttribute(attribute, toString(values));
	}

	public static Element serialize(Element context, String tag, String value)
	{
		Element node = addElement(context, tag);
		node.appendChild(context.getOwnerDocument().createTextNode(value));
		return node;
	}

	public static Element serialize(Element context, String xPathName, boolean value)
	{
		return serialize(context, xPathName, (new Boolean(value)).toString());
	}

	public static Element serialize(Element context, String xPathName, int value)
	{
		return serialize(context, xPathName, Integer.toString(value));
	}

	public static Element serialize(Element context, String xPathName, int[] values)
	{
		return serialize(context, xPathName, toString(values));
	}

	public static Element serialize(Element context, String xPathName, long value)
	{
		return serialize(context, xPathName, Long.toString(value));
	}

	public static Element addElement(Element context, String tag)
	{
		Element newNode = XMLSerializeFactory.createElement(context.getOwnerDocument(), tag);
		context.appendChild(newNode);
		return newNode;
	}

	public static Element[] getChildElements(Node context)
	{
		NodeList list = context.getChildNodes();
		int len = list.getLength();
		ArrayList e = new ArrayList();
		Node node;
		for (int i = 0; i < len; i++)
		{
			node = list.item(i);
			if (node.getNodeType() == Document.ELEMENT_NODE)
			{
				e.add(node);
			}
		}
		return (Element[]) e.toArray(new Element[0]);
	}

	public static void checkBounds(int value, int min, int max, String name)
	{
		if (min > value || value > max)
		{
			throw new RuntimeException(name + " : " + value + " is not in valid range " + min + " - " + max);
		}
	}

	/******************************************************************************
	 *	Private Methods
	 */
	/**
	 * Will always return at least a String of length 0, never a null pointer
	*/
	private static String getNodeValue(Element element)
	{
		Node node = element.getFirstChild();
		if (node == null)
		{
			return EMPTY_STRING;
		}
		String value = node.getNodeValue();
		if (value == null)
		{
			return EMPTY_STRING;
		}
		return value;
	}

	/**
	   *  Restore instance from xml string
	   *
	   *  @param context -
	   *  @param  tag - xml data
	   *  @return object - true if success
	   */
	private static boolean deserialize(Element context, XMLSerializeable object)
	{
		//String className = getAttribute(context, CLASS_NAME_TAG);
		String className = context.getTagName();
		if (!className.equals(object.getClassName()))
		{
			logger.warning(ERROR_DESERIALIZING_WRONG_CLASS + ". Class found: " + className + " . Class needed: " + object.getClassName());
			return false;
		}
		object.deserialize(context);
		return true;
	}

	private static XMLSerializeable deserialize(Element element)
	{
		XMLSerializeable object = null;
		String classname = element.getTagName();
		//String classname = getAttribute(element, CLASS_NAME_TAG);
		Class c = (Class) obfuscatedClasses.get(classname);

		try
		{
			if (c != null)
			{
				object = (XMLSerializeable) c.newInstance();
				object.deserialize(element);

				if (object instanceof XMLSerializeableSingleton)
				{
					object = ((XMLSerializeableSingleton) object).getInstance(object);
				}
			}
			else
			{
				logger.severe("Could not find " + classname + " in list of known classes. " + obfuscatedClasses.values(), new Exception());
			}
		}
		catch (InstantiationException e)
		{
			logger.warning(e.toString());
			return null;
		}
		catch (IllegalAccessException e)
		{
			logger.warning(e.toString());
			return null;
		}
		return object;
	}

	private static boolean toBoolean(String bool)
	{
		//Somehow the Boolean.getBoolean() java code is fucked up and always returs false
		//anyway, our code is faster too
		if (bool != null && bool.equalsIgnoreCase(TRUE))
		{
			return true;
		}
		return false;
	}

	/*
	 *  We use this method to catch all exceptions from invalid NodeNames
	 */
	private static Element createElement(Document doc, String tag)
	{
		try
		{
			return doc.createElement(tag);
		}
		catch (Exception e)
		{
			logger.warning(e.toString());
			return null;
		}
	}

	private Document createDocument(String xml, boolean validate)
	{
		if (validate)
		{
			try
			{
				InputSource source = new InputSource(new StringReader(xml));
				String userDir = System.getProperty("user.dir");
				/**
				*  :HACK:Martin Pape:Sep 24, 2003
				*
				*	To make it work under windows have to prefix the user dir
				*   with "/" to make it a valid URI
				*/
				if (!userDir.startsWith("/"))
				{
					userDir = "/" + userDir;
				}
				source.setSystemId("file://" + userDir + "/");
				return validatingBuilder.parse(source);
			}
			catch (IOException e)
			{
				logger.warning(e.toString());
			}
			catch (SAXException e)
			{
				logger.warning(e.toString());
			}

			logger.warning("Validation failed, using the nonvalidating parser instead");
		}

		try
		{
			InputSource source = new InputSource(new StringReader(xml));
			String userDir = System.getProperty("user.dir");
			/**
			*  :HACK:Martin Pape:Sep 24, 2003
			*
			*	To make it work under windows have to prefix the user dir
			*   with "/" to make it a valid URI
			*/
			if (!userDir.startsWith("/"))
			{
				userDir = "/" + userDir;
			}
			source.setSystemId("file://" + userDir + "/");
			//source.setSystemId("file://" + System.getProperty("user.dir") + "/");
			return builder.parse(source);
		}
		catch (IOException e)
		{
			logger.warning(e.toString());
		}
		catch (SAXException e)
		{
			logger.warning(e.toString());
		}

		return null;
	}

	private static String createString(File file)
	{
		char[] array = new char[(int) file.length()];
		try
		{
			FileReader reader = new FileReader(file);
			reader.read(array);
			reader.close();
			return new String(array);
		}
		catch (FileNotFoundException e)
		{
			logger.warning(e.toString());
		}
		catch (IOException e)
		{
			logger.warning(e.toString());
		}
		return null;
	}

	private static String toString(int[] array)
	{
		String s = new String();
		if (array.length > 0)
		{
			s += Integer.toString(array[0]);
			int i = 1;
			while (i < array.length)
			{
				s += NUMBER_SEPARATOR;
				s += Integer.toString(array[i]);
				i++;
			}
		}
		return s;
	}

	private static int[] parseIntArray(String s)
	{
		StringTokenizer tokenizer = new StringTokenizer(s, NUMBER_SEPARATOR);
		int[] array = new int[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens())
		{
			array[i] = Integer.parseInt(tokenizer.nextToken());
			i++;
		}
		return array;
	}
}
