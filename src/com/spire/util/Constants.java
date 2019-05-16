 package com.spire.util;

import java.util.Random;
import com.spire.io.Stream;
import java.net.InetAddress;
import com.spire.io.Encoder;
import com.spire.net.Tunnel;
import com.spire.net.Received;
import com.spire.os.WindowsOS;
import com.spire.net.LocalComputer;
import com.spire.ex.StringException;
import com.spire.io.OperatingSystem;
import com.spire.ex.PermissionException;

public final class Constants
{
	public static final boolean ISMOBILE = false;
	public static final String EMPTY_STRING = "";
	public static final Random RNG = new Random();
	public static final int MAX_USHORT_SIZE = 65535;
	public static final boolean ON_DOMAIN = isOnDomain();
	public static final LocalComputer LOCAL = new LocalComputer();
	public static final String SPIRE_VERSION = "Spire Alpha Version 1.8.2";
	public static final HashList<InetAddress, Received> PACKETS = new HashList<InetAddress, Received>();
	public static final OperatingSystem CURRENT_OS = System.getProperty("os.name").contains("Windows") ? Stream.WINDOWS : Stream.UNIX;
	
	public static Tunnel NETWORK = null;
	
	private static final Stamp NOW = new Stamp();
	
	public static final void refreshLocalAddresses()
	{
		LOCAL.refreshLocal();
	}
	public static final void setEncryptionPasscode(long EncryptionPasscode) throws PermissionException
	{
		Encoder.setEncryptionPasscode(EncryptionPasscode);
	}
	public static final void setEncryptionPasscode(String EncryptionPasscode) throws StringException, PermissionException
	{
		Encoder.setEncryptionPasscode(EncryptionPasscode);
	}
	
	public static final boolean isWindows()
	{
		return CURRENT_OS instanceof WindowsOS;
	}
	public static final boolean isOnDomain()
	{
		if(CURRENT_OS instanceof WindowsOS)
		{
			String a = System.getenv("userdomain");
			return a != null && !a.equals(LOCAL.getName());
		}
		return false;
	}
	
	public static final Stamp getNow()
	{
		NOW.updateStamp();
		return NOW;
	}
	
	private Constants() { }
}