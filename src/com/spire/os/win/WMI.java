package com.spire.os.win;

import java.util.HashMap;
import java.io.IOException;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.ex.InternalException;

public final class WMI
{
	private static String[] wmiCachedData;
	
	public static final String getModel() throws InternalException
	{
		if(wmiCachedData == null) captureBIOSInfo();
		return wmiCachedData[3];
	}
	public static final String getDomain() throws InternalException
	{
		if(wmiCachedData == null) captureBIOSInfo();
		return wmiCachedData[4];
	}
	public static final String getBIOSName() throws InternalException
	{
		if(wmiCachedData == null) captureBIOSInfo();
		return wmiCachedData[0];
	}
	public static final String getSerialNumber() throws InternalException
	{
		if(wmiCachedData == null) captureBIOSInfo();
		return wmiCachedData[1];
	}
	public static final String getManufacturer() throws InternalException
	{
		if(wmiCachedData == null) captureBIOSInfo();
		return wmiCachedData[2];
	}
	
	public static final HashMap<String, String> getQuery(String WMIClass) throws NullException, StringException, InternalException
	{
		if(WMIClass == null) throw new NullException("WMIClass");
		if(WMIClass.isEmpty()) throw new StringException("WMIClass");
		try
		{
			String a = PowerShell.runCommand("Get-WMIObject -Query 'SELECT * FROM " + WMIClass + "'");
			if(a.startsWith("Get-WMIObject : Invalid class"))
				throw new InternalException("\"" + WMIClass + "\" is not a valid WMI Class!");
			return PowerShell.getObjectData(a);
		}
		catch (IOException Exception)
		{
			return null;
		}
	}
	
	private static final void captureBIOSInfo() throws InternalException
	{
		try
		{
			HashMap<String, String> a = PowerShell.runObjectCommand("Get-WMIObject -Query 'SELECT * FROM Win32_BIOS'");
			wmiCachedData = new String[5];
			wmiCachedData[0] = a.get("Name");
			wmiCachedData[2] = a.get("Manufacturer");
			wmiCachedData[1] = a.get("SerialNumber");
			a = PowerShell.runObjectCommand("Get-WMIObject -Query 'SELECT * FROM Win32_ComputerSystem'");
			wmiCachedData[3] = a.get("Model");
			wmiCachedData[4] = a.get("Domain");
		}
		catch (IOException Exception) { }
	}
}
