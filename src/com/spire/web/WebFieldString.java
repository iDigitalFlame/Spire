package com.spire.web;

final class WebFieldString extends WebField
{
	protected String feildValue;
	
	protected WebFieldString(String FeildName)
	{
		super(FeildName);
	}
	
	protected final Object getFeildValue()
	{
		return feildValue;
	}
}