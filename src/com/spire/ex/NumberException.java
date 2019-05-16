package com.spire.ex;

public class NumberException extends FormatException
{
	private static final long serialVersionUID = 61520131046419L;
	
	public NumberException(String NumberMessage)
	{
		super(ExceptionType.NUMBER_TITLE, NumberMessage);
	}
	public NumberException(final String ParamaterName, int CurrentValue, int MaxValue)
	{
		this("The Paramater \"" + ParamaterName + "\" (" + CurrentValue + ") cannot be greater than " + MaxValue + "!");
	}
	public NumberException(final String ParamaterName, int CurrentValue, boolean GreaterZero)
	{
		this("The Paramater \"" + ParamaterName + "\" (" + CurrentValue + ") must be greater than " + (GreaterZero ? "" : "or equal to ") + "Zero!");	
	}
	public NumberException(final String ParamaterName, int CurrentValue, int MinValue, int MaxValue)
	{
		this("The Paramater \"" + ParamaterName + "\" (" + CurrentValue + ") cannot be " + (CurrentValue < MinValue ? "less than " + MinValue : "greater than " + MaxValue) + "!");
	}
}