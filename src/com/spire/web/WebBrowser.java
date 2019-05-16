package com.spire.web;

import com.spire.util.BoolTag;

public final class WebBrowser extends WebHeader
{
	private final WebHeader browserHeader;
	private final BoolTag browserProperties;
	
	private String browserName;
	private float browserVersion;
	
	public final boolean isMobile()
	{
		return browserProperties.getTagA();
	}
	public final boolean isTouchSupported()
	{
		return browserProperties.getTagB();
	}
	
	public final float getVersion()
	{
		return browserVersion;
	}
	
	public final String getBrowserName()
	{
		return browserName;
	}
	public final String getAgentString()
	{
		return headerData;
	}
	
	protected WebBrowser(String HeaderKey, String HeaderData, boolean Outgoing)
	{
		super(HeaderKey, HeaderData, Outgoing);
		browserHeader = null;
		browserProperties = new BoolTag();
		//System.out.println(HeaderData);
	}
	
	private static final void processHeader(WebBrowser Browser, String HeaderData)
	{
		String[] a = HeaderData.split(" ");
		for(short b = 0; b < a.length; b++)
		{
			if(a[b].contains("MSIE"))
			{
				// is ie
			}
			if(a[b].contains("Firefox") || a[b].contains("firefox"))
			{
				// might be mozillia
			}
			if(a[b].contains("Mozilla"))
			{
				//html cap
			}
			if(a[b].contains("Touch") || a[b].contains("touch"))
			{
				// touch cabilbe
			}
			if(a[b].contains("Windows"))
			{
				// windows
				
			}
			if(a[b].contains("Linux"))
			{
				//Linux
			}
			if(a[b].contains("Android"))
			{
				
			}
			if(a[b].contains("Mobile") || a[b].contains("mobile"))
			{
				
			}
		}
	}
}