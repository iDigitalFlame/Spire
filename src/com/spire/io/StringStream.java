package com.spire.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import com.spire.log.Reporter;
import java.io.InputStreamReader;
import com.spire.ex.NullException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.spire.ex.PermissionException;

public final class StringStream extends Stream
{
	private static final String STREAM_TYPE = "string";
	
	private OutputStream stringOutput;
	private BufferedReader stringInput;
	
	public StringStream() throws PermissionException
	{
		super(null, new ByteArrayOutput());
	}
	public StringStream(String StringInput) throws IOException, PermissionException
	{
		super(new ByteArrayInput(translateString(StringInput)), null);
	}
	public StringStream(InputStream StreamInput, OutputStream StreamOutput) throws PermissionException, NullException
	{
		super(new ByteArrayInput(), new ByteArrayOutput());
		if(StreamInput == null) throw new NullException("StreamInput");
		if(StreamOutput == null) throw new NullException("StreamOutput");
		stringOutput = StreamOutput;
		stringInput = new BufferedReader(new InputStreamReader(StreamInput));
	}
	
	public final void flush() throws IOException
	{
		flush(false);
	}
	public final void flush(boolean ClearOut) throws IOException
	{
		super.flush();
		if(stringOutput != null)
		{
			stringOutput.write(getString().getBytes());
			if(ClearOut) ((ByteArrayOutput)getInternalOutput()).clear();
		}
	}
	public final void append(String InputString) throws IOException
	{
		getInternalOutput().write(translateString(InputString));
	}
	public final void append(StringStream InputStream) throws IOException, NullException
	{
		if(InputStream == null) throw new NullException("InputStream");
		append(InputStream.getString());
	}

	@SuppressWarnings("restriction")
	public final String getString()
	{
		try
		{
			return com.sun.org.apache.xml.internal.security.utils.Base64.encode(((ByteArrayOutput)getInternalOutput()).getBytes());
		}
		catch (IOException Exception) { }
		return null;
	}
	
	protected final void checkRead() throws IOException
	{
		if(stringInput != null)
		{
			String a = null;
			for(; (a = stringInput.readLine()) != null;)
				((ByteArrayInput)getInternalInput()).addData(translateString(a));
		}
	}
	
	protected final String streamType()
	{
		return STREAM_TYPE;
	}
	
	@SuppressWarnings("restriction")
	private static final byte[] translateString(String InputString) throws IOException
	{
		if(InputString != null && !InputString.isEmpty()) try
		{
			return com.sun.org.apache.xml.internal.security.utils.Base64.decode(InputString);
		}
		catch (com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException Exception) { }
		Reporter.error(Reporter.REPORTER_IO, "This string is not in a correct format!");
		throw new IOException("This string is not in a correct format!");
	}

	private static final class ByteArrayInput extends ByteArrayInputStream
	{
		private ByteArrayInput()
		{
			super(new byte[0]);
		}
		private ByteArrayInput(byte[] InputArray)
		{
			super(InputArray);
		}
		
		private final void addData(byte[] ByteArray)
		{
			byte[] a = new byte[count + ByteArray.length];
			System.arraycopy(buf, 0, a, 0, count);
			System.arraycopy(ByteArray, 0, a, count, ByteArray.length);
			buf = a;
			count += ByteArray.length;
		}
	}
	private static final class ByteArrayOutput extends ByteArrayOutputStream
	{
		private ByteArrayOutput() { }
		
		private final void clear()
		{
			count = 0;
		}
		
		private byte[] getBytes()
		{
			return buf;
		}
	}
}