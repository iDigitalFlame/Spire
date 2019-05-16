package com.spire.ex;

public class IDException extends BasicException
{
	private static final long serialVersionUID = 61520131046425L;

	public IDException(String IDExceptionMessage)
	{
		super(ExceptionType.ID_TITLE, ExceptionType.GENERAL, IDExceptionMessage, null);
	}
}