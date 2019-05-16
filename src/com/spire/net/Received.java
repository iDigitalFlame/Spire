package com.spire.net;

import java.util.List;
import com.spire.io.Item;
import java.net.InetAddress;
import com.spire.util.HashKey;
import com.spire.ex.CloneException;

public final class Received implements HashKey<InetAddress>
{
	public final int receivedPort;
	public final Item[] receivedData;
	public final Computer receivedComputer;
	public final InetAddress receivedAddress;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Received && ((Received)CompareObject).receivedAddress.equals(receivedAddress) &&
			   ((Received)CompareObject).receivedComputer.equals(receivedComputer) && ((Received)CompareObject).receivedData.equals(receivedData);
	}
	
	public final int hashCode()
	{
		return receivedComputer.hashCode();
	}
	
	public final String toString()
	{
		return "Received(NT) >> " + receivedComputer.computerName + "@" + receivedAddress.getHostAddress();
	}
	
	public final InetAddress getKey()
	{
		return receivedAddress;
	}
	
	protected Received(Computer Sender, InetAddress SenderAddress, Item[] Received, int ReceivedPort)
	{
		receivedData = Received;
		receivedComputer = Sender;
		receivedPort = ReceivedPort;
		receivedAddress = SenderAddress;
	}
	protected Received(Computer Sender, InetAddress SenderAddress, List<Item> Received, int ReceivedPort)
	{
		receivedComputer = Sender;
		receivedPort = ReceivedPort;
		receivedAddress = SenderAddress;
		receivedData = Received.toArray(new Item[Received.size()]);
	}
	
	protected final Received clone() throws CloneException
	{
		throw new CloneException("Cannot clone Received Items!");
	}
	protected final Received combine(Received AnotherReceived)
	{
		Item[] a = new Item[receivedData.length + AnotherReceived.receivedData.length];
		System.arraycopy(receivedData, 0, a, 0, receivedData.length);
		System.arraycopy(AnotherReceived.receivedData, 0, a, receivedData.length, AnotherReceived.receivedData.length);
		return new Received(receivedComputer, receivedAddress, a, AnotherReceived.receivedPort);
	}
}