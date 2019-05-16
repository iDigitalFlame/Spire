package com.spire.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Adler32;
import com.spire.ex.NullException;
import com.spire.ex.PermissionException;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

public final class CRCStream extends Stream
{
	private static final String STREAM_TYPE = "crc";
	
	public CRCStream(InputStream StreamInput) throws NullException, PermissionException
	{
		this(StreamInput, null);
	}
	public CRCStream(OutputStream StreamOutput) throws NullException, PermissionException
	{
		this(null, StreamOutput);
	}
	public CRCStream(InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(StreamInput != null ? new CheckedInputStream(StreamInput, new Adler32()) : StreamInput, 
				StreamOutput != null ? new CheckedOutputStream(StreamOutput, new Adler32()) : StreamOutput);
	}
	
	public final long getInputChecksumValue() throws IOException
	{
		if(!isStreamInput()) Stream.notInputStream();
		return ((CheckedInputStream)streamInput.getStream()).getChecksum().getValue();
	}
	public final long getOutputChecksumValue() throws IOException
	{
		if(!isStreamOutput()) Stream.notOutputStream();
		return ((CheckedOutputStream)streamOutput.getStream()).getChecksum().getValue();
	}
	
	protected final String streamType()
	{
		return STREAM_TYPE;
	}
}