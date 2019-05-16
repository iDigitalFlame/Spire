package com.derp.fm;

import java.io.IOException;
import org.netcom.io.Encoder;
import org.netcom.io.ItemStorage;
import org.netcom.io.Stream;
import org.netcom.io.TimeStamp;

public final class FileRecord implements ItemStorage
{
	protected final TimeStamp file_Time;

	protected boolean file_Dir;
	protected long file_Size;
	protected String file_Name;
	protected String file_Path;

	public FileRecord()
	{
		file_Dir = false;
		file_Size = 0;
		file_Name = "error";
		file_Path = "";
		file_Time = new TimeStamp();
	}

	public final void readStorage(Encoder itemEnc, Stream inStream) throws IOException
	{
		file_Time.readStorage(itemEnc, inStream);
		file_Dir = itemEnc.readBoolean(inStream);
		file_Size = itemEnc.readLong(inStream);
		file_Name = itemEnc.readString(inStream);
		file_Path = itemEnc.readLongString(inStream);
	}
	public final void writeStorage(Encoder itemEnc, Stream outStream) throws IOException
	{
		file_Time.writeStorage(itemEnc, outStream);
		itemEnc.writeBoolean(outStream, file_Dir);
		itemEnc.writeLong(outStream, file_Size);
		itemEnc.writeString(outStream, file_Name);
		itemEnc.writeLongString(outStream, file_Path);
	}

	public final boolean equals(Object compareItem)
	{
		return compareItem instanceof FileRecord &&
			   ((FileRecord)compareItem).file_Path.equals(file_Path) &&
			   ((FileRecord)compareItem).file_Size == file_Size;
	}

	public final int hashCode()
	{
		return (int)(file_Size / 1024) + file_Path.hashCode();
	}

	public final String toString()
	{
		return "FILE: " + file_Name + " LOC: " + file_Path;
	}

	protected FileRecord(String FilePath)
	{
		file_Path = FilePath;
		file_Name = Stream.getFileName(FilePath);
		file_Size = Stream.getSize(FilePath);
		file_Dir = Stream.isDirectory(FilePath);
		file_Time = Stream.getModifiedStamp(FilePath);
		if(file_Name == null || file_Name.isEmpty()) file_Name = file_Path;
	}

	protected final FileRecord clone()
	{
		return new FileRecord(file_Path);
	}
}