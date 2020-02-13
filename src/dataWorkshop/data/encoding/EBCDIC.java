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
		HashMap<Integer, Character> map = new HashMap<Integer, Character>();
		map.put(Integer.valueOf(64), Character.valueOf(')'));

		map.put(Integer.valueOf(76), Character.valueOf('<'));
		map.put(Integer.valueOf(77), Character.valueOf('('));
		map.put(Integer.valueOf(78), Character.valueOf('+'));
		map.put(Integer.valueOf(79), Character.valueOf('|'));
		map.put(Integer.valueOf(80), Character.valueOf('&'));

		map.put(Integer.valueOf(90), Character.valueOf('!'));
		map.put(Integer.valueOf(91), Character.valueOf('$'));
		map.put(Integer.valueOf(92), Character.valueOf('*'));
		map.put(Integer.valueOf(93), Character.valueOf(')'));
		map.put(Integer.valueOf(94), Character.valueOf(';'));
		map.put(Integer.valueOf(95), Character.valueOf('~'));
		map.put(Integer.valueOf(96), Character.valueOf('-'));
		map.put(Integer.valueOf(97), Character.valueOf('/'));

		map.put(Integer.valueOf(107), Character.valueOf(','));
		map.put(Integer.valueOf(108), Character.valueOf('%'));
		map.put(Integer.valueOf(109), Character.valueOf('_'));
		map.put(Integer.valueOf(110), Character.valueOf('>'));
		map.put(Integer.valueOf(111), Character.valueOf('?'));

		map.put(Integer.valueOf(122), Character.valueOf(':'));
		map.put(Integer.valueOf(123), Character.valueOf('#'));
		map.put(Integer.valueOf(124), Character.valueOf('@'));
		map.put(Integer.valueOf(125), Character.valueOf('\''));
		map.put(Integer.valueOf(126), Character.valueOf('='));
		map.put(Integer.valueOf(127), Character.valueOf('"'));

		map.put(Integer.valueOf(129), Character.valueOf('a'));
		map.put(Integer.valueOf(130), Character.valueOf('b'));
		map.put(Integer.valueOf(131), Character.valueOf('c'));
		map.put(Integer.valueOf(132), Character.valueOf('d'));
		map.put(Integer.valueOf(133), Character.valueOf('e'));
		map.put(Integer.valueOf(134), Character.valueOf('f'));
		map.put(Integer.valueOf(135), Character.valueOf('g'));
		map.put(Integer.valueOf(136), Character.valueOf('h'));
		map.put(Integer.valueOf(137), Character.valueOf('i'));

		map.put(Integer.valueOf(145), Character.valueOf('j'));
		map.put(Integer.valueOf(146), Character.valueOf('k'));
		map.put(Integer.valueOf(147), Character.valueOf('l'));
		map.put(Integer.valueOf(148), Character.valueOf('m'));
		map.put(Integer.valueOf(149), Character.valueOf('n'));
		map.put(Integer.valueOf(150), Character.valueOf('o'));
		map.put(Integer.valueOf(151), Character.valueOf('p'));
		map.put(Integer.valueOf(152), Character.valueOf('q'));
		map.put(Integer.valueOf(153), Character.valueOf('r'));

		map.put(Integer.valueOf(162), Character.valueOf('s'));
		map.put(Integer.valueOf(163), Character.valueOf('t'));
		map.put(Integer.valueOf(164), Character.valueOf('u'));
		map.put(Integer.valueOf(165), Character.valueOf('v'));
		map.put(Integer.valueOf(166), Character.valueOf('w'));
		map.put(Integer.valueOf(167), Character.valueOf('x'));
		map.put(Integer.valueOf(168), Character.valueOf('y'));
		map.put(Integer.valueOf(169), Character.valueOf('z'));

		map.put(Integer.valueOf(185), Character.valueOf('`'));

		map.put(Integer.valueOf(193), Character.valueOf('A'));
		map.put(Integer.valueOf(194), Character.valueOf('B'));
		map.put(Integer.valueOf(195), Character.valueOf('C'));
		map.put(Integer.valueOf(196), Character.valueOf('D'));
		map.put(Integer.valueOf(197), Character.valueOf('E'));
		map.put(Integer.valueOf(198), Character.valueOf('F'));
		map.put(Integer.valueOf(199), Character.valueOf('G'));
		map.put(Integer.valueOf(200), Character.valueOf('H'));
		map.put(Integer.valueOf(201), Character.valueOf('I'));

		map.put(Integer.valueOf(209), Character.valueOf('J'));
		map.put(Integer.valueOf(210), Character.valueOf('K'));
		map.put(Integer.valueOf(211), Character.valueOf('L'));
		map.put(Integer.valueOf(212), Character.valueOf('M'));
		map.put(Integer.valueOf(213), Character.valueOf('N'));
		map.put(Integer.valueOf(214), Character.valueOf('O'));
		map.put(Integer.valueOf(215), Character.valueOf('P'));
		map.put(Integer.valueOf(216), Character.valueOf('Q'));
		map.put(Integer.valueOf(217), Character.valueOf('R'));

		map.put(Integer.valueOf(226), Character.valueOf('S'));
		map.put(Integer.valueOf(227), Character.valueOf('T'));
		map.put(Integer.valueOf(228), Character.valueOf('U'));
		map.put(Integer.valueOf(229), Character.valueOf('V'));
		map.put(Integer.valueOf(230), Character.valueOf('W'));
		map.put(Integer.valueOf(231), Character.valueOf('X'));
		map.put(Integer.valueOf(232), Character.valueOf('Y'));
		map.put(Integer.valueOf(233), Character.valueOf('Z'));

		map.put(Integer.valueOf(240), Character.valueOf('0'));
		map.put(Integer.valueOf(241), Character.valueOf('1'));
		map.put(Integer.valueOf(242), Character.valueOf('2'));
		map.put(Integer.valueOf(243), Character.valueOf('3'));
		map.put(Integer.valueOf(244), Character.valueOf('4'));
		map.put(Integer.valueOf(245), Character.valueOf('5'));
		map.put(Integer.valueOf(246), Character.valueOf('6'));
		map.put(Integer.valueOf(247), Character.valueOf('7'));
		map.put(Integer.valueOf(248), Character.valueOf('8'));
		map.put(Integer.valueOf(249), Character.valueOf('9'));

		for (int i = 0; i < 256; i++)
		{
			valueForChar[i] = -1;
			charForValue[i] = '.';
		}

		Iterator<Integer> it = map.keySet().iterator();
		Character character;
		int value;
		while (it.hasNext())
		{
			value = ((Integer) it.next()).intValue();
			character = (Character) map.get(Integer.valueOf(value));
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