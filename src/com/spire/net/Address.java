package com.spire.net;

import java.util.Arrays;
import java.io.IOException;
import java.net.InetAddress;
import com.spire.io.Encoder;
import com.spire.io.Storage;
import com.spire.io.Streamable;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public final class Address implements Storage
{
	private static final String DEFAULT_ADAPTER = "Adapter";
	
	protected byte[] addressMAC;
	protected byte[] addressBytes;
	protected Computer addressHost;
	
	private boolean addressVPN;
	private short addressPrefix;
	private String addressAdapter;
	private InetAddress addressInet;
	
	public Address()
	{
		addressBytes = new byte[0];
		addressAdapter = DEFAULT_ADAPTER;
	}
	
	public final void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException
	{
		addressPrefix = StorageEncoder.readShort(InStream);
		addressMAC = StorageEncoder.readByteArray(InStream, 6);
		addressBytes = StorageEncoder.readByteArray(InStream, StorageEncoder.readBoolean(InStream) ? 4 : 16);
	}
	public final void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException
	{
		StorageEncoder.writeShort(OutStream, addressPrefix);
		StorageEncoder.writeByteArray(OutStream, addressMAC);
		StorageEncoder.writeBoolean(OutStream, addressBytes.length == 4);
		StorageEncoder.writeByteArray(OutStream, addressBytes);
	}
	
	public final boolean isVPN()
	{
		return addressVPN;
	}
	public final boolean isIP6()
	{
		return addressBytes.length > 4;
	}
	public final boolean isMACEmpty()
	{
		return addressMAC == null || addressMAC.length == 0;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Address && ((((Address)CompareObject).isMACEmpty() || isMACEmpty()) ?
			   ((Address)CompareObject).addressBytes.equals(addressBytes) : ((Address)CompareObject).addressMAC == addressMAC); 
	}
	
	public final short getSubnetPrefix()
	{
		return addressPrefix;
	}
	
	public final int hashCode()
	{
		return isMACEmpty() ? (addressMAC[0] + addressMAC[1] + addressMAC[2] + addressMAC[3] + addressMAC[4] + addressMAC[5]) :
			   				   addressBytes.length * addressBytes[0] + ((addressBytes[addressBytes.length - 1] + 1) * addressBytes.length);
	}
	
	public final String toString()
	{
		return "Address(S) " + Arrays.toString(addressBytes) + "::" + addressAdapter + "@" + addressHost.getName();
	}
	public final String getAdapterName()
	{
		return addressAdapter;
	}
	
	public final InetAddress getAddress()
	{
		if(addressInet == null) try
		{
			addressInet = InetAddress.getByAddress(addressBytes);
		}
		catch (UnknownHostException Exception) { }
		return addressInet;
	}
	
	public final Computer getComputer()
	{
		return addressHost;
	}

	public final byte[] getAdapterMAC()
	{
		return Arrays.copyOf(addressMAC, addressMAC.length);
	}
	public final byte[] getByteAddress()
	{
		return Arrays.copyOf(addressBytes, addressBytes.length);
	}
	
	public static final InetAddress getInetFromString(String Address)
	{
		if(Address == null || Address.isEmpty()) return null;
		try
		{
			return InetAddress.getByName(Address);
		}
		catch (UnknownHostException Exception) { }
		return null;
	}
	
	protected Address(Computer Host, InetAddress Address)
	{
		addressVPN = false;
		addressHost = Host;
		addressInet = Address;
		addressMAC = new byte[0];
		addressAdapter = "Adapter";
		addressBytes = Address.getAddress();
	}
	protected Address(Computer Host, byte[] MACAddress, short NetworkPrefix, NetworkInterface Interface, InetAddress Address)
	{
		addressHost = Host;
		addressInet = Address;
		addressMAC = MACAddress;
		addressPrefix = NetworkPrefix;
		addressBytes = Address.getAddress();
		try
		{
			addressVPN = Interface.isPointToPoint() || addressMAC.length == 0;
		}
		catch (SocketException Exception) { }
		addressAdapter = Interface.getDisplayName();
		if(addressMAC.length != 6) addressMAC = Arrays.copyOf(addressMAC, 6);
	}

	protected final Address clone()
	{
		Address a = new Address();
		a.addressMAC = addressMAC;
		a.addressVPN = addressVPN;
		a.addressHost = addressHost;
		a.addressInet = addressInet;
		a.addressBytes = addressBytes;
		a.addressPrefix = addressPrefix;
		a.addressAdapter = addressAdapter;
		return a;
	}
}