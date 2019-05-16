package com.spire.web;

import com.spire.util.HashKey;

class WebHeader implements HashKey<String>
{
	protected String headerKey;
	protected String headerData;
	protected boolean headerOutgoing;
	
	public final String getKey()
	{
		return headerKey;
	}
	
	protected WebHeader(String HeaderKey, String HeaderData, boolean Outgoing)
	{
		headerKey = HeaderKey;
		headerData = HeaderData;
		headerOutgoing = Outgoing;
	}
}