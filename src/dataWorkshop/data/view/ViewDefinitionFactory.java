package dataWorkshop.data.view;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.logging.Logger;
import dataWorkshop.number.IntegerFormatFactory;
import dataWorkshop.xml.XMLSerializeFactory;

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
public class ViewDefinitionFactory {
    
    public final static String PATH_TAG = "path";
    public final static String DATA_TAG = "data";
    public final static String BIT_OFFSET_TAG = "bitOffset";
    public final static String BIT_SIZE_TAG = "bitSize";
    
    public final static String BIT_START_TAG = "bitStart";
    public final static String BIT_END_TAG = "bitEnd";
    
    public final static String CLASS_NAME_TAG = "class";
    
    
    /******************************************************************************
     *	Constructors
     */
    
    /******************************************************************************
     *	Public Methods
     */
    public static DataFrame createDataFrame(BitRange[] intervals, DataEncoding unit) {
        DataFrame frame = new DataFrame();
        frame.setLabel("IntervalStructureRoot");
        
        DataEncodingField field;
        for (int i = 0; i < intervals.length; i++) {
            field = new DataEncodingField();
            field.setLabel(Integer.toString(i));
            field.setDataConverter(unit);
            field.setBitSize(intervals[i].getSize());
            field.setBitOffset(intervals[i].getStart());
            frame.add(field);
        }
        return frame;
    }
    
	public static Document serializeForXPathQuery(DataFrame root, Data data) {
	   Document doc = createDocument(root.getLabel());
	   serializeForXPathQuery(doc.getDocumentElement(), data, root);
	   return doc;
	}
    
     /**
     *  data is rendered structured in units and lines, information is added for
     *  further formatting in html or text using xslt
     *
     *  no information for data interpretation
     */
    public static Document renderDataToXML(DataFrame root, Data data, DataViewOption viewOptions, IntegerFormatFactory integerFormatFactory) {
        Document doc = createDocument(root.getLabel());
        renderDataToXML(doc.getDocumentElement(), data, root, viewOptions, integerFormatFactory);
        return doc;
    }
    
    /******************************************************************************
     *	Protected Methods
     */
	private static void serializeForXPathQuery(Element context, Data data, DataFrame node) {
			XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, node.getBitSize());
			XMLSerializeFactory.setAttribute(context, BIT_START_TAG, node.getBitOffset());
			XMLSerializeFactory.setAttribute(context, BIT_END_TAG, node.getBitSize() + node.getBitOffset());
			XMLSerializeFactory.setAttribute(context, PATH_TAG, node.getPath());
			XMLSerializeFactory.setAttribute(context, CLASS_NAME_TAG, node.getClassName());
        
			if (node.isField()) {
				XMLSerializeFactory.setAttribute(context, DATA_TAG, ((DataField) node).render(data));
			}
			else {
				for (int i = 0; i < node.getChildCount(); i++) {
					DataFrame child = (DataFrame) node.getChildAt(i);
					Element childElement = XMLSerializeFactory.addElement(context, child.getLabel());
					serializeForXPathQuery(childElement, data, child);
				}
			}
		}
		
    private static void renderDataToXML(Element context, Data data, DataFrame node, DataViewOption viewOptions, IntegerFormatFactory integerFormatFactory) {
        XMLSerializeFactory.setAttribute(context, BIT_SIZE_TAG, integerFormatFactory.getUnsignedOffset().toString(node.getBitSize(), false));
        XMLSerializeFactory.setAttribute(context, BIT_START_TAG, integerFormatFactory.getUnsignedOffset().toString(node.getBitOffset(), true));
        XMLSerializeFactory.setAttribute(context, BIT_END_TAG, integerFormatFactory.getUnsignedOffset().toString(node.getBitSize() + node.getBitOffset(), true));
		XMLSerializeFactory.setAttribute(context, PATH_TAG, node.getPath());
        /*
        if (viewOptions.isRenderSize()) {
            XMLSerializeFactory.setAttribute(element, BIT_SIZE_TAG, viewOptions.getIntegerFormatFactory().getUnsignedOffset().toString(node.getBitSize(), false));
        }
        
        if (viewOptions.isRenderOffset()) {
            XMLSerializeFactory.setAttribute(element, BIT_OFFSET_TAG, viewOptions.getIntegerFormatFactory().getUnsignedOffset().toString(node.getBitOffset(), false));
        }
         */
        
        XMLSerializeFactory.setAttribute(context, CLASS_NAME_TAG, node.getClassName());
        
        if (node.isField()) {
            Element dataElement;
            long bitOffset = node.getBitOffset();
            if (node instanceof DataEncodingField) {
                DataEncodingFieldLayout layout = viewOptions.createLayout((DataEncodingField) node);
                long bitEnd = bitOffset + node.getBitSize();
                long bitsPerLine;
                while (bitOffset < bitEnd) {
                    bitsPerLine = Math.min(layout.getBitsPerLine(), bitEnd);
                    dataElement = XMLSerializeFactory.serialize(context, DATA_TAG, layout.render(data, new BitRange(bitOffset, bitsPerLine)).getString());
                    XMLSerializeFactory.setAttribute(dataElement, BIT_OFFSET_TAG, integerFormatFactory.getUnsignedOffset().toString(bitOffset, true));
                    bitOffset += bitsPerLine;
                }
            }
            else if (node instanceof MapField) {
                  dataElement = XMLSerializeFactory.serialize(context, DATA_TAG, ((MapField) node).render(data));
                   XMLSerializeFactory.setAttribute(dataElement, BIT_OFFSET_TAG, integerFormatFactory.getUnsignedOffset().toString(bitOffset, true));
            }
        }
        else {
            for (int i = 0; i < node.getChildCount(); i++) {
				DataFrame child = (DataFrame) node.getChildAt(i);
				Element childElement = XMLSerializeFactory.addElement(context, child.getLabel());
                renderDataToXML(childElement, data, child, viewOptions, integerFormatFactory);
            }
        }
    }
    
    private static Document createDocument(String rootNodeName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(XMLSerializeFactory.XML_HEADER + "<" + rootNodeName + "/>")));
            return doc;
        }
        catch (ParserConfigurationException e) {
            Logger.getLogger(ViewDefinitionFactory.class).severe(e);
        }
        catch (SAXException e) {
			Logger.getLogger(ViewDefinitionFactory.class).severe(e);
        }
        catch (IOException e) {
			Logger.getLogger(ViewDefinitionFactory.class).severe(e);
        }
        return null;
    }
}