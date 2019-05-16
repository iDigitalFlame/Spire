package com.spire.net;

import com.spire.io.Item;
import java.io.IOException;
import java.net.InetAddress;
import com.spire.util.HashKey;
import com.spire.io.Streamable;

public abstract class SendQueue extends QueueRecipt implements HashKey<NetAddress>
{
	public final void setStatus(boolean Status)
	{
		setStatus(Status, queueAttempts);
	}
	public final void setException(Throwable Exception)
	{
		queueException = Exception;
	}
	public final void setStatus(boolean Status, int StatusNumber)
	{
		queueHasSent = Status;
		queueAttempts = (byte)StatusNumber;
	}
	
	public final boolean queueAddAttempt()
	{
		if(queueAttempts >= 2) return false;
		queueAttempts++;
		return true;
	}
	public abstract boolean queueIsCorrupted();
	public final boolean queueAddItem(Item QeueItem)
	{
		if(queueHasSent && queueWaitResp)
		{
			queueData.clear();
			queueAttempts = 0;
			queueHasSent = false;
			queueException = null;
		}
		if(queueData.size() >= 127) return false;
		queueData.add(QeueItem);
		return true;
	}
	
	public final NetAddress getKey()
	{
		return queueAddress;
	}
	
	protected SendQueue(InetAddress Receiver, NetAddress Address)
	{
		super(Receiver, Address);
	}
	
	protected final void writeHeader(Streamable OutStream) throws IOException
	{
		queueHeader.prepareForSend(queueData);
		queueHeader.writeStream(OutStream);
	}
	protected final void writeContents(Streamable OutStream) throws IOException
	{
		for(byte a = 0; a < queueData.size(); a++) 
		{
			queueData.get(a).writeStream(OutStream);
			if(queueData.get(a) instanceof FilePacket)
				((FilePacket)queueData.get(a)).writeFileToStream(OutStream);
		}
	}
}