package com.derp.manager;

import java.io.File;
import java.util.Vector;

import org.netcom.io.Stream;
import org.netcom.net.Tunnel;
import org.netcom.types.Computer;

class ManagerSystem
{
	private Computer system_Server;
	private Tunnel system_NetworkInterface;

	protected Vector<ManagerRecord> system_Listing;

	protected boolean system_Waiting;
	protected long system_WaitingTime;
	protected String system_CurrentDirectory;
	protected volatile String system_ManagerStatus;

	protected final void deregisterServer()
	{
		system_Server = null;
		if(system_NetworkInterface != null) system_NetworkInterface.sleep();
	}
	protected final void registerServer(Computer Server)
	{
		system_Server = Server;
		if(system_NetworkInterface == null) system_NetworkInterface = new Tunnel(FileManager.PORT_NUMBER);
		else system_NetworkInterface.wake();
	}
	protected final void navagateDirectory(String Directory)
	{
		system_CurrentDirectory = Directory != null ? Directory : "!";
		if(system_Server != null)
		{
			system_NetworkInterface.sendItem(new ManagerCommand(0, system_CurrentDirectory), system_Server);
			system_ManagerStatus = "Getting listing from server \"" + system_Server.getName() + "\"";
			system_Waiting = true;
			system_WaitingTime = System.currentTimeMillis();
		}
		else
		{
			system_ManagerStatus = "Getting local files listing";
			File[] a = system_CurrentDirectory.equals(":") ? Stream.getDrives() : Stream.getFileList(system_CurrentDirectory);
			for(int b = 0; b < a.length; b++) system_Listing.add(new ManagerRecord(a[b]));
		}
	}
	//protected final void

	protected final boolean isConnected()
	{
		return system_Server != null;
	}
}