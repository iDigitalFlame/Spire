package com.spire.ex;

import com.spire.log.Reporter;

public abstract class DefaultException extends Exception
{
	private static final long serialVersionUID = 61520131046414L;

	private final String exceptionName;
	private final ExceptionType exceptionType;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Exception && ((Exception)CompareObject).getMessage().equals(getMessage());
	}
	
	public final int hashCode()
	{
		return exceptionName.hashCode();
	}
	
	public final String toString()
	{
		return getClass().getCanonicalName() + "; " + getMessage(); 
	}
	public final String getLogName()
	{
		return exceptionType.typeLog;
	}
	public final String getMessage()
	{
		return exceptionName + ": " + (super.getMessage() != null ? super.getMessage() :
							           super.getCause() != null ? super.getCause().getMessage() : "NO MESSAGE AVALIBLE");
	}
	
	public final Reporter getReporter()
	{
		return Reporter.getReporter(exceptionType.typeLog);
	}
	
	protected DefaultException(String ExceptionName, ExceptionType ExceptionType, String ExceptionMessage, Throwable ExceptionCause)
	{
		super(ExceptionMessage, ExceptionCause);
		exceptionName = ExceptionName;
		exceptionType = ExceptionType;
		Reporter.getReporter(exceptionType.typeLog).logSpecial(exceptionType.typeLevel, getMessage(), getCause());
	}

	protected final BasicException clone()
	{
		return null;
	}
}