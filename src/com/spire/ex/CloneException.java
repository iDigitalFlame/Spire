package com.spire.ex;

public final class CloneException extends InternalException
{
	private static final long serialVersionUID = 61520131046424L;
	
	public CloneException()
	{
		super(ExceptionType.CLONE_TITLE, "This Object cannot be cloned!");
	}
	public CloneException(final String CloneMessage)
	{
		super(ExceptionType.CLONE_TITLE, CloneMessage);
	}
}