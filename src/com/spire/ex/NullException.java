package com.spire.ex;

public final class NullException extends FormatException
{
	private static final long serialVersionUID = 61520131046417L;

	public NullException()
	{
		super(ExceptionType.NULL_TITLE, "Null Pointer Access!", new NullPointerException());
	}
	public NullException(final String ParamaterName)
	{
		super(ExceptionType.NULL_TITLE, "The Paramater \"" + ParamaterName + "\" cannot be null!", null);
	}
}