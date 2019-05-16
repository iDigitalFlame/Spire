package org.netcom.ui;

public final class PixelPosition
{
	protected final byte ID;
	protected final int PosX;
	protected final int PosY;
	
	protected PixelPosition(byte PixelID, int PixelPosX, int PixelPosY)
	{
		ID = PixelID;
		PosX = PixelPosX;
		PosY = PixelPosY;
	}
}