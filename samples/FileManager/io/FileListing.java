package com.derp.io;

import java.io.IOException;
import java.util.Vector;

import org.netcom.io.Stream;
import org.netcom.net.NetworkPacket;

import com.derp.fm.FileRecord;

public class FileListing extends NetworkPacket
{
	protected final Vector<FileRecord> directoryFiles;

	protected String directoryName;

	public FileListing(Stream inStream)
	{
		super(false, 80, 75);
		directoryFiles = new Vector<FileRecord>();
		readItemData(inStream);
	}

	protected FileListing()
	{
		this((String)null);
	}
	protected FileListing(String Directory)
	{
		super(false, 80, 75);
		directoryName = Directory;
		directoryFiles = new Vector<FileRecord>();
		String[] a = Directory != null ? Stream.getList(Directory) : Stream.getDrivesPath();
		if(a != null && a.length > 0)
		{
			directoryFiles.ensureCapacity(a.length);
			//for(int b = 0; b < a.length; b++) directoryFiles.add(new FileRecord(directoryName + a[b]));
		}
	}

	protected final void readSubItem(Stream inStream) throws IOException
	{
		directoryName = itemEnc.readLongString(inStream);
		itemEnc.readStorageList(inStream, FileRecord.class, directoryFiles);
	}
	protected final void writeSubItem(Stream outStream) throws IOException
	{
		itemEnc.writeLongString(outStream, directoryName);
		itemEnc.writeStorageList(outStream, directoryFiles);
	}

	protected final FileListing doClone()
	{
		return new FileListing(directoryName);
	}
}