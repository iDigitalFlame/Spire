package com.spire.net;

import java.io.File;
import com.spire.io.Item;
import java.io.IOException;
import com.spire.log.Reporter;
import com.spire.util.BoolTag;
import com.spire.io.Streamable;
import com.spire.io.DataStream;
import java.io.FileInputStream;
import com.spire.util.Constants;
import java.io.FileOutputStream;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class FilePacket extends Packet implements FileHolder
{
	public static final byte ITEM_CLASS_ID = 8;
	
	private static final long MAX_FILE_SIZE = 8796093018112L;
	
	private long fileSize;
	private int filePackets;
	private String fileName;
	private File fileInstance;
	private BoolTag fileSettings;
	private String fileDestination;
	
	public FilePacket()
	{
		super(ITEM_CLASS_ID, 0);
	}
	public FilePacket(String FilePath) throws NullException, StringException, IOException
	{
		this(FilePath, null);
	}
	public FilePacket(String FilePath, String FileDestination) throws NullException, StringException, IOException
	{
		super(ITEM_CLASS_ID, 0);
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		fileSettings = new BoolTag();
		fileInstance = new File(Constants.CURRENT_OS.phrasePath(FilePath));
		if(!fileInstance.exists())
		{
			Reporter.error(Reporter.REPORTER_IO, "The file \"" + fileInstance.getAbsolutePath() + "\" does not exist!");
			throw new IOException("The file \"" + fileInstance.getAbsolutePath() + "\" does not exist!");
		}
		fileName = fileInstance.getName();
		fileSize = fileInstance.length();
		if(fileSize > MAX_FILE_SIZE)
		{
			Reporter.error(Reporter.REPORTER_IO, "The file \"" + fileInstance.getAbsolutePath() + "\" is too large to send!");
			throw new IOException("The file \"" + fileInstance.getAbsolutePath() + "\" is too large to send!");
		}
		filePackets = Math.max((int)Math.ceil((double)fileSize / 4096F), 1);
		fileDestination = FileDestination != null ? FileDestination : FilePath;
	}
	
	public final void setFileAppend(boolean FileAppend)
	{
		fileSettings.setTagA(FileAppend);
		if(FileAppend) fileSettings.setTagB(false);
	}
	public final void setFileTemporary(boolean FileTemp)
	{
		fileSettings.setTagC(FileTemp);
	}
	public final void setFileOverwrites(boolean FileOverwrites)
	{
		fileSettings.setTagB(FileOverwrites);
		if(FileOverwrites) fileSettings.setTagA(false);
	}
	public final void setFileCreatesFolders(boolean CreateFolders)
	{
		fileSettings.setTagD(CreateFolders);
	}
	public final void setDestination(String FilePath) throws NullException, StringException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		fileDestination = FilePath;
	}
	public final void readFileFromStream(Streamable InStream) throws IOException, NullException
	{
		if(InStream == null) throw new NullException("InStream");
		FileData a = null;
		Streamable b = createFile(this);
		for(int c = 0; c < filePackets; c++)
		{
			a = (FileData)Item.getNextItemByID(InStream, 9);
			if(a != null && a.fileSize <= 4096)
			{
				b.writeByteArray(a.fileData, 0, a.fileSize);
			}
			a = null;
		}
		b.flush();
		b.close();
	}
	public final void writeFileToStream(Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		Streamable a = new DataStream(new FileInputStream(fileInstance));
		for(int b = 0; b < filePackets; b++) new FileData(b, a).writeStream(OutStream);
		a.close();
	}
	
	public final boolean fileAppends()
	{
		return fileSettings.getTagA();
	}
	public final boolean fileTemporary()
	{
		return fileSettings.getTagC();
	}
	public final boolean fileOverwrites()
	{
		return fileSettings.getTagB();
	}
	public final boolean fileCreatesFolders()
	{
		return fileSettings.getTagD();
	}
	
	public final int hashCode()
	{
		return super.hashCode() + (int)fileSize + filePackets + fileName.hashCode() - (fileSettings.hashCode() + (fileDestination != null ? fileDestination.hashCode() : 0)); 
	}
	public final int getPacketHash()
	{
		return super.getPacketHash() + (int)fileSize + filePackets + fileName.hashCode() - (fileSettings.hashCode() + (fileDestination != null ? fileDestination.hashCode() : 0));
	}
	public final int filePacketsCount()
	{
		return filePackets;
	}
	
	public final long fileSize()
	{
		return fileSize;
	}
	
	public final String toString()
	{
		return "FilePacket(" + getItemID() + ") N" + filePackets + "F[" + fileName + "]";
	}
	public final String getFileName()
	{
		return fileName;
	}
	public final String getFilePath()
	{
		return fileInstance.getAbsolutePath();
	}
	public final String getFileDestination()
	{
		return fileDestination;
	}
	
	protected final void readPacket(Streamable InStream) throws IOException
	{
		fileSize = itemEncoder.readLong(InStream);
		filePackets = itemEncoder.readInteger(InStream);
		fileName = itemEncoder.readString(InStream);
		fileDestination = itemEncoder.readString(InStream);
		fileSettings = itemEncoder.readBooleanTags(InStream);
	}
	protected final void writePacket(Streamable OutStream) throws IOException
	{
		itemEncoder.writeLong(OutStream, fileSize);
		itemEncoder.writeInteger(OutStream, filePackets);
		itemEncoder.writeString(OutStream, fileName);
		itemEncoder.writeString(OutStream, fileDestination);
		itemEncoder.writeByte(OutStream, fileSettings.getTagData());
	}
	
	protected final FilePacket getCopy()
	{
		return null;
	}
	
	private static final Streamable createFile(FilePacket Packet) throws IOException
	{
		String a = null;
		byte b = (byte)Packet.fileName.lastIndexOf('.');
		if(b != -1) a = Packet.fileName.substring(b);
		if(!Packet.fileTemporary())
		{
			Packet.fileInstance = new File(Packet.fileDestination);
			File c = Packet.fileInstance.getParentFile();
			if(c != null && !c.exists() && Packet.fileCreatesFolders()) c.mkdirs();
			if((c == null || c.exists()) && (Packet.fileOverwrites() || Packet.fileAppends() || !Packet.fileInstance.exists()))
				return new DataStream(new FileOutputStream(Packet.fileInstance, Packet.fileAppends()));
			//if((c == null || c.exists()) && ((Packet.fileInstance.exists() && (Packet.fileAppends() || Packet.fileOverwrites())) || !Packet.fileInstance.exists()))
			//	return new DataStream(new FileOutputStream(Packet.fileInstance, Packet.fileAppends()));
		}
		Packet.fileInstance = File.createTempFile("spire_temp" + Integer.toHexString(Packet.hashCode()), a);
		Packet.fileName = Packet.fileInstance.getName();
		return new DataStream(new FileOutputStream(Packet.fileInstance));
	}
}