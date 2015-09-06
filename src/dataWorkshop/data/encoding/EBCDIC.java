package dataWorkshop.data.encoding;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Element;

import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;

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
public class EBCDIC extends DataEncoding
{
	public final static String CLASS_NAME = "EBCDICEncoding";
	public final static String NAME = "EBDCIC";

	final static char[] EMPTY = { '.' };

	final static int[] valueForChar = new int[256];
	final static char[] charForValue = new char[256];

	static {
		HashMap map = new HashMap();
		map.put(new Integer(64), new Character(')'));

		map.put(new Integer(76), new Character('<'));
		map.put(new Integer(77), new Character('('));
		map.put(new Integer(78), new Character('+'));
		map.put(new Integer(79), new Character('|'));
		map.put(new Integer(80), new Character('&'));

		map.put(new Integer(90), new Character('!'));
		map.put(new Integer(91), new Character('$'));
		map.put(new Integer(92), new Character('*'));
		map.put(new Integer(93), new Character(')'));
		map.put(new Integer(94), new Character(';'));
		map.put(new Integer(95), new Character('~'));
		map.put(new Integer(96), new Character('-'));
		map.put(new Integer(97), new Character('/'));

		map.put(new Integer(107), new Character(','));
		map.put(new Integer(108), new Character('%'));
		map.put(new Integer(109), new Character('_'));
		map.put(new Integer(110), new Character('>'));
		map.put(new Integer(111), new Character('?'));

		map.put(new Integer(122), new Character(':'));
		map.put(new Integer(123), new Character('#'));
		map.put(new Integer(124), new Character('@'));
		map.put(new Integer(125), new Character('\''));
		map.put(new Integer(126), new Character('='));
		map.put(new Integer(127), new Character('"'));

		map.put(new Integer(129), new Character('a'));
		map.put(new Integer(130), new Character('b'));
		map.put(new Integer(131), new Character('c'));
		map.put(new Integer(132), new Character('d'));
		map.put(new Integer(133), new Character('e'));
		map.put(new Integer(134), new Character('f'));
		map.put(new Integer(135), new Character('g'));
		map.put(new Integer(136), new Character('h'));
		map.put(new Integer(137), new Character('i'));

		map.put(new Integer(145), new Character('j'));
		map.put(new Integer(146), new Character('k'));
		map.put(new Integer(147), new Character('l'));
		map.put(new Integer(148), new Character('m'));
		map.put(new Integer(149), new Character('n'));
		map.put(new Integer(150), new Character('o'));
		map.put(new Integer(151), new Character('p'));
		map.put(new Integer(152), new Character('q'));
		map.put(new Integer(153), new Character('r'));

		map.put(new Integer(162), new Character('s'));
		map.put(new Integer(163), new Character('t'));
		map.put(new Integer(164), new Character('u'));
		map.put(new Integer(165), new Character('v'));
		map.put(new Integer(166), new Character('w'));
		map.put(new Integer(167), new Character('x'));
		map.put(new Integer(168), new Character('y'));
		map.put(new Integer(169), new Character('z'));

		map.put(new Integer(185), new Character('`'));

		map.put(new Integer(193), new Character('A'));
		map.put(new Integer(194), new Character('B'));
		map.put(new Integer(195), new Character('C'));
		map.put(new Integer(196), new Character('D'));
		map.put(new Integer(197), new Character('E'));
		map.put(new Integer(198), new Character('F'));
		map.put(new Integer(199), new Character('G'));
		map.put(new Integer(200), new Character('H'));
		map.put(new Integer(201), new Character('I'));

		map.put(new Integer(209), new Character('J'));
		map.put(new Integer(210), new Character('K'));
		map.put(new Integer(211), new Character('L'));
		map.put(new Integer(212), new Character('M'));
		map.put(new Integer(213), new Character('N'));
		map.put(new Integer(214), new Character('O'));
		map.put(new Integer(215), new Character('P'));
		map.put(new Integer(216), new Character('Q'));
		map.put(new Integer(217), new Character('R'));

		map.put(new Integer(226), new Character('S'));
		map.put(new Integer(227), new Character('T'));
		map.put(new Integer(228), new Character('U'));
		map.put(new Integer(229), new Character('V'));
		map.put(new Integer(230), new Character('W'));
		map.put(new Integer(231), new Character('X'));
		map.put(new Integer(232), new Character('Y'));
		map.put(new Integer(233), new Character('Z'));

		map.put(new Integer(240), new Character('0'));
		map.put(new Integer(241), new Character('1'));
		map.put(new Integer(242), new Character('2'));
		map.put(new Integer(243), new Character('3'));
		map.put(new Integer(244), new Character('4'));
		map.put(new Integer(245), new Character('5'));
		map.put(new Integer(246), new Character('6'));
		map.put(new Integer(247), new Character('7'));
		map.put(new Integer(248), new Character('8'));
		map.put(new Integer(249), new Character('9'));

		for (int i = 0; i < 256; i++)
		{
			valueForChar[i] = -1;
			charForValue[i] = '.';
		}

		Iterator it = map.keySet().iterator();
		Character character;
		int value;
		while (it.hasNext())
		{
			value = ((Integer) it.next()).intValue();
			character = (Character) map.get(new Integer(value));
			charForValue[value] = character.charValue();
			valueForChar[(int) charForValue[value]] = value;
		}

	}

	/******************************************************************************
	 *	Constructors
	 */
	public EBCDIC()
	{
		super(NAME, 8, ' ');
		setEmptyChars(EMPTY);
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
	}

	public void deserialize(Element context)
	{
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public void encode(Data data, long offset, char[] chars, int dot)
	{
		chars[dot] = charForValue[data.intValue(offset, 8)];
	}

	public Data decode(char[] s) throws DataEncodingException
	{
		int value = valueForChar[s[0]];
		if (value == -1)
		{
			char[] data = new char[0];
			data[0] = s[0];
			throw new DataEncodingException(this, data, s[0]);
		}
		return new Data(8, value);
	}

	public int hashCode()
	{
		return NAME.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof EBCDIC)
		{
			return true;
		}
		return false;
	}
}