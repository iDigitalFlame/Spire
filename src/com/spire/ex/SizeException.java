package com.spire.ex;

public final class SizeException extends NumberException
{
	private static final long serialVersionUID = 61520131046420L;
	
	public SizeException(final String SizeMessage)
	{
		super(SizeMessage);
	}
	public SizeException(final String ParamaterName, boolean isArray, int CurrentSize, int MaxSize)
	{
		super("The " + (isArray ? "Array" : "List") + " \"" + ParamaterName + "\" " + (isArray ? "length" : "size" ) + "(" + CurrentSize + ") cannot be greater than " + MaxSize + "!");
	}
	public SizeException(final String ParamaterName, boolean isArray, int CurrentSize, int MinSize, int MaxSize)
	{
		super("The " + (isArray ? "Array" : "List") + " \"" + ParamaterName + "\" " + (isArray ? "length" : "size" ) + "(" + CurrentSize + ") cannot be " + (CurrentSize < MinSize ? "less than " + MinSize : "greater than " + MaxSize) + "!");
	}
}