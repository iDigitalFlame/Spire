package org.netcom.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.Vector;

import org.netcom.gui.Interface;
import org.netcom.io.Stream;
import org.netcom.io.Item;

public final class PixelScreen extends Item
{
	private final byte[][] pixelIndex;
	public final Interface pixelDisplay;
	private final Vector<PixelPosition> pixelImages;
	
	private Color pixelBase;
	
	public PixelScreen(int Width, int Height)
	{
		this(null, Width, Height, -1, -1, null);
	}
	public PixelScreen(int Width, int Height, int PosX, int PosY)
	{
		this(null, Width, Height, PosX, PosY, null);
	}
	public PixelScreen(String WindowTitle, int Width, int Height)
	{
		this(WindowTitle, Width, Height, -1, -1, null);
	}
	public PixelScreen(int Width, int Height, int PosX, int PosY, Object WindowColor)
	{
		this(null, Width, Height, PosX, PosY, WindowColor);
	}
	public PixelScreen(String WindowTitle, int Width, int Height, int PosX, int PosY)
	{
		this(WindowTitle, Width, Height, PosX, PosY, null);
	}
	public PixelScreen(String WindowTitle, int Width, int Height, int PosX, int PosY, Object WindowColor)
	{
		super(14);
		pixelIndex = new byte[Width][Height];
		pixelImages = new Vector<PixelPosition>();
		pixelDisplay = new Interface(WindowTitle, Width, Height, PosX, PosY);
		if(WindowColor instanceof Color) pixelBase = (Color)WindowColor;
		//else if(WindowColor instanceof Number) pixelBase = Interface.getColorDec(WindowColor.toString());
		else if(WindowColor == null) pixelBase = Color.black;
		//else pixelBase = Interface.getColor(WindowColor.toString());
		for(int a = 0; a < Width; a += 5) for(int b = 0; b < Height; b += 5)
		{
			pixelDisplay.addLabel("pixel:" + (b / 5) + "-" + (a / 5), "", 5, 5, a, b);
			pixelDisplay.setOpaque("pixel:" + (b / 5) + "-" + (a / 5), true);
		}
		pixelDisplay.setBackgroundAll(pixelBase);
	}
	
	public final void addLetter(char Letter, int Index, int PosX, int PosY)
	{
		addLetter(Letter, Color.WHITE, Index, PosX, PosY);
	}
	public final void addLetter(char Letter, String Color, int Index, int PosX, int PosY)
	{
		int a = pixelDisplay.hashCode() + Letter * Color.getBytes().hashCode() * Index * PosX * PosY;
		//addLetter(Letter, Interface.getColor(Color), Index, PosX, PosY);
		Integer.toBinaryString(a);
	}
	public final void addLetter(char Letter, Color Color, int Index, int PosX, int PosY)
	{
		addImage(PixelImage.getByID((byte)Letter - 62).replaceColor(1, Color), Index, PosX, PosY);
	}
	public final void addString(String Line, Color[] Color, int Index, int PosX, int PosY)
	{
		char[] a = Line.toCharArray();
		int b = PosY;
		for(int c = 0; c < a.length; c++)
		{
			addLetter(a[c], Color.length > c?Color[c]:Color[0], Index, PosX, b);
			b += PixelImage.getByID((byte)a[c] - 62).getWidth() + 1;
		}
	}
	public final void addImage(PixelImage Image, int Index, int PosX, int PosY)
	{
		if(Image != null && Index > -1 && PosX > -1 && PosY > -1)
		{
			if(Image.ID > -1) pixelImages.add(new PixelPosition(Image.ID, PosX, PosY));
			for(int a = 0; a < Image.getWidth(); a++) for(int b = 0; b < Image.getHeight(); b++)
				if(pixelIndex[a + PosX][b + PosY] <= Index && Image.PixelData[a][b] != 0)
					pixelDisplay.setBackground("pixel:" + (a + PosX) + "-" + (b + PosY), getColor(Image.PixelData[a][b]));
		}
	}
	
	public int hashCode()
	{
		return 0;
	}

	public String toString()
	{
		return null;
	}

	protected void readItem(Stream inStream) throws IOException
	{
		
	}
	protected void writeItem(Stream outStream) throws IOException
	{
		
	}
	protected void setCorrupted()
	{
		
	}

	protected Item doClone()
	{
		return null;
	}

	private static final Color getColor(int ColorNumber)
	{
		return Color.getColor("", ColorNumber);//Interface.getColorDec(Integer.toString(ColorNumber));
	}
}