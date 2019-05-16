package com.spire.ex;

public final class UIException extends InternalException
{
	private static final long serialVersionUID = 61520131046429L;
	
	public UIException(String ExceptionMessage)
	{
		this(ExceptionMessage, null);
	}
	public UIException(Throwable ExceptionCause)
	{
		this(null, ExceptionCause);
	}
	public UIException(String ExceptionMessage, Throwable ExceptionCause)
	{
		super(ExceptionType.UI_TITLE, ExceptionType.INTERFACE, ExceptionMessage, ExceptionCause);
	}
}