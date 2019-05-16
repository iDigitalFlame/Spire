package com.spire.io.es.xes;

import java.io.IOException;

public final class XESException extends IOException
{
	private static final long serialVersionUID = -7188088528885140989L;
	
	protected XESException(String ExceptionMessage)
	{
		super(ExceptionMessage);
	}
	protected XESException(Throwable ExceptionCause)
	{
		super(ExceptionCause);
	}
}