package org.netcom.ui;

import java.awt.Color;
import java.util.Arrays;
import org.netcom.lang.InstanceException;

public final class PixelImage
{
	private static final PixelImage[] pixelList = new PixelImage[256];
	
	protected final byte ID;
	protected final int[][] PixelData;
	
	private int NextPosX;
	private int NextPosY;
	
	public PixelImage(int[][] PixelCords)
	{
		ID = -1;
		PixelData = PixelCords.clone();
	}
	public PixelImage(int Width, int Height)
	{
		ID = -1;
		PixelData = new int[Width][Height];
	}
	
	public final void setPixel(int PosX, int PosY, int Color)
	{
		try
		{
			PixelData[PosX][PosY] = Color;
			NextPosX = PosX;
			NextPosY = PosY;
		}
		catch (ArrayIndexOutOfBoundsException E) { }
	}
	public final void setPixel(int PosX, int PosY, String Color)
	{
		setPixel(PosX, PosY, Color.hashCode());
	}
	public final void setPixel(int PosX, int PosY, Color Color)
	{
		setPixel(PosX, PosY, Color.getRGB());
	}
	public final void setNextPixel(int Color)
	{
		try
		{
			PixelData[NextPosX++][NextPosY++] = Color;
		}
		catch (ArrayIndexOutOfBoundsException E) { }
	}
	public final void setNextPixel(String Color)
	{
		setNextPixel(Color.hashCode());
	}
	public final void setNextPixel(Color Color)
	{
		setNextPixel(Color.getRGB());
	}
	
	public final int getWidth()
	{
		return PixelData.length;
	}
	public final int getHeight()
	{
		return PixelData[0].length;
	}
	public final int getArea()
	{
		return PixelData.length * PixelData[0].length;
	}
	
	public final PixelImage replaceColor(int ReplaceColor, int NewColor)
	{
		PixelImage a = clone();
		for(int b = 0; b < a.getWidth(); b++) for(int c = 0; c < a.getHeight(); c++)
			if(a.PixelData[b][c] == ReplaceColor) a.PixelData[b][c] = NewColor;
		return a;
	}
	public final PixelImage replaceColor(int ReplaceColor, String NewColor)
	{
		return replaceColor(ReplaceColor, NewColor.hashCode());
	}
	public final PixelImage replaceColor(int ReplaceColor, Color NewColor)
	{
		return replaceColor(ReplaceColor, NewColor.getRGB());
	}
	public final PixelImage replaceColor(String ReplaceColor, String NewColor)
	{
		return replaceColor(ReplaceColor.hashCode(), NewColor.hashCode());
	}
	public final PixelImage replaceColor(Color ReplaceColor, Color NewColor)
	{
		return replaceColor(ReplaceColor.getRGB(), NewColor.getRGB());
	}
	public final PixelImage clone()
	{
		PixelImage a = new PixelImage(getWidth(), getHeight());
		for(int b = 0; b < a.getWidth(); b++)
			a.PixelData[b] = Arrays.copyOf(PixelData[b], a.getHeight());
		return a;
	}
	
	protected static final PixelImage getByID(int PixelID)
	{
		return pixelList[PixelID];
	}
	
	private PixelImage(int PixelID, int[][] CordData)
	{
		ID = (byte)PixelID;
		if(pixelList[ID] != null) throw new InstanceException("Already a Pixel Image in slot: " + ID + "!");
		PixelData = CordData;
		pixelList[ID] = this;
	}
		
	public static final PixelImage SQUARE;
	public static final PixelImage CIRCLE;
	public static final PixelImage TRIANGLE;
	public static final PixelImage LETTER_A;
	public static final PixelImage LETTER_B;
	public static final PixelImage LETTER_C;
	public static final PixelImage LETTER_D;
	public static final PixelImage LETTER_E;
	public static final PixelImage LETTER_F;
	public static final PixelImage LETTER_G;
	public static final PixelImage LETTER_H;
	
	static
	{
		SQUARE = new PixelImage(0, new int[][]
			{
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1}
			});
		CIRCLE = new PixelImage(1, new int[][]
			{
				{0, 1, 1, 1, 0},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1},
				{0, 1, 1, 1, 0}
			}); 
		TRIANGLE = new PixelImage(2, new int[][]
			{
				{0, 0, 0, 0, 0},
				{0, 0, 1, 0, 0},
				{0, 1, 1, 1, 0},
				{0, 1, 1, 1, 0},
				{1, 1, 1, 1, 1}
			});
		LETTER_A = new PixelImage(3, new int[][]
			{
				{1, 1, 1, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 0, 1, 1}
			});
		LETTER_B = new PixelImage(4, new int[][]
			{
				{1, 1, 1, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 1, 0, 0},
				{1, 1, 0, 1, 1},
				{1, 1, 1, 1, 1}
			});
		LETTER_C = new PixelImage(5, new int[][]
			{
				{0, 1, 1, 1, 0},
				{1, 1, 1, 1, 1},
				{1, 1, 0, 0, 0},
				{1, 1, 1, 1, 1},
				{0, 1, 1, 1, 0}
			});
		LETTER_D = new PixelImage(6, new int[][]
			{
				{1, 1, 1, 1, 0},
				{1, 1, 0, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 1, 1, 0}
			});
		LETTER_E = new PixelImage(7, new int[][]
			{
				{1, 1, 1, 1, 1},
				{1, 1, 0, 0, 0},
				{1, 1, 1, 0, 0},
				{1, 1, 0, 0, 0},
				{1, 1, 1, 1, 1}
			});
		LETTER_F = new PixelImage(8, new int[][]
			{
				{1, 1, 1, 1, 0},
				{1, 1, 0, 0, 0},
				{1, 1, 1, 0, 0},
				{1, 1, 0, 0, 0},
				{1, 1, 0, 0, 0}
			});
		LETTER_G = new PixelImage(9, new int[][]
			{
				{0, 1, 1, 1, 1},
				{1, 1, 0, 0, 0},
				{1, 1, 0, 0, 1},
				{1, 1, 1, 1, 1},
				{0, 1, 1, 0, 1}
			});
		LETTER_H = new PixelImage(10, new int[][]
			{
				{1, 1, 0, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 1, 1, 1},
				{1, 1, 0, 1, 1},
				{1, 1, 0, 1, 1}
			});
	}
}