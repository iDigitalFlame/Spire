package com.spire.net;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.Enumeration;
import com.spire.log.Reporter;
import com.spire.util.BoolTag;
import com.spire.util.Constants;
import java.net.InterfaceAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import com.spire.ex.NumberException;
import java.net.UnknownHostException;
import com.spire.ex.InternalException;

public final class LocalComputer extends Computer
{
	private Address[] localAddresses;
	private MobileInfoThread localMobile;
	
	public LocalComputer() throws InternalException
	{
		super();
		if(Constants.LOCAL != null)
			throw new InternalException("Cannot create an instance of LocalComputer! Please use \"Constants.LOCAL\" to specify the Local PC!");
		computerSpire = true;
		computerName = getComputerName();
		computerID = getComputerID(computerName);
		if(Constants.ISMOBILE)
		{
			localMobile = new MobileInfoThread(this);
			localMobile.start();
		}
		else
		{
			localAddresses = getComputerAddresses(this);
			computerAddress = localAddresses.length > 0 ? localAddresses[0] : Computer.EMPTY_ADDR;
		}
	}
	
	public final void refreshLocal()
	{
		localAddresses = getComputerAddresses(this);
		computerAddress = localAddresses.length > 0 ? localAddresses[0] : null;
	}
	
	public final int getAdddressCount()
	{
		return localAddresses.length;
	}
	
	public final Address getAddress(int AddressIndex) throws NumberException
	{
		if(localAddresses == null) return null;
		if(AddressIndex < 0)
			throw new NumberException("AddressIndex", AddressIndex, false);	
		if(AddressIndex > localAddresses.length)
			throw new NumberException("AddressIndex", AddressIndex, 0, localAddresses.length);
		return localAddresses[AddressIndex];
	}
	
	public final Address[] getLocalAddresses()
	{
		return Arrays.copyOf(localAddresses, localAddresses.length);
	}
	
	protected final void changeLocalAddress(InetAddress Address)
	{
		byte[] a = Address.getAddress();
		for(byte b = 0; b < localAddresses.length; b++)
		{
			if(isAddressMatch(localAddresses[b], a))
			{
				synchronized(computerAddress)
				{
					computerAddress = localAddresses[b];
				}
				return;
			}
		}
		for(byte c = 0; c < localAddresses.length; c++)
			if(localAddresses[c].addressBytes.length == a.length)
			{
				synchronized(computerAddress)
				{
					computerAddress = localAddresses[c];
				}
				return;
			}
		synchronized(computerAddress)
		{
			computerAddress = localAddresses.length > 0 ? localAddresses[0] : null;
		}
	}

	private static final boolean isAddressMatch(Address LocalAddress, byte[] CompareAddress)
	{
		if(CompareAddress.length != LocalAddress.addressBytes.length) return false;
		byte a = (byte)(LocalAddress.getSubnetPrefix() / 8);
		if(a == 0) a = 3;
		for(byte b = 0; b < a; b++) if(LocalAddress.addressBytes[b] != CompareAddress[b]) return false;
		if(LocalAddress.getSubnetPrefix() > 0 && (LocalAddress.getSubnetPrefix() - (a * 8)) > 0)
		{
			BoolTag c = new BoolTag(LocalAddress.addressBytes[a]), d = new BoolTag(CompareAddress[a]);
			for(byte e = 7; e > (7 - LocalAddress.getSubnetPrefix() - (a * 8)); e--)
			{
				if(c.getTag(e) && !d.getTag(e)) return false;
				if(!c.getTag(e) && d.getTag(e)) return false;
			}
		}
		return true;
	}
	
	private static final String getComputerName()
	{
		String a = System.getenv("computername");
		if(a == null) a = System.getenv("hostname");
		if(a == null) try
		{
			a = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException Exception) { }
		return a == null ? "SPIRE_UNKN_PC" : a;
	}
	
	private static final Address[] getComputerAddresses(Computer Instance)
	{
		try
		{
			Enumeration<NetworkInterface> a = NetworkInterface.getNetworkInterfaces();
			if(a != null) 
			{
				byte[] b;
				InetAddress c;
				NetworkInterface d;
				List<InterfaceAddress> e;
				ArrayList<Address> f = new ArrayList<Address>();
				while(a.hasMoreElements())
				{
					d = a.nextElement();
					if(d.getHardwareAddress() != null && d.isUp() && !d.isLoopback())
					{
						b = d.getHardwareAddress();
						if(b.length == 0 || (b.length > 0 && b[b.length - 1] != -32))
						{
							e = d.getInterfaceAddresses();
							for(int g = 0; g < e.size(); g++)
							{
								c = e.get(g).getAddress();
								if(Instance.computerName == null)
									Instance.computerName = c.getHostName();
								f.add(new Address(Instance, b, e.get(g).getNetworkPrefixLength(), d, c));
							}
						}
					}
				}
				return f.toArray(new Address[f.size()]);
			}
			Reporter.severe(Reporter.REPORTER_NETWORK, "Cannot retrive LOCAL Address data!");
			return new Address[0];
		}
		catch (SocketException Exception)
		{
			Reporter.severe(Reporter.REPORTER_NETWORK, "Cannot retrive LOCAL Address data!", Exception);
		}
		return new Address[0];
	}

	private static final class MobileInfoThread extends Thread
	{
		private final LocalComputer threadLocal;
		
		public final void run()
		{
			threadLocal.localAddresses = getComputerAddresses(threadLocal);
			threadLocal.computerAddress = threadLocal.localAddresses.length > 0 ? threadLocal.localAddresses[0] : Computer.EMPTY_ADDR;
			threadLocal.localMobile = null;
		}
				
		private MobileInfoThread(LocalComputer LocalInstance)
		{
			threadLocal = LocalInstance;
			setPriority(8);
			setName("Get Mobile Info");
		}
	}
}