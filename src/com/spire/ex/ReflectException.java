package com.spire.ex;

public final class ReflectException extends InternalException
{
	private static final long serialVersionUID = 61520131046427L;

	public ReflectException(String Message)
	{
		super(ExceptionType.REFLECT_TITLE, Message);
	}
	public ReflectException(Throwable Exception)
	{
		super(ExceptionType.REFLECT_TITLE, Exception);
	}
}