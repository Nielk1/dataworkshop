package dataWorkshop.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.Element;

import dataWorkshop.Utils;
import dataWorkshop.logging.Logger;
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
public class DataRecognizer implements XMLSerializeable
{

	public final static String CLASS_NAME = "DataRecognizer";

	public final static String DATA_SIGNATURE_TEMPLATES_TAG = "DataSignatureTemplates";
	public final static String FILENAME_SUFFIX_TEMPLATES_TAG = "FilenameSuffixTemplates";
	public final static String DEFAULT_TEMPLATES_TAG = "DefaultTemplates";

	DataSignatureTemplate[] dataSignatures =
	{ 
			new DataSignatureTemplate("Gzip compressed file", new String[] { "gzip.str" }, new Data(16, 0x1F8B)), 
			new DataSignatureTemplate("Java executable", new String[] { "class.str" }, new Data(32, 0xCAFEBABE)),
			new DataSignatureTemplate("Windows executable", new String[] {"pe.str"} , new Data(16, 0x4D5A))
	 };

	FilenameSuffixTemplate[] filenameSignatures =
	{
		new FilenameSuffixTemplate("Palm resource file", new String[] { "prc.str" }, "prc"),
		new FilenameSuffixTemplate("Palm database file", new String[] { "pdb.str" }, "pdb"),
		new FilenameSuffixTemplate("Captured Ethernet traffic in libcap format", new String[] { "libcap.str" }, "libcap"),
		new FilenameSuffixTemplate("Ext2 Filesystem dump", new String[] { "ext2.str" }, "ext2"),
	};

	DefaultTemplate[] defaultSignatures = { 
		new DefaultTemplate("Vanilla Hexeditor", new String[] { "hex.str", "ascii.str" }),
		new DefaultTemplate("None", new String[0])
	};

	private Logger logger;

	/******************************************************************************
	 *	Constructors
	 */
	public DataRecognizer()
	{
	}

	/******************************************************************************
	 *	XMLSerializeable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		XMLSerializeFactory.serialize(context, DATA_SIGNATURE_TEMPLATES_TAG, getDataSignatures());
		XMLSerializeFactory.serialize(context, FILENAME_SUFFIX_TEMPLATES_TAG, getFilenameSignatures());
		XMLSerializeFactory.serialize(context, DEFAULT_TEMPLATES_TAG, getDefaultSignatures());
	}

	public void deserialize(Element context)
	{
		setDataSignatures((DataSignatureTemplate[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, DATA_SIGNATURE_TEMPLATES_TAG)).toArray(new DataSignatureTemplate[0]));
		setFilenameSignatures((FilenameSuffixTemplate[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, FILENAME_SUFFIX_TEMPLATES_TAG)).toArray(new FilenameSuffixTemplate[0]));
		setDefaultSignatures((DefaultTemplate[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, DEFAULT_TEMPLATES_TAG)).toArray(new DefaultTemplate[0]));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public FilenameSuffixTemplate[] getFilenameSignatures()
	{
		return filenameSignatures;
	}

	public void setFilenameSignatures(FilenameSuffixTemplate[] filenameSignatures)
	{
		this.filenameSignatures = filenameSignatures;
	}

	public DataSignatureTemplate[] getDataSignatures()
	{
		return dataSignatures;
	}

	public void setDataSignatures(DataSignatureTemplate[] dataSignatures)
	{
		this.dataSignatures = dataSignatures;
	}

	public DefaultTemplate[] getDefaultSignatures()
	{
		return defaultSignatures;
	}

	public void setDefaultSignatures(DefaultTemplate[] defaultSignatures)
	{
		this.defaultSignatures = defaultSignatures;
	}

	public ViewTemplate[] getPossibleViewTemplates(Data data)
	{
		ArrayList result = new ArrayList();

		for (int i = 0; i < dataSignatures.length; i++)
		{
			if (data.equals(dataSignatures[i].getSignature(), 0))
			{
				result.add(dataSignatures[i]);
			}
		}
		
		result.addAll(Arrays.asList(defaultSignatures));
		return (ViewTemplate[]) result.toArray(new ViewTemplate[0]);
	}

	/**
	 * @param file
	 * @return Signature[]
	 */
	public ViewTemplate[] getPossibleViewTemplates(File file)
	{
		ArrayList result = new ArrayList();

		long size = Math.min(file.length(), Utils.alignUp(getMaximumSignatureBitSize(), 8) / 8);
		try
		{
			FileInputStream fstream = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fstream);

			byte[] byteData = new byte[(int) size];
			in.read(byteData);

			in.close();
			fstream.close();

			Data signature = new Data(size * 8, byteData);
			
			result.addAll(Arrays.asList(getPossibleViewTemplates(signature)));
		}
		catch (FileNotFoundException e)
		{
			logger.warning(e.toString());
		}
		catch (IOException e)
		{
			logger.warning(e.toString());
		}

		String filename = file.getName();
		for (int i = 0; i < filenameSignatures.length; i++)
		{
			if (filename.endsWith(filenameSignatures[i].getSuffix()))
			{
				result.add(filenameSignatures[i]);
			}
		}

		return (ViewTemplate[]) result.toArray(new ViewTemplate[0]);
	}

	/******************************************************************************
	 *	Private Methods
	 */
	/*
	 *  return the bitSize of the largest signature
	 */
	private long getMaximumSignatureBitSize()
	{
		long max = 0;
		for (int i = 0; i < dataSignatures.length; i++)
		{
			max = Math.max(max, dataSignatures[i].getSignature().getBitSize());
		}
		return max;
	}
}
