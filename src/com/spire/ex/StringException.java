package com.spire.ex;

import com.spire.util.Constants;

public final class StringException extends FormatException
{
	private static final long serialVersionUID = 61520131046418L;
	
	public StringException(Throwable ExceptionCause)
	{
		super(ExceptionType.STRING_TITLE, ExceptionCause);
	}
	public StringException(final String ParamaterName)
	{
		super(ExceptionType.STRING_TITLE, "The Paramater \"" + ParamaterName + "\" cannot be empty!");
	}
	public StringException(final String ParamaterName, int StringLength)
	{
		super(ExceptionType.STRING_TITLE, "The Paramater \"" + ParamaterName + "\" length (" + StringLength + ") cannot be greater than " +
										  Constants.MAX_USHORT_SIZE + "!");
	}
}