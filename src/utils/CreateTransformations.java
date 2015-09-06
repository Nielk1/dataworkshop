package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import dataWorkshop.DataWorkshop;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataNumber;
import dataWorkshop.data.DataToDataMapping;
import dataWorkshop.data.DataTransformation;
import dataWorkshop.data.encoding.Base64;
import dataWorkshop.data.encoding.IntegerEncoding;
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
public class CreateTransformations {
    
    public static void main(String[] args) {
    
        HashMap transformers = new HashMap();
        
        DataToDataMapping mapping = new DataToDataMapping(8);
        for (int i = 0; i < 26; i++) {
            mapping.add(new Data(8, 'a' + i), new Data(8, 'A' + i));
        }
        transformers.put("toUpperCase.xml", new DataTransformation("To Uppercase", 8, new dataWorkshop.data.transformer.Translate(mapping)));
        
        transformers.put("swapWord.xml", new DataTransformation("Swap Word", 16, new SwapByteOrder(2)));
        transformers.put("swapDWord.xml",  new DataTransformation("Swap DWord", 32, new SwapByteOrder(4)));
        transformers.put("swapQWord.xml", new DataTransformation("Swap QWord", 64, new SwapByteOrder(8)));
        transformers.put("rot13Encoder.xml", new DataTransformation("Rot 13 Encoder", 8, new AddValue(DataNumber.getDataNumber(8, false, false), 13)));
        transformers.put("rot13Decoder.xml", new DataTransformation("Rot 13 Decoder", 8, new AddValue(DataNumber.getDataNumber(8, false, false), -13)));
        transformers.put("inc.xml", new DataTransformation("Inc", 1, new Increase(1)));
        transformers.put("dec.xml", new DataTransformation("Dec", 1, new Decrease(1)));
        transformers.put("fillWithZeros.xml", new DataTransformation("Fill With Zeros", 1, new Fill(new Data(1, 0))));
        transformers.put("fillWithOnes.xml", new DataTransformation("Fill With Ones", 1, new Fill(new Data(1, 1))));
        transformers.put("not.xml", new DataTransformation("Not", 1, new Not()));
        transformers.put("negate.xml", new DataTransformation("Negate", 1, new Negate()));
        transformers.put("rotateRight.xml",  new DataTransformation("Rotate Right", 1, new Rotate(1)));
        transformers.put("rotateLeft.xml", new DataTransformation("Rotate Left", 1, new Rotate(-1)));
		transformers.put("shiftRight.xml",  new DataTransformation("Shift Right", 1, new Shift(1, false)));
		transformers.put("shiftLeft.xml", new DataTransformation("Shift Left", 1, new Shift(-1, false)));
        transformers.put("asciiToUnicode.xml", new DataTransformation("Ascii to Unicode", 8, new FindAndReplace(new Data(0), new Data(8, 0), new IntegerEncoding(16, 8, false, false), new IntegerEncoding(16, 8, false, false), 8 )));
        
        transformers.put("crc32.xml",  new DataTransformation("CRC-32", 8, new CRC32()));
        transformers.put("base64Decode.xml", new DataTransformation("Base64 Decode", 8, new Convert(new  USAscii(), new Base64())));
        transformers.put("base64Encode.xml", new DataTransformation("Base64 Encode", 6, new Convert(new Base64(), new  USAscii())));
        transformers.put("hexDecode.xml", new DataTransformation("Hex Decode", 8, new Convert(new  USAscii(), new IntegerEncoding(16, 4, false, false))));
        transformers.put("hexEncode.xml", new DataTransformation("Hex Encode", 4, new Convert(new IntegerEncoding(16, 4, false, false), new  USAscii())));
        transformers.put("binaryDecode.xml", new DataTransformation("Binary Decode", 8, new Convert(new  USAscii(), new IntegerEncoding(2, 1, false, false))));
        transformers.put("binaryEncode.xml", new DataTransformation("Binary Encode", 1, new Convert(new IntegerEncoding(2, 1, false, false), new USAscii())));
        
        Iterator it = transformers.keySet().iterator();
        File transformerDir = DataWorkshop.getInstance().getFile(DataWorkshop.TRANSFORMATION_DIR);
        while (it.hasNext()) {
            String name = (String) it.next();
            File transformerFile = new File(transformerDir, name);
            try {
                transformerFile.createNewFile();
            }
            catch (IOException e) {
                System.out.println(e);
            }
            XMLSerializeFactory.getInstance().serialize((DataTransformation) transformers.get(name), transformerFile);
            System.out.println("Written " + name);
        }
        
		System.out.println("FINISHED");
        System.exit(0);
    }
}
