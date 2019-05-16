package com.spire.net;

import java.net.InetAddress;

final class NetAddress
{
	protected short addressPort;
	protected InetAddress addressInstance;
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof NetAddress && ((NetAddress)CompareObject).addressInstance.equals(addressInstance) && ((NetAddress)CompareObject).addressPort == addressPort;
	}
	
	public final int hashCode()
	{
		return addressPort ^ addressInstance.hashCode();
	}
	
	protected NetAddress(int Port, InetAddress Address)
	{
		addressPort = (short)Port;
		addressInstance = Address;
	}
	
	protected final int getPort()
	{
		return (addressPort & 0xFFFF);
	}
}