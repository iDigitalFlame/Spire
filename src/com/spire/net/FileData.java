package com.spire.net;

import com.spire.io.Item;
import java.io.IOException;
import com.spire.io.Streamable;

public final class FileData extends Item
{
	public static final byte ITEM_CLASS_ID = 9;
	
	protected final byte[] fileData;
	
	protected short fileSize;
	protected int filePosition;
	
	public final short fileSize()
	{
		return fileSize;
	}
	
	public final int hashCode()
	{
		return fileSize + filePosition;
	}
	public final int filePosition()
	{
		return filePosition;
	}
	
	public final String toString()
	{
		return "FileData(" + getItemID() + ") P" + filePosition;
	}
	
	protected FileData(int Position, Streamable InStream) throws IOException
	{
		this();
		filePosition = Position;
		fileSize = (short)InStream.getStreamInput().read(fileData);
	}
	
	public FileData()
	{
		super(ITEM_CLASS_ID);
		fileData = new byte[4096];
	}
	
	protected final void readItemFailure()
	{
		fileSize = 0;
		filePosition = -1;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		fileSize = itemEncoder.readShort(InStream);
		filePosition = itemEncoder.readInteger(InStream);
		itemEncoder.readByteArray(InStream, fileData, 0, fileSize);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeShort(OutStream, fileSize);
		itemEncoder.writeInteger(OutStream, filePosition);
		itemEncoder.writeByteArray(OutStream, fileData, 0, fileSize);
	}
}