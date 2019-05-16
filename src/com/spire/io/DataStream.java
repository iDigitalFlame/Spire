package com.spire.io;

import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.spire.ex.NullException;
import com.spire.ex.PermissionException;

public final class DataStream extends Stream
{
	private static final String STREAM_TYPE = "data";
	
	public DataStream()
	{
		this(System.in, System.out);
	}
	public DataStream(InputStream StreamInput) throws NullException, PermissionException
	{
		this(StreamInput, null);
	}
	public DataStream(OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(null, StreamOutput);
	}
	public DataStream(InputStream StreamInput, OutputStream StreamOutput) throws NullException, PermissionException
	{
		super(StreamInput, StreamOutput);
	}
	
	public final PrintWriter getWriter()
	{
		return new PrintWriter(streamOutput);
	}
	
	public final BufferedReader getReader()
	{
		return new BufferedReader(new InputStreamReader(streamInput));
	}
	
	protected final String streamType()
	{
		return STREAM_TYPE;
	}
}