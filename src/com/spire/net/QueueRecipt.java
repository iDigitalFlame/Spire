package com.spire.net;

import com.spire.io.Item;
import java.util.ArrayList;
import java.net.InetAddress;
import com.spire.ex.CloneException;
import com.spire.io.SecurityProvider;

public abstract class QueueRecipt
{
	protected final Header queueHeader;
	protected final NetAddress queueAddress;
	protected final ArrayList<Item> queueData;
	
	protected byte queueAttempts;
	protected boolean queueHasSent;
	protected boolean queueWaitResp;
	protected boolean queueWaitPeek;
	protected Throwable queueException;
	protected Received queueReceivedData;
	protected SecurityProvider queueProvider;
	
	public final void waitForResponse(boolean Wait)
	{
		queueWaitResp = Wait;
	}
	public final void setWaitForResponse(boolean Wait)
	{
		queueWaitResp = Wait;
	}
	public final void setSecurityProvider(SecurityProvider Provider)
	{
		queueProvider = Provider;
	}
	
	public final boolean isSent()
	{
		return queueHasSent;
	}
	public final boolean isFailed()
	{
		return !queueHasSent && queueAttempts >= 3;
	}
	public final boolean isWaiting()
	{
		return !queueHasSent && queueAttempts == 0;
	}
	public final boolean isReceived()
	{
		return (!queueWaitResp) || (queueWaitResp && queueReceivedData != null); 
	}
	public final boolean isPeekQueue()
	{
		return !queueHasSent && queueWaitPeek;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof QueueRecipt && queueData.equals(((QueueRecipt)CompareObject).queueData);
	}

	public int hashCode()
	{
		return queueData.hashCode() * (1 + queueData.size()) - queueAttempts;
	}
	public final int getPortNumber()
	{
		return queueAddress.getPort();
	}
	
	public final String toString()
	{
		return "SendQueue(N) [" + (isSent() ? "S" : isFailed() ? "F" : "W/" + queueAttempts) + "]";
	}
	
	public final Throwable queueException()
	{
		return isFailed() ? queueException : null;
	}
	
	public final Received queueReceived()
	{
		return queueReceivedData;
	}
	
	public final SecurityProvider getSecurityProvider()
	{
		return queueProvider;
	}

	protected QueueRecipt(InetAddress Receiver, NetAddress Address)
	{
		queueAddress = Address;
		queueData = new ArrayList<Item>();
		queueHeader = new Header(Receiver);
	}
	
	protected final QueueRecipt clone() throws CloneException
	{
		throw new CloneException("Cannot clone a SendQueue reference!");
	}
}