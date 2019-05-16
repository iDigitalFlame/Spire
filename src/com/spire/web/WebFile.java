package com.spire.web;

import java.io.File;
import java.util.Arrays;
import com.spire.io.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.io.DataStream;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import java.io.ByteArrayInputStream;
import com.spire.ex.StringException;

public final class WebFile extends WebField
{
	private final String fileName;
	private final String fileType;
	private final byte[] fileContent;

	public final void writeFile(File FilePath) throws IOException, NullException
	{
		Stream a = Stream.getFileOutputStream(FilePath, true), b = getStream();
		b.writeToStream(a);
		a.close();
		b.close();
		a = null;
		b = null;
	}
	public final void writeFile(Stream FileOutput) throws IOException, NullException
	{
		if(FileOutput == null) throw new NullException("FileOutput");
		Stream a = getStream();
		a.writeToStream(FileOutput);
		a.close();
		a = null;
	}
	public final void writeFile(OutputStream FileOutput) throws IOException, NullException
	{
		if(FileOutput == null) throw new NullException("FileOutput");
		Stream a = getStream();
		a.writeToStream(FileOutput);
		a.close();
		a = null;
	}
	public final void writeFile(String FilePath) throws IOException, NullException, StringException
	{
		File a = new File(FilePath);
		writeFile(a);
		a = null;
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebFile && ((WebFile)CompareObject).hashCode() == hashCode() && Arrays.equals(fileContent, ((WebFile)CompareObject).fileContent);
	}
	
	public final int hashCode()
	{
		return fileName.hashCode() + fileType.hashCode() * fileContent.length;
	}
	public final int getFileSize()
	{
		return fileContent.length;
	}
	
	public final String toString()
	{
		return "WebFile(IC) " + fileName + "; " + fileType;
	}
	public final String getFileType()
	{
		return fileType;
	}
	public final String getFileName()
	{
		return fileName;
	}
	
	public final InputStream getInputStream()
	{
		return new ByteArrayInputStream(fileContent);
	}
	
	public final Stream getStream()
	{
		return new DataStream(new ByteArrayInputStream(fileContent));
	}
	
	public final WebFile clone() throws CloneException
	{
		throw new CloneException("Cannot clone WebFiles!");
	}
	
	protected WebFile(String FeildName, String FileName, String FileType, byte[] FileContent)
	{
		super(FeildName);
		fileName = FileName;
		fileType = FileType;
		fileContent = FileContent;
	}
	
	protected final Object getFeildValue()
	{
		return getStream();
	}
}