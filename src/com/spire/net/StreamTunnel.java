package com.spire.net;

import java.net.Socket;

import com.spire.io.Item;
import com.spire.io.SecurityProvider;

import java.util.ArrayList;

import com.spire.io.Stream;

import java.io.IOException;
import java.net.InetAddress;

import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.HashList;
import com.spire.io.DataStream;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.FormatException;

import java.net.SocketTimeoutException;

import com.spire.ex.PermissionException;

public final class StreamTunnel extends NetworkTunnel
{
	private static final String TUNNEL_TYPE = "stream";
	
	protected final HashList<NetAddress, NetworkStream> streamStreams;
	
	private final ArrayList<StreamRule> streamRules;
	
	private boolean streamAutoFlush;
	private StreamAdapter streamAdapter;
	
	public StreamTunnel(int SendingPort) throws PermissionException, IOException
	{
		this(0, SendingPort, null, null);
	}
	public StreamTunnel(int SendingPort, InetAddress LocalAddress) throws PermissionException, IOException
	{
		this(0, SendingPort, LocalAddress, null);
	}
	public StreamTunnel(int SendingPort, SecurityProvider TunnelProvider) throws PermissionException, IOException
	{
		this(0, SendingPort, null, TunnelProvider);
	}
	public StreamTunnel(int ReceivingPort, int SendingPort) throws NumberException, PermissionException, IOException
	{
		this(ReceivingPort, SendingPort, null, null);
	}
	public StreamTunnel(int SendingPort, InetAddress LocalAddress, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(0, SendingPort, LocalAddress, TunnelProvider);
	}
	public StreamTunnel(int ReceivingPort, int SendingPort, InetAddress LocalAddress) throws NumberException, PermissionException, IOException
	{
		this(ReceivingPort, SendingPort, LocalAddress, null);
	}
	public StreamTunnel(int ReceivingPort, int SendingPort, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(ReceivingPort, SendingPort, null, TunnelProvider);
	}
	public StreamTunnel(int ReceivingPort, int SendingPort, InetAddress LocalAddress, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		super(ReceivingPort, SendingPort, 250, LocalAddress, TunnelProvider);
		streamRules = new ArrayList<StreamRule>();
		streamStreams = new HashList<NetAddress, NetworkStream>();
		startTunnel();
	}
	
	public final void setStreamAutoFlush(boolean AutoFlush)
	{
		streamAutoFlush = AutoFlush;
	}
	public final void removeRule(int RuleIndex) throws NumberException
	{
		if(RuleIndex < 0) throw new NumberException("RuleIndex", RuleIndex, false);
		if(RuleIndex > streamRules.size()) throw new NumberException("RuleIndex", RuleIndex, 0, streamRules.size());
		streamRules.remove(RuleIndex);
	}
	public final void addRule(StreamRule NewRule) throws NullException
	{
		if(NewRule == null) throw new NullException("NewRule");
		streamRules.add(NewRule);
	}
	public final void closeStream() throws IOException, PermissionException
	{
		Security.check("io.net.stream.close", getName());
		closeAllConnections();
		stop();
	}
	public final void removeRule(StreamRule RemoveRule) throws NullException
	{
		if(RemoveRule == null) throw new NullException("RemoveRule");
		streamRules.remove(RemoveRule);
	}
	public final void closeAllConnections() throws IOException, PermissionException
	{
		Security.check("io.net.stream.close.all", getName());
		for(int a = 0; a < streamStreams.size(); a++)
			streamStreams.get(a).close();
	}
	public final void setAdapter(StreamAdapter Adapter) throws PermissionException, FormatException
	{
		Security.check("io.net.stream.adp", Adapter.getClass());
		if(streamAdapter != null && !streamAdapter.canReplaceAdapter())
			throw new FormatException("The current Adapter denied changing the adapter!");
		streamAdapter = Adapter;
	}
	public final void closeConnection(String Destination) throws NullException, IOException, PermissionException
	{
		Security.check("io.net.stream.close", Destination);
		if(Destination == null) throw new NullException("Destination");
		NetworkStream a = findStream(Address.getInetFromString(Destination));
		if(a != null) a.close();
	}
	public final void closeConnection(Computer Destination) throws NullException, IOException, PermissionException
	{
		Security.check("io.net.stream.close", Destination);
		if(Destination == null) throw new NullException("Destination");
		NetworkStream a = findStream(Destination.getInetAddress());
		if(a != null) a.close();
	}
	public final void closeConnection(InetAddress Destination) throws NullException, IOException, PermissionException
	{
		Security.check("io.net.stream.close", Destination);
		if(Destination == null) throw new NullException("Destination");
		NetworkStream a = findStream(Destination);
		if(a != null) a.close();
	}
	
	public final boolean hasRules()
	{
		return !streamRules.isEmpty();
	}
	public final boolean getSteamAutoFlush()
	{
		return streamAutoFlush;
	}
	public final boolean containsConnection(InetAddress Destination)
	{
		return Destination != null && findStream(Destination) != null;
	}
	public final boolean conatinsRule(StreamRule FindRule) throws NullException
	{
		if(FindRule == null) throw new NullException("FindRule");
		return streamRules.contains(FindRule);
	}
	
	public final int getRulesSize()
	{
		return streamRules.size();
	}
	public final int getConnectionsSize()
	{
		return streamStreams.size();
	}
	
	public final String toString()
	{
		return "StreamTunnel(ST) [" + getReceivingPort() + ":" + getSendPort() + " C:" + streamStreams.size() + "]";
	}
	
	public final NetworkStream getConnection(String Destination) throws NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return findStream(Address.getInetFromString(Destination));
	}
	public final NetworkStream getConnection(Computer Destination) throws NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return findStream(Destination.getInetAddress());
	}
	/**
	 * Returns a connection if there is an active one, but will return null if there is not one
	 */
	public final NetworkStream getConnection(InetAddress Destination) throws NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return findStream(Destination);
	}
	public final NetworkStream makeConnection(String Destination) throws IOException, NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Address.getInetFromString(Destination), getSendPort(), networkProvider);
	}
	public final NetworkStream makeConnection(Computer Destination) throws IOException, NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Destination.getInetAddress(), getSendPort(), networkProvider);
	}
	/**
	 * Returns a connection if there is currently an open one, but opens one if there is not
	 */
	public final NetworkStream makeConnection(InetAddress Destination) throws IOException, NullException, PermissionException
	{
		return makeConnection(Destination, getSendPort(), networkProvider);
	}
	public final NetworkStream getConnection(String Destination, int PortNumber) throws NullException, PermissionException, NumberException
	{
		if(Destination == null) throw new NullException("Destination");
		return getConnection(Address.getInetFromString(Destination), PortNumber);
	}
	public final NetworkStream getConnection(Computer Destination, int PortNumber) throws NullException, PermissionException, NumberException
	{
		if(Destination == null) throw new NullException("Destination");
		return getConnection(Destination.getInetAddress(), PortNumber);
	}
	/**
	 * Returns a connection if there is an active one, but will return null if there is not one
	 */
	public final NetworkStream getConnection(InetAddress Destination, int PortNumber) throws NullException, PermissionException, NumberException
	{
		if(Destination == null) throw new NullException("Destination");
		if(PortNumber <= 0) throw new NumberException("PortNumber", PortNumber, true);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		NetAddress a = new NetAddress(PortNumber, Destination);
		NetworkStream b = streamStreams.get(a);
		a = null;
		return b;
	}
	public final NetworkStream makeConnection(String Destination, SecurityProvider StreamProvider) throws IOException, NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Address.getInetFromString(Destination), getSendPort(), StreamProvider);
	}
	public final NetworkStream makeConnection(String Destination, int PortNumber) throws IOException, NullException, NumberException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Address.getInetFromString(Destination), PortNumber, networkProvider);
	}
	public final NetworkStream makeConnection(Computer Destination, SecurityProvider StreamProvider) throws IOException, NullException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Destination.getInetAddress(), getSendPort(), StreamProvider);
	}
	public final NetworkStream makeConnection(Computer Destination, int PortNumber) throws IOException, NullException, NumberException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Destination.getInetAddress(), PortNumber, networkProvider);
	}
	/**
	 * Returns a connection if there is currently an open one, but opens one if there is not
	 */
	public final NetworkStream makeConnection(InetAddress Destination, SecurityProvider StreamProvider) throws IOException, NullException, PermissionException
	{
		return makeConnection(Destination, getSendPort(), StreamProvider);
	}
	/**
	 * Returns a connection if there is currently an open one, but opens one if there is not
	 */
	public final NetworkStream makeConnection(InetAddress Destination, int PortNumber) throws IOException, NullException, NumberException, PermissionException
	{
		return makeConnection(Destination, PortNumber, networkProvider);
	}
	public final NetworkStream makeConnection(String Destination, int PortNumber, SecurityProvider StreamProvider) throws IOException, NullException, NumberException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Address.getInetFromString(Destination), PortNumber, StreamProvider);
	}
	public final NetworkStream makeConnection(Computer Destination, int PortNumber, SecurityProvider StreamProvider) throws IOException, NullException, NumberException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		return makeConnection(Destination.getInetAddress(), PortNumber, StreamProvider);
	}
	/**
	 * Returns a connection if there is currently an open one, but opens one if there is not
	 */
	public final NetworkStream makeConnection(InetAddress Destination, int PortNumber, SecurityProvider StreamProvider) throws IOException, NullException, NumberException, PermissionException
	{
		if(Destination == null) throw new NullException("Destination");
		if(PortNumber <= 0) throw new NumberException("PortNumber", PortNumber, true);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		NetAddress a = new NetAddress(PortNumber, Destination);
		if(streamStreams.containsKey(a)) return streamStreams.get(a);
		if(StreamProvider != null) return handleConnection(StreamProvider.createSocket(Destination, networkSendPort), StreamProvider, a, false);
		return handleConnection(new Socket(Destination, PortNumber), StreamProvider, a, false);
	}
	
	protected final void doOperations() throws IOException
	{
		try
		{
			Socket a = networkSocket.accept();
			handleConnection(a, networkProvider, new NetAddress(a.getPort(), a.getInetAddress()), true);
		}
		catch (SocketTimeoutException Exception) { }
	}

	protected final String tunnelType()
	{
		return TUNNEL_TYPE;
	}
	
	private final NetworkStream findStream(InetAddress Address)
	{
		for(int a = 0; a < streamStreams.size(); a++)
			if(streamStreams.get(a).getHostAddress().equals(Address)) return streamStreams.get(a);
		return null;
	}
	private final NetworkStream handleConnection(Socket Connection, SecurityProvider Provider, NetAddress Address, boolean IsServer) throws IOException
	{
		Reporter.info(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" opening a connection to \"" + Connection.getInetAddress().getHostAddress() + "\"");
		Stream a = null;
		if(Provider != null)
			a = new DataStream(Provider.modifyInputStream(Connection.getInputStream()), Provider.modifyOuputStream(Connection.getOutputStream()));
		else a = new DataStream(Connection.getInputStream(), Connection.getOutputStream());
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" connection opened");
		byte b = 0;
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" syncing the starting bit");
		if(IsServer)
		{
			b = (byte)Constants.RNG.nextInt(255);
			a.writeByte(b);
			a.flush();
		}
		else
			b = a.readByte();
		StreamEncoder c = new StreamEncoder(b);
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" Encoder created!");
		c.syncKeys(a);
		c.openEncoder(a, IsServer);
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" Encoder keys synced!");
		Computer e = Constants.LOCAL, f = null;
		e.changeLocalAddress(Connection.getInetAddress());
		if(IsServer)
		{
			e.writeStream(a);
			Item.closeCurrentController(a);
			a.flush();
			f = (Computer)Item.getNextItemByID(a, Computer.ITEM_CLASS_ID);
			Item.closeCurrentController(a);
		}
		else
		{
			f = (Computer)Item.getNextItemByID(a, Computer.ITEM_CLASS_ID);
			Item.closeCurrentController(a);
			e.writeStream(a);
			Item.closeCurrentController(a);
			a.flush();
		}
		if(f == null)
		{
			Reporter.error(Reporter.REPORTER_NETWORK, "The connection to \"" + Connection.getInetAddress().getHostAddress() + "\" failed!");
			throw new IOException("The connection to \"" + Connection.getInetAddress().getHostAddress() + "\" failed!");
		}
		boolean g = true;
		if(!streamRules.isEmpty())
			for(int h = 0; g && h < streamRules.size(); h++)
				g = streamRules.get(h).acceptConnection(f, Connection.getInetAddress());
		a.writeBoolean(g);
		a.flush();
		if(!a.readBoolean())
		{
			a.close();
			Connection.close();
			Reporter.error(Reporter.REPORTER_NETWORK, "The connection to \"" + f.computerName + "\" was refused by its rules!");
			throw new IOException("The connection to \"" + f.computerName + "\" was refused by its rules!");
		}
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" received host data, connected to \"" + f.computerName + "\"");
		NetworkStream h = new NetworkStream(this, Address, c, f, a);
		streamStreams.add(h);
		Reporter.debug(Reporter.REPORTER_NETWORK, "NetworkTunnel type \"stream\" connection finished!");
		if(streamAdapter != null)
		{
			if(streamAdapter.runAsThread())
				new StreamTunnelThread(IsServer, streamAdapter, h).start();
			else
				streamAdapter.onConnect(h, IsServer);
		}
		return h;
	}

	private static final class StreamTunnelThread extends Thread
	{
		private final boolean streamServer;
		private final StreamAdapter streamAdapter;
		private final NetworkStream streamConnection;
		
		public final void run()
		{
			try
			{
				streamAdapter.onConnect(streamConnection, streamServer);
			}
			catch (Throwable Exception)
			{
				Reporter.error(Reporter.REPORTER_NETWORK, "There was a problem running the \"onConnect\" method as a thread!", Exception);
			}
		}
		
		private StreamTunnelThread(boolean IsServer, StreamAdapter Adapter, NetworkStream Stream)
		{
			streamServer = IsServer;
			streamAdapter = Adapter;
			streamConnection = Stream;
			setPriority(1);
			setName("StreamTunnelThread Adp: " + Adapter.getClass().getName());
		}
	}
}