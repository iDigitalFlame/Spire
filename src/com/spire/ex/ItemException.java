package com.spire.ex;

public final class ItemException extends BasicException
{
	private static final long serialVersionUID = 61520131046428L;

	public ItemException(String ItemExceptionMessage)
	{
		super(ExceptionType.ITEM_TITLE, ExceptionType.GENERAL, ItemExceptionMessage, null);
	}
	public ItemException(Throwable ItemExceptionException)
	{
		super(ExceptionType.ITEM_TITLE, ExceptionType.GENERAL, null, ItemExceptionException);
	}
}