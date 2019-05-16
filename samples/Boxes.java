package com.derp.boxes;

import java.awt.Color;
import org.netcom.io.ItemThread;
import org.netcom.types.Constants;
import org.netcom.ui.Interface;
import org.netcom.ui.InterfaceStatic;

public final class Boxes extends ItemThread
{
	public Boxes()
	{
		setName(toString());
		setDaemon(false);
		setPriority(2);
		isAlive = true;
		start();
	}

	public final void run()
	{
		byte a = (byte)InterfaceStatic.getNumbeOfScreens(), b = 0;
		Interface[] d = new Interface[70 + Constants.RNG.nextInt(a > 0 ? 40 : 20)];
		for(byte e = 0; e < d.length; e++)
		{
			d[e] = new Interface(150, 150);
			d[e].setWindowBackground(Color.getHSBColor(Constants.RNG.nextFloat(), Constants.RNG.nextFloat(), Constants.RNG.nextFloat()));
			d[e].setWindowOpacity(0.4F);
			d[e].setOnTop();
			if(a > 0 && Constants.RNG.nextBoolean()) d[e].setWindowOnScreen(Constants.RNG.nextInt(a));
			d[e].showWindow();
		}
		while(isAlive) try
		{
			b = (byte)(1 + Constants.RNG.nextInt(d.length - 1));
			for(byte f = 0, c = (byte)Constants.RNG.nextInt(d.length); f < b; f++, c = (byte)Constants.RNG.nextInt(d.length))
			{
				if(a > 0 && Constants.RNG.nextInt(10) == 0) d[c].setWindowOnScreen(Constants.RNG.nextInt(a));
				d[f].setWindowPosition(Constants.RNG.nextInt(InterfaceStatic.getScreenWidth(d[c].getScreenIndex())),
										   Constants.RNG.nextInt(InterfaceStatic.getScreenHeight(d[c].getScreenIndex())));
			}
			Thread.sleep(100 + (Constants.RNG.nextInt(50) * 100));
		}
		catch (InterruptedException E) { }
	}

	public final boolean equals(Object compareItem)
	{
		return false;
	}

	public final int hashCode()
	{
		return 0;
	}

	public final String toString()
	{
		return "Boxes Application";
	}

	public final Boxes clone()
	{
		return new Boxes();
	}
}