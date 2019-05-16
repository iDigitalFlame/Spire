package com.spire.ex;

public class FormatException extends BasicException
{
	private static final long serialVersionUID = 61520131046415L;
	
	public FormatException(String ExceptionMessage)
	{
		this(ExceptionType.FORMAT_TITLE, ExceptionMessage, null);
	}
	public FormatException(Throwable ExceptionCause)
	{
		this(ExceptionType.FORMAT_TITLE, null,  ExceptionCause);
	}
	public FormatException(String ExceptionName, String ExceptionMessage)
	{
		this(ExceptionName, ExceptionMessage, null);
	}
	public FormatException(String ExceptionMessage, Throwable ExceptionCause)
	{
		this(ExceptionType.FORMAT_TITLE, ExceptionMessage, ExceptionCause);
	}
	public FormatException(String ExceptionName, String ExceptionMessage, Throwable ExceptionCause)
	{
		super(ExceptionName, ExceptionType.FORMAT, ExceptionMessage, ExceptionCause);
	}
}