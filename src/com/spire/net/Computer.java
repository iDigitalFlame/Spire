package com.spire.net;

import com.spire.io.Item;
import java.io.IOException;
import java.net.InetAddress;
import com.spire.util.HashKey;
import com.spire.log.Reporter;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import java.net.UnknownHostException;
import com.spire.ex.ReferenceException;

public class Computer extends Item implements HashKey<Short>
{
	protected static final Address EMPTY_ADDR = getEmptyAddress();
	
	public static final byte ITEM_CLASS_ID = 1;
	
	protected short computerID;
	protected String computerName;
	protected boolean computerSpire;
	protected Address computerAddress;
	
	public Computer(InetAddress Address) throws NullException
	{
		this();
		if(Address == null) throw new NullException("Address");
		computerName = Address.getHostName();
		computerID = getComputerID(computerName);
		computerAddress = new Address(this, Address);
	}
	
	public final boolean isIP6()
	{
		return computerAddress.isIP6();
	}
	public final boolean hasMAC()
	{
		return computerAddress.isMACEmpty();
	}
	public final boolean getSoftwareStatus()
	{
		return computerSpire;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Computer && (!hasMAC() || !((Computer)CompareObject).hasMAC()) ?
			   ((Computer)CompareObject).computerID == computerID :
			   ((Computer)CompareObject).computerAddress.addressMAC.equals(computerAddress.addressMAC);
	}
	
	public final short getID()
	{
		return computerID;
	}
	
	public final int hashCode()
	{
		return computerID + computerAddress.hashCode() + computerName.hashCode();
	}
	
	public final String getName()
	{
		return computerName;
	}
	public final String toString()
	{
		return "Computer(" + getItemID() + ") @" + computerID + "/" + computerName;
	}
	
	public final Address getAddress()
	{
		return computerAddress;
	}
	
	public final InetAddress getInetAddress()
	{
		return computerAddress.getAddress();
	}
	
	public final byte[] getMACAddress()
	{
		return computerAddress.getAdapterMAC();
	}
	
	public final Short getKey()
	{
		return Short.valueOf(computerID);
	}
	
	public final QueueRecipt ping() throws ReferenceException
	{
		if(Constants.NETWORK == null) throw new ReferenceException("Constants.NETWORK");
		return Constants.NETWORK.addQueue(this, Packet.PACKET_PING);
	}
	public final QueueRecipt ping(Tunnel NetworkTunnel) throws NullException
	{
		if(NetworkTunnel == null) throw new NullException("NetworkTunnel");
		return NetworkTunnel.addQueue(this, Packet.PACKET_PING);
	}
	
	public static final Computer getByName(String ComputerName) throws NullException, StringException
	{
		if(ComputerName == null) throw new NullException("ComputerName");
		if(ComputerName.isEmpty()) throw new StringException("ComputerName");
		try
		{
			Reporter.debug(Reporter.REPORTER_NETWORK, "Getting computer \"" + ComputerName + "\" data from the network");
			return new Computer(InetAddress.getByName(ComputerName));
		}
		catch (UnknownHostException Exception)
		{ 
			Reporter.error(Reporter.REPORTER_NETWORK, "Cannot find computer \"" + ComputerName + "\"!");
			return null;
		}
		catch (SecurityException Exception)
		{
			return null;
		}
	}
	
	protected Computer()
	{
		super(ITEM_CLASS_ID);
	}
	
	protected final void readItemFailure()
	{
		computerID = -1;
		computerSpire = false;
		computerName = ".";
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		computerName = itemEncoder.readString(InStream);
		computerSpire = itemEncoder.readBoolean(InStream);
		computerAddress = (Address)itemEncoder.readStorage(InStream);
		computerAddress.addressHost = this;
		computerID = getComputerID(computerName);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeString(OutStream, computerName);
		itemEncoder.writeBoolean(OutStream, computerSpire);
		itemEncoder.writeStorage(OutStream, computerAddress);
	}
	
	protected final Computer getCopy()
	{
		Computer a = new Computer();
		a.computerID = computerID;
		a.computerName = computerName;
		a.computerSpire = computerSpire;
		a.computerAddress = computerAddress.clone();
		a.computerAddress.addressHost = a;
		return a;
	}
	
	protected static final short getComputerID(String ComputerName)
	{
		int a = ComputerName.hashCode();
		for(byte b = 0; b < ComputerName.length(); b++) a+= (byte)ComputerName.charAt(b);
		return (short)a;
	}
	
	private static final Address getEmptyAddress()
	{
		try
		{
			return new Address(null, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
		}
		catch (UnknownHostException Exception)
		{
			return null;
		}
	}
}