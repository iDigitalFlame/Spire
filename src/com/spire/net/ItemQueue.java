package com.spire.net;

import com.spire.io.Item;
import java.net.InetAddress;

final class ItemQueue extends SendQueue
{
	protected ItemQueue(NetAddress Address)
	{
		super(Address.addressInstance, Address);
	}
	protected ItemQueue(InetAddress Destination, NetAddress Address, Item Payload, boolean WaitPeek)
	{
		super(Destination, Address);
		queueAddItem(Payload);
		queueWaitPeek = WaitPeek;
	}
 
	public final boolean queueIsCorrupted()
	{
		return queueAddress == null;
	}
}