package com.spire.ui;

import java.awt.Color;
import java.util.HashMap;

import com.spire.ex.NullException;
import com.spire.ex.ReferenceException;
import com.spire.ex.StringException;
import com.spire.util.Constants;

public final class InterfaceStatic
{	
	private static final HashMap<Integer, Color> interfaceColorMap = new HashMap<Integer, Color>();
	
	public static final Color getRandomColor()
	{
		return Color.getHSBColor(Constants.RNG.nextFloat(), Constants.RNG.nextFloat(), Constants.RNG.nextFloat());
	}
	public static final Color getColor(int ColorRBG)
	{
		switch(ColorRBG)
		{
		case 0:
			return Color.BLACK;
		case -1:
			return Color.WHITE;
		case -256:
			return Color.YELLOW;
		case -14336:
			return Color.ORANGE;
		case -20561:
			return Color.PINK;
		case -65281:
			return Color.MAGENTA;
		case -65536:
			return Color.RED;
		case -4144960:
			return Color.LIGHT_GRAY;
		case -8355712:
			return Color.GRAY;
		case -12566464:
			return Color.DARK_GRAY;
		case -16711681:
			return Color.CYAN;
		case -16711936:
			return Color.GREEN;
		case -16776961:
			return Color.BLUE;
		case -16777216:
			return Color.BLACK;
		}
		Integer a = Integer.valueOf(ColorRBG);
		if(interfaceColorMap.containsKey(a))
			return interfaceColorMap.get(a);
		Color b = new Color(ColorRBG);
		interfaceColorMap.put(a, b);
		return b;
	}
	public static final Color getColor(String RBGHexString) throws NullException, StringException
	{
		return getColor(Integer.parseInt(RBGHexString, 16));
	}
	public static final Color getColorByName(String ColorName) throws NullException, ReferenceException
	{
		if(ColorName == null) throw new NullException("ColorName");
		if(ColorName.equalsIgnoreCase("black"))
			return Color.BLACK;
		else if(ColorName.equalsIgnoreCase("white"))
			return Color.WHITE;
		else if(ColorName.equalsIgnoreCase("yellow"))
			return Color.YELLOW;
		else if(ColorName.equalsIgnoreCase("orange"))
			return Color.ORANGE;
		else if(ColorName.equalsIgnoreCase("pink"))
			return Color.PINK;
		else if(ColorName.equalsIgnoreCase("magenta"))
			return Color.MAGENTA;
		else if(ColorName.equalsIgnoreCase("red"))
			return Color.RED;
		else if(ColorName.equalsIgnoreCase("lightgray"))
			return Color.LIGHT_GRAY;
		else if(ColorName.equalsIgnoreCase("gray"))
			return Color.GRAY;
		else if(ColorName.equalsIgnoreCase("darkgray"))
			return Color.DARK_GRAY;
		else if(ColorName.equalsIgnoreCase("cyan"))
			return Color.CYAN;
		else if(ColorName.equalsIgnoreCase("green"))
			return Color.GREEN;
		else if(ColorName.equalsIgnoreCase("blue"))
			return Color.BLUE;
		throw new ReferenceException("There is no color by that name!");
	}
}