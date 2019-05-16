package com.spire.ex;

import com.spire.log.Reporter;

public final class PermissionException extends SecurityException
{
	private static final long serialVersionUID = 61520131046423L;
	
	private final String exceptionName;
	private final ExceptionType exceptionType;

	public PermissionException(final String PermissionName)
	{
		this(true, PermissionName);
	}
	public PermissionException(boolean UseString, final String PermissionName)
	{
		super(UseString ? "The Permission \"" + PermissionName + "\" has denied access due to configuration and / or paramaters!" : PermissionName, null);
		exceptionType = ExceptionType.SECURITY;
		exceptionName = ExceptionType.PERMISSION_TITLE;
		if(PermissionName.startsWith("java"))
			Reporter.reportUncaught(this);
		else 
			Reporter.getReporter(exceptionType.typeLog).logSpecial(exceptionType.typeLevel, getMessage(), getCause());
	}
	
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
	
	protected final BasicException clone()
	{
		return null;
	}
}