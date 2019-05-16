package com.spire.mail;

import java.util.Arrays;
import javax.mail.Message;
import com.spire.io.Stream;
import javax.mail.BodyPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.log.Reporter;
import java.lang.reflect.Field;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

final class EmailPart extends MimeBodyPart implements Storage
{
	private static final String EMAIL_BLOCK = "parsed";
	private static final String EMAIL_INDEX = "boundary=";
	private static final byte[] EMAIL_SEP =  new byte[] { 13, 10, 13, 10 };
	
	protected String partContent;
	
	private EmailStream partStream;
	private ByteArrayInputStream partInput;
	private ByteArrayOutputStream partOutput;
	
	public final void setFileName(String FileName) throws MessagingException
	{
		super.setFileName(FileName);
		setExternal();
	}
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		int a = 0;
		switch (StorageEncoder.readByte(InStream))
		{
		case 0:
			a = StorageEncoder.readUnsignedByte(InStream);
			break;
		case 1:
			a = StorageEncoder.readUnsignedShort(InStream);
			break;
		case 2:
			a = StorageEncoder.readInteger(InStream);
			break;
		}
		content = new byte[a];
		StorageEncoder.readByteArray(InStream, content, 0, a);
		partContent = StorageEncoder.readString(InStream);
		String b = StorageEncoder.readString(InStream),
			   c = StorageEncoder.readString(InStream),
			   d = StorageEncoder.readString(InStream);
		try
		{
			if(b != null) setFileName(b);
			if(c != null) setDescription(c);
			if(d != null) setDisposition(d);
		}
		catch (MessagingException Exception) { }
		b = null; c = null; d = null;
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		if(content.length < 255)
		{
			StorageEncoder.writeByte(OutStream, 0);
			StorageEncoder.writeByte(OutStream, content.length);
		}
		else if(content.length < Constants.MAX_USHORT_SIZE)
		{
			StorageEncoder.writeByte(OutStream, 1);
			StorageEncoder.writeShort(OutStream, content.length);
		}
		else
		{
			StorageEncoder.writeByte(OutStream, 2);
			StorageEncoder.writeInteger(OutStream, content.length);
		}
		StorageEncoder.writeByteArray(OutStream, content);
		StorageEncoder.writeString(OutStream, partContent);
		String a = null, b = null, c = null;
		try
		{
			a = getFileName();
			b = getDescription();
			c = getDisposition();
		}
		catch (MessagingException Exception) { }
		StorageEncoder.writeString(OutStream, a);
		StorageEncoder.writeString(OutStream, b);
		StorageEncoder.writeString(OutStream, c);
		a = null; b = null; c = null;
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof EmailPart && hashCode() == CompareObject.hashCode();
	}
	
	public final int hashCode()
	{
		return partContent.hashCode() + (content != null ? content.length : 0);
	}
	
	public final String toString()
	{
		return "EmailPart(I) " + partContent + " " + (content != null ? content.length : 0);
	}
	public final String getContentType()
	{
		return partContent;
	}
	
	public final OutputStream getOutputStream()
	{
		if(partOutput == null)
			partOutput = new ByteArrayOutputStream();
		return partOutput;
	}
	
	public final InputStream getInputStream() throws IOException
	{
		if(partInput == null)
		{
			if(content == null)
				throw new IOException("There is no content!");
			partInput = new ByteArrayInputStream(content);
		}
		return partInput;
	}
	
	public final DataHandler getDataHandler() throws MessagingException
	{
		if(dh == null) setExternal();
		else if(!(dh instanceof EmailDataHandler)) try
		{
			Object a = dh.getContentType();
			if(a instanceof MimeMultipart)
				setInvalidBlock((MimeMultipart)a, false);
		}
		catch (IOException Exception)
		{
			throw new MessagingException(Exception.getMessage());
		}
		return dh;
	}
	
	protected EmailPart()
	{
		super();
		partContent = Email.DEFAULT_TYPE;
	}
	protected EmailPart(Message Email) throws IOException, MessagingException
	{
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		try
		{
			Stream.fillFromStream(Email.getInputStream(), a);
		}
		catch (MessagingException Exception)
		{
			try
			{
				Stream.fillFromStream(Email.getDataHandler().getInputStream(), a);
			}
			catch (MessagingException ExceptionA) { }
		}
		a.close();
		content = a.size() == 0 ? String.valueOf(Email.getContent()).getBytes() : a.toByteArray();
		if(Email.getDisposition() != null) setDisposition(Email.getDisposition());
		if(Email.getDescription() != null) setDescription(Email.getDescription());
		partContent = Email.getContentType();
	}
	protected EmailPart(BodyPart Part) throws IOException, MessagingException
	{
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		Stream.fillFromStream(Part.getDataHandler().getInputStream(), a);
		a.close();
		partContent = Part.getContentType();
		if(Part.getDisposition() != null) setDisposition(Part.getDisposition());
		if(Part.getDescription() != null) setDescription(Part.getDescription());
		if(Part.getFileName() != null) setFileName(Part.getFileName());
		content = a.toByteArray();
	}
	protected EmailPart(BodyPart Part, boolean TextSearch) throws IOException, MessagingException
	{
		partContent = Part.getContentType();
		if(Part.getDisposition() != null) setDisposition(Part.getDisposition());
		if(Part.getDescription() != null) setDescription(Part.getDescription());
		if(Part.getFileName() != null) setFileName(Part.getFileName());
		content = getByteSource(Part.getDataHandler().getInputStream(), Part.getContentType());
	}

	protected final void setExternal()
	{
		dh = new EmailDataHandler(this);
	}
	protected final void closePart() throws IOException
	{
		if(partOutput != null)
		{
			if(partStream != null)
				partStream.superClose();
			partOutput.close();
			if(content == null)
				content = partOutput.toByteArray();
			else
			{
				int b = content.length;
				content = Arrays.copyOf(content, content.length + partOutput.size());
				System.arraycopy(partOutput.toByteArray(), 0, content, b, partOutput.size());
			}
			partStream = null;
			partOutput = null;
		}
		if(partInput != null)
		{
			partInput.close();
			partInput = null;
		}
	}
		
	protected final EmailStream getStream()
	{
		if(partStream == null)
		{
			if(partOutput == null)
				partOutput = new ByteArrayOutputStream();
			if(partInput == null && content != null)
				partInput = new ByteArrayInputStream(content);
			partStream = new EmailStream(this, partInput, partOutput);
		}
		return partStream;
	}
	
	protected final EmailPart clone()
	{
		try
		{
			EmailPart a = new EmailPart();
			a.content = Arrays.copyOf(content, content.length);
			a.partContent = partContent;
			if(getDisposition() != null) a.setDisposition(getDisposition());
			if(getDescription() != null) a.setDescription(getDescription());
			if(getFileName() != null) a.setFileName(getFileName());
			return a;
		}
		catch (MessagingException Exception) { }
		return null;
	}

	private static final void setInvalidBlock(MimeMultipart PartData, boolean IsEmailPart) throws IOException
	{
		try
		{
			Field a = (IsEmailPart ? PartData.getClass().getSuperclass() : PartData.getClass()).getDeclaredField(EMAIL_BLOCK);
			a.setAccessible(true);
			if(!a.getBoolean(PartData))
			{
				a.setBoolean(PartData, true);
				Reporter.debug(Reporter.REPORTER_EMAIL, "Fixed invalid block on \"" + PartData.toString() + "\"!");
			}
			a = null;
		}
		catch (SecurityException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Permissions denied fixing invalid blocks!");
			throw Exception;
		}
		catch (NoSuchFieldException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Invalid class! Check class structure!");
			throw new IOException("Invalid class! Check class structure!");
		}
		catch (IllegalAccessException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Invalid class! Check class structure!");
			throw new IOException("Invalid class! Check class structure!");
		}
		catch (IllegalArgumentException Exception)
		{
			Reporter.error(Reporter.REPORTER_EMAIL, "Invalid class! Check class structure!");
			throw new IOException("Invalid class! Check class structure!");
		}
	}
	
	private static final int searchByteArray(int StartIndex, byte[] SearchArray, int SearchSize, byte[] FindArray)
	{
		boolean a = false;
		for(int b = StartIndex; b < SearchArray.length && b < SearchSize; b++)
			if(SearchArray[b] == FindArray[0])
			{
				a = true;
				for(int c = 1; (b + c) < SearchArray.length && c < FindArray.length; c++)
				{
					if(SearchArray[b + c] != FindArray[c])
					{
						a = false;
						break;
					}
				}
				if(a) return b;
			}	
		return -1;
	}
	
	private static final byte[] getByteSource(InputStream InStream, String SelectString) throws IOException
	{
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		byte[] b = new byte[8192], c = null;
		if(SelectString != null && SelectString.contains(EMAIL_INDEX))
		{
			int d = SelectString.indexOf(EMAIL_INDEX);
			if(d >= 0)
			{
				String e = SelectString.substring(d + 9);
				if(e.indexOf('"') >= 0) e = e.substring(1, e.length() - 1);
				c = e.getBytes();
				e = null;
				byte f = 0;
				for(int g = 0, h = 0, i = 0, j = 0; (g = InStream.read(b)) > 0; )
				{
					for(h = searchByteArray(0, b, g, c), i = 0; h >= 0; h = searchByteArray(i, b, g, c), f++)
					{
						if(f == 2)
						{
							j = searchByteArray(i, b, h, EMAIL_SEP);
							if(j >= 0) i = j;
						}
						if(f > 1)
						{
							if(b[i] == 13 && b[i + 1] == 10) i += 2;
							if(b[i] == 13 && b[i + 1] == 10) i += 2;
							if(b[h - 6] == 13 && b[h - 5] == 10 && b[h - 4] == 13 && b[h - 3] == 10 && b[h - 2] == 45 && b[h - 1] == 45) try
							{
								a.write(b, i, (h - i) - 6);
							}
							catch (IndexOutOfBoundsException Exception)
							{
								a.write(b, i, (h - i));
							}
							else 
								a.write(b, i, (h - i));
						}
						i = h + c.length;
					}
					if(i < (g - 2)) a.write(b, i, (g - i));
				}
			}
			else
				Stream.fillFromStream(InStream, a);
		}
		else
			Stream.fillFromStream(InStream, a);
		a.close();
		b = null;
		return a.toByteArray();
	}

	private static final class EmailDataHandler extends DataHandler
	{
		public final Object getContent() throws IOException
		{
			Object a = super.getContent();
			if(a instanceof MimeMultipart)
				setInvalidBlock((MimeMultipart)a, false);
			return a;
		}
		
		private EmailDataHandler(EmailPart PartSource)
		{
			super(new EmailPartSource(PartSource));
		}
	}
	private static final class EmailPartSource implements DataSource
	{
		private final EmailPart sourcePart;
		
		public final String getName()
		{
			try
			{
				return sourcePart.getFileName();
			}
			catch (MessagingException Exception) { }
			return null;
		}
		public final String getContentType()
		{
			return sourcePart.partContent;
		}
		
		public final InputStream getInputStream() throws IOException
		{
			return new ByteArrayInputStream(sourcePart.content);
		}
		
		public final OutputStream getOutputStream() throws IOException
		{
			return null;
		}
		
		private EmailPartSource(EmailPart Part)
		{
			sourcePart = Part;
		}
	}
}