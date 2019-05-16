package com.spire.ex;

public class InternalException extends BasicException
{
	private static final long serialVersionUID = 61520131046416L;
	
	public InternalException(String ExceptionMessage)
	{
		this(ExceptionType.INTERNAL_TITLE, ExceptionMessage, null);
	}
	public InternalException(Throwable ExceptionCause)
	{
		this(ExceptionType.INTERNAL_TITLE, null,  ExceptionCause);
	}
	public InternalException(String ExceptionName, String ExceptionMessage)
	{
		this(ExceptionName, ExceptionType.FORMAT, ExceptionMessage, null);
	}
	public InternalException(String ExceptionMessage, Throwable ExceptionCause)
	{
		this(ExceptionType.INTERNAL_TITLE, ExceptionMessage, ExceptionCause);
	}
	public InternalException(String ExceptionName, String ExceptionMessage, Throwable ExceptionCause)
	{
		this(ExceptionName, ExceptionType.GENERAL, ExceptionMessage, ExceptionCause);
	}
	
	protected InternalException(String ExceptionName, ExceptionType ExceptionType, String ExceptionMessage, Throwable ExceptionCause)
	{
		super(ExceptionName, ExceptionType, ExceptionMessage, ExceptionCause);
	}
}