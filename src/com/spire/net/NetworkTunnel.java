package com.spire.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.Constants;
import java.net.InetSocketAddress;
import com.spire.ex.NullException;
import com.spire.ex.FormatException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.io.SecurityProvider;
import com.spire.ex.PermissionException;

public abstract class NetworkTunnel
{
	private final NetworkThread networkThread;
	
	protected final SecurityProvider networkProvider;
	
	protected int networkSendPort;
	protected boolean networkSleep;
	protected boolean networkAlive;
	protected boolean networkStatic;
	protected ServerSocket networkSocket;
	
	protected volatile boolean networkSending;
	
	public final void stop() throws PermissionException
	{
		Security.check("io.net.stop", networkThread.getName());
		networkAlive = false;
		networkThread.interrupt();
		try
		{
			onClose();
			networkSocket.close();
		}
		catch(IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_NETWORK, "There was a problem closing a networked socket!", Exception);
		}
		networkSocket = null;
		networkSending = false;
	}
	public final void wake() throws PermissionException
	{
		if(networkSleep)
		{
			Security.check("io.net.wake", networkThread.getName());
			networkThread.interrupt();
			networkSleep = false;
		}
	}
	public final void sleep() throws PermissionException
	{
		if(!networkSleep)
		{
			Security.check("io.net.sleep", networkThread.getName());
			networkSleep = true;
		}
	}
	public final void interrupt() throws PermissionException
	{
		Security.check("io.net.int", networkThread.getName());
		networkThread.interrupt();
		networkSleep = false;
	}
	public final void setThreadDaemon(boolean Daemon) throws PermissionException
	{
		Security.check("io.net.daemon", networkThread.getName());
		networkThread.setDaemon(Daemon);
	}
	public final void setThreadPriority(int Priority) throws NumberException, PermissionException
	{
		Security.check("io.net.pri", networkThread.getName());
		if(Priority < 0) throw new NumberException("Priority", Priority, false);
		if(Priority > 10) throw new NumberException("Priority", Priority, 0, 10);
		networkThread.setPriority(Priority);
	}
	public final void setThreadName(String Name) throws NullException, StringException, PermissionException
	{
		Security.check("io.net.name", networkThread.getName());
		if(Name == null) throw new NullException("Name");
		if(Name.isEmpty()) throw new StringException("Name");
		networkThread.setName(Name);
	}
	public final void setSendPort(int PortNumber) throws NumberException, PermissionException, FormatException
	{
		Security.check("io.net.edit.sport", networkThread.getName());
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(networkSending) throw new FormatException("Cannot make changes to the Tunnel while it is active!");
		networkSendPort = PortNumber;
	}
	public final void setTunnelTimeout(int Timeout) throws NumberException, PermissionException, FormatException
	{
		Security.check("io.net.edit.timeout", networkThread.getName());
		if(Timeout < 0) throw new NumberException("Timeout", Timeout, false);
		if(networkSending) throw new FormatException("Cannot make changes to the Tunnel while it is active!");
		SocketAddress a = networkSocket.getLocalSocketAddress();
		sleep();
		try
		{
			networkSocket.close();
			networkSocket = networkProvider != null ? networkProvider.createServerSocket() : new ServerSocket();
			networkSocket.bind(a, 500);
			if(Timeout > 0) networkSocket.setSoTimeout(Timeout);
			networkAlive = true;
			wake();
		}
		catch (IOException Exception)
		{
			networkAlive = false;
			networkSocket = null;
			Reporter.error(Reporter.REPORTER_NETWORK, "There was a problem re-creating a NetworkTunnel type \"" + tunnelType() + "\"!", Exception);
		}
		a = null;
	}
	public final void setReceivingPort(int PortNumber) throws NumberException, PermissionException, FormatException
	{
		Security.check("io.net.edit.port", networkThread.getName());
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(networkSending) throw new FormatException("Cannot make changes to the Tunnel while it is active!");
		int a = 0;
		InetAddress b = ((InetSocketAddress)networkSocket.getLocalSocketAddress()).getAddress();
		try
		{
			a = networkSocket.getSoTimeout();
		}
		catch (IOException Exception) { }
		sleep();
		try
		{
			networkSocket.close();
			networkSocket = networkProvider != null ? networkProvider.createServerSocket(PortNumber, 500, b) : new ServerSocket(PortNumber, 500, b);
			if(a > 0) networkSocket.setSoTimeout(a);
			networkAlive = true;
			networkStatic = false;
			wake();
		}
		catch (IOException Exception)
		{
			networkAlive = false;
			networkSocket = null;
			Reporter.error(Reporter.REPORTER_NETWORK, "There was a problem re-creating a NetworkTunnel type \"" + tunnelType() + "\"!", Exception);
		}
		b = null;
	}
	
	public final boolean isAlive()
	{
		return isRunning();
	}
	public final boolean isSecure()
	{
		return networkProvider != null;
	}
	public final boolean isSending()
	{
		return networkSending && networkSocket != null;
	}
	public final boolean isRunning()
	{
		return networkAlive && networkSocket != null;
	}
	public final boolean isSleeping()
	{
		return networkSleep && networkSocket != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof NetworkTunnel && ((NetworkTunnel)CompareObject).getReceivingPort() == getReceivingPort() && CompareObject.hashCode() == hashCode();
	}
	
	public int hashCode()
	{
		return networkSocket != null ? networkSocket.hashCode() + networkSocket.getLocalPort() : 0;
	}
	public final int getSendPort()
	{
		return networkSendPort;
	}
	public final int getReceivingPort()
	{
		return networkSocket != null ? (networkSocket.getLocalPort() + (networkStatic ? 0 : 1)): 0;
	}
	
	public abstract String toString();
	
	public final SecurityProvider getSecurityProvider()
	{
		return networkProvider;
	}
	
	protected NetworkTunnel(int RecPortNumber, int TunnelTimeout) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, RecPortNumber, TunnelTimeout, null, null);
	}
	protected NetworkTunnel(int RecPortNumber, int SendPortNumber, int TunnelTimeout) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, SendPortNumber, TunnelTimeout, null, null);
	}
	protected NetworkTunnel(int RecPortNumber, int TunnelTimeout, InetAddress LocalAddress) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, RecPortNumber, TunnelTimeout, LocalAddress, null);
	}
	protected NetworkTunnel(int RecPortNumber, int TunnelTimeout, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, RecPortNumber, TunnelTimeout, null, TunnelProvider);
	}
	protected NetworkTunnel(int RecPortNumber, int SendPortNumber, int TunnelTimeout, InetAddress LocalAddress) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, SendPortNumber, TunnelTimeout, LocalAddress, null);
	}
	protected NetworkTunnel(int RecPortNumber, int SendPortNumber, int TunnelTimeout, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(RecPortNumber, SendPortNumber, TunnelTimeout, null, TunnelProvider);
	}
	protected NetworkTunnel(int RecPortNumber, int SendPortNumber, int TunnelTimeout, InetAddress LocalAddress, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		Security.check("io.net." + tunnelType());
		if(TunnelTimeout < 0) throw new NumberException("TunnelTimeout", TunnelTimeout, false);
		if(RecPortNumber < 0) throw new NumberException("RecPortNumber", RecPortNumber, false);
		if(RecPortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("RecPortNumber", RecPortNumber, 0, Constants.MAX_USHORT_SIZE);
		if(SendPortNumber <= 0) throw new NumberException("SendPortNumber", SendPortNumber, true);
		if(SendPortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("SendPortNumber", SendPortNumber, 0, Constants.MAX_USHORT_SIZE);
		try
		{
			networkSendPort = SendPortNumber;
			networkProvider = TunnelProvider;
			networkSocket = networkProvider != null ? networkProvider.createServerSocket(RecPortNumber, 500, LocalAddress) : new ServerSocket(RecPortNumber, 500, LocalAddress);
			if(TunnelTimeout > 0) networkSocket.setSoTimeout(TunnelTimeout);
			networkStatic = RecPortNumber != 0;
			networkAlive = true;
			if(networkProvider != null)
				Reporter.debug(Reporter.REPORTER_SECURITY, "NetworkTunnel type \"" + tunnelType() + "\" bound with a \"" + networkProvider.getProviderName() + "\" SecurityProvider!");
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_NETWORK, "There was a problem creating a NetworkTunnel type \"" + tunnelType() + "\"!", Exception);
			throw Exception;
		}
		networkThread = new NetworkThread(this);
		Reporter.info(Reporter.REPORTER_NETWORK, " NetworkTunnel type \"" + tunnelType() + "\" was opened for reciving on port " + networkSocket.getLocalPort() + " and sending on " + SendPortNumber + "!");
	}
	
	protected final void startTunnel()
	{
		if(networkAlive) networkThread.start();
	}
	@SuppressWarnings("unused")
	protected void onClose() throws IOException { }
	protected void onError(Throwable Exception) { }
	protected abstract void doOperations() throws IOException;
	
	protected final String getName()
	{
		return networkThread.getName();
	}
	protected abstract String tunnelType();

	private static final class NetworkThread extends Thread
	{
		private final NetworkTunnel threadTunnel;
		
		public final void run()
		{
			while(threadTunnel.networkAlive) try
			{
				if(threadTunnel.networkSleep) try
				{
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				threadTunnel.doOperations();
			}
			catch (Throwable Exception)
			{
				Reporter.error(Reporter.REPORTER_NETWORK, Exception);
				if(threadTunnel.networkAlive)
				{
					try
					{
						threadTunnel.onError(Exception);
					}
					catch (Throwable Exception1)
					{
						Reporter.error(Reporter.REPORTER_NETWORK, "An error occured handeling the \"onError\" method of the NetworkTunnel type \"" + threadTunnel.tunnelType() + "\"!", Exception1);
					}
					threadTunnel.networkSending = false;
				}
			}
		}
		
		private NetworkThread(NetworkTunnel Tunnel)
		{
			setPriority(3);
			threadTunnel = Tunnel;
			setName("NetworkTunnel@" + threadTunnel.getReceivingPort() + "/" + threadTunnel.tunnelType());
		}
	}
}