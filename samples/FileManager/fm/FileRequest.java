package com.derp.fm;

import java.io.IOException;
import java.util.Vector;
import org.netcom.io.Stream;
import org.netcom.net.NetworkPacket;

public final class FileRequest extends NetworkPacket
{
	private final Vector<FileRecord> request_FileData;

	protected byte request_Type;
	protected String request_Secondary;
	protected String request_Directory;

	public FileRequest(Stream inStream)
	{
		super(false, 75, 80);
		request_FileData = new Vector<FileRecord>();
		readItemData(inStream);
	}

	protected FileRequest(String RequestDirectory)
	{
		this(1, RequestDirectory, null);
	}
	protected FileRequest(int RequestType, String RequestDirectory)
	{
		this(RequestType, RequestDirectory, null);
	}
	protected FileRequest(int RequestType, String RequestDirectory, String RequestSecondary)
	{
		super(false, 75 + RequestType, 80);
		request_FileData = new Vector<FileRecord>();
		request_Type = (byte)RequestType;
		request_Directory = RequestDirectory;
		request_Secondary = RequestSecondary;
		{
			String[] a = request_Directory == null ? Stream.getDrivesPath() : Stream.getList(request_Directory);
			if(a != null)
			{
				request_FileData.ensureCapacity(a.length);
				for(int b = 0; b < a.length; b ++) request_FileData.add(new FileRecord(Stream.ensurePath(request_Directory) + a[b]));
			}
			else request_Type = -1;
		}
	}

	protected final void getAllRecords(Vector<FileRecord> Records)
	{
		Records.addAll(request_FileData);
	}
	protected final void readSubItem(Stream inStream) throws IOException
	{
		request_Type = itemEnc.readByte(inStream);
		request_Directory = itemEnc.readLongString(inStream);
		request_Secondary = itemEnc.readLongString(inStream);
		if(request_Type == 0) itemEnc.readStorageList(inStream, FileRecord.class, request_FileData);
		if(request_Directory.equals("~")) request_Directory = null;
		if(request_Secondary.equals("~")) request_Secondary = null;
	}
	protected final void writeSubItem(Stream outStream) throws IOException
	{
		itemEnc.writeByte(outStream, request_Type);
		itemEnc.writeLongString(outStream, request_Directory != null ? request_Directory : "~");
		itemEnc.writeLongString(outStream, request_Secondary != null ? request_Secondary : "~");
		if(request_Type == 0) itemEnc.writeStorageList(outStream, request_FileData);
	}

	protected final FileRequest doClone()
	{
		return null;
	}
}