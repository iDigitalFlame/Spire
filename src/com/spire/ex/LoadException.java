package com.spire.ex;

public final class LoadException extends InternalException
{
	private static final long serialVersionUID = 61520131046421L;

	public LoadException(Throwable Exception)
	{
		super(ExceptionType.LOAD_TITLE, ExceptionType.IO, null, Exception);
	}
}