package com.derp.io;

import java.io.IOException;
import org.netcom.io.Stream;
import org.netcom.net.NetworkPacket;

public final class FileRequest extends NetworkPacket
{
	private String requestDirectory;

	public FileRequest(Stream inStream)
	{
		super(false, 80, 75);
		readItemData(inStream);
	}

	protected FileRequest(String Directory)
	{
		super(false, 80, 75);
		requestDirectory = Directory;
	}

	protected final void readSubItem(Stream inStream) throws IOException
	{
		requestDirectory = itemEnc.readLongString(inStream);
	}
	protected final void writeSubItem(Stream outStream) throws IOException
	{
		itemEnc.writeLongString(outStream, requestDirectory);
	}

	protected final FileRequest doClone()
	{
		return new FileRequest(requestDirectory);
	}
}