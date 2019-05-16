package com.spire.web;

import com.spire.util.HashKey;

abstract class WebField implements HashKey<String>
{
	protected final String feildName;
	
	public final String getKey()
	{
		return feildName;
	}
	public final String getFeildName()
	{
		return feildName;
	}

	protected WebField(String FeildName)
	{
		feildName = FeildName;
	}
	
	protected abstract Object getFeildValue();
}