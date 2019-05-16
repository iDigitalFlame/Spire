package com.spire.web;

import java.util.HashMap;
import java.net.InetAddress;
import com.spire.util.HashKey;
import com.spire.ex.CloneException;

final class WebSession implements HashKey<String>
{
	protected final String sessionKey;
	protected final String sessionCheck;
	protected final InetAddress sessionHost;
	protected final HashMap<String, Object> sessionObjects;
	
	protected long sessionExpires;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebSession && ((WebSession)CompareObject).sessionKey.equals(sessionKey);
	}
	
	public final int hashCode()
	{
		return sessionKey.hashCode();
	}
	
	public final String getKey()
	{
		return sessionKey;
	}
	public final String toString()
	{
		return "WebSession(IE) " + sessionKey + "/" + sessionHost.getHostAddress();
	}
	
	public final WebSession clone() throws CloneException
	{
		throw new CloneException("Cannot clone WebSessions");
	}
	
	protected WebSession(String SessionKey, String SessionCheck, InetAddress SessionHost, long Expires)
	{
		sessionKey = SessionKey;
		sessionExpires = Expires;
		sessionHost = SessionHost;
		sessionCheck = SessionCheck;
		sessionObjects = new HashMap<String, Object>();
	}
}