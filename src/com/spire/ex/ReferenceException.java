package com.spire.ex;

public class ReferenceException extends InternalException
{
	private static final long serialVersionUID = 61520131046422L;

	public ReferenceException(final String ReferenceParamater)
	{
		super(ExceptionType.REFERENCE_TITLE, "\"" + ReferenceParamater + "\" has not yet been initialized or created!");
	}
}