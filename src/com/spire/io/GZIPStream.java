package com.spire.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.spire.ex.NullException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import com.spire.ex.PermissionException;

public final class GZIPStream extends Stream
{
	private static final String STREAM_TYPE = "gzip";
	
	public GZIPStream(InputStream StreamInput) throws IOException, NullException, PermissionException
	{
		this(StreamInput, null);
	}
	public GZIPStream(OutputStream StreamOutput) throws IOException, NullException, PermissionException
	{
		this(null, StreamOutput);
	}
	public GZIPStream(InputStream StreamInput, OutputStream StreamOutput) throws IOException, NullException, PermissionException
	{
		super(StreamInput != null ? new GZIPInputStream(StreamInput) : StreamInput, 
				StreamOutput != null ? new GZIPOutputStream(StreamOutput) : StreamOutput);
	}
	
	protected final String streamType()
	{
		return STREAM_TYPE;
	}
}