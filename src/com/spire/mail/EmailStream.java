package com.spire.mail;

import com.spire.io.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import javax.activation.FileTypeMap;
import javax.mail.MessagingException;
import com.spire.ex.PermissionException;

public final class EmailStream extends Stream
{
	private static final String STREAM_TYPE = "email";
	
	private final EmailPart streamPart;
	
	public final void close() throws IOException
	{
		streamPart.closePart();
	}
	public final void setContentType(String ContentType) throws NullException, StringException
	{
		if(ContentType == null) throw new NullException("ContentType");
		if(ContentType.isEmpty()) throw new StringException("ContentType");
		streamPart.partContent = ContentType;
	}
	public final void setAsFile(String FileName) throws IOException, NullException, StringException, PermissionException
	{
		if(FileName == null) throw new NullException("FileName");
		if(FileName.isEmpty()) throw new StringException("FileName");
		Security.check("io.mail.attch", FileTypeMap.getDefaultFileTypeMap().getContentType(FileName));
		try
		{
			streamPart.setFileName(FileName);
		}
		catch (MessagingException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, Exception);
			throw new IOException(Exception);
		}
	}

	public final boolean isAttachment()
	{
		try
		{
			return streamPart.getFileName() != null;
		}
		catch (MessagingException Exception) { }
		return false;
	}
	
	public final int hashCode()
	{
		return streamPart.hashCode() + super.hashCode();
	}
	
	public final String getFileName()
	{
		try
		{
			return streamPart.getFileName();
		}
		catch (MessagingException Exception){ }
		return null;
	}
	public final String getContentType()
	{
		return streamPart.partContent;
	}
	
	protected EmailStream(EmailPart Part, InputStream InStream, OutputStream OutStream)
	{
		super(InStream, OutStream);
		streamPart = Part;
	}

	protected final void superClose() throws IOException
	{
		super.close();
	}
	
	protected final String streamType()
	{
		return STREAM_TYPE;
	}
}