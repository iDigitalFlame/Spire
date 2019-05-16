package com.spire.net;

import java.util.List;
import java.net.Socket;
import java.util.Arrays;
import com.spire.io.Item;
import java.util.ArrayList;
import com.spire.io.Stream;
import java.io.IOException;
import com.spire.io.Encoder;
import java.net.InetAddress;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.Reflect;
import com.spire.io.CRCStream;
import com.spire.io.Streamable;
import com.spire.util.HashList;
import com.spire.io.DataStream;
import com.spire.util.Constants;
import java.net.SocketException;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.FormatException;
import com.spire.io.SecurityProvider;
import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import com.spire.ex.PermissionException;

public final class Tunnel extends NetworkTunnel
{
	private static final String TUNNEL_TYPE = "item";
	
	private final ArrayList<Item> tunnelSTemp;
	private final ArrayList<TunnelRule> tunnelRules;
	private final HashList<NetAddress, ItemQueue> tunnelQueue;
	
	private Reflect tunnelReflect;
	private boolean tunnelReflectObj;
	private boolean tunnelAcceptsFiles;
	private Computer tunnelReceiveOnly;
	private TunnelAdapter tunnelAdapter;
	private boolean tunnelAcceptsInternet;
	private List<Received> tunnelReceived;
	private TunnelSocketThread[] tunnelThreads;
	
	public Tunnel(int SendPort) throws PermissionException, IOException
	{
		this(0, SendPort, null, null);
	}
	public Tunnel(int SendPort, InetAddress LocalAddress) throws PermissionException, IOException
	{
		this(0, SendPort, LocalAddress, null);
	}
	public Tunnel(int SendPort, SecurityProvider TunnelProvider) throws PermissionException, IOException
	{
		this(0, SendPort, null, TunnelProvider);
	}
	public Tunnel(int ReceivePort, int SendPort) throws NumberException, PermissionException, IOException
	{
		this(ReceivePort, SendPort, null, null);
	}
	public Tunnel(int SendPort, InetAddress LocalAddress, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(0, SendPort, LocalAddress, TunnelProvider);
	}
	public Tunnel(int ReceivePort, int SendPort, InetAddress LocalAddress) throws NumberException, PermissionException, IOException
	{
		this(ReceivePort, SendPort, LocalAddress, null);
	}
	public Tunnel(int ReceivePort, int SendPort, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		this(ReceivePort, SendPort, null, TunnelProvider);
	}
	public Tunnel(int ReceivePort, int SendPort, InetAddress LocalAddress, SecurityProvider TunnelProvider) throws NumberException, PermissionException, IOException
	{
		super(ReceivePort, SendPort, 500, LocalAddress, TunnelProvider);
		tunnelSTemp = new ArrayList<Item>();
		tunnelThreads = new TunnelSocketThread[7];
		tunnelRules = new ArrayList<TunnelRule>();
		tunnelQueue = new HashList<NetAddress, ItemQueue>();
		startTunnel();
	}
	
	public final void clearRules()
	{
		tunnelRules.clear();
	}
	public final void clearReceive()
	{
		tunnelReceived = null;
		tunnelReflect = null;
	}
	public final void clearReceiveReflect()
	{
		tunnelReflect = null;
	}	
	public final void setReceiveFiles(boolean ReceiveFiles)
	{
		tunnelAcceptsFiles = ReceiveFiles;
	}
	public final void setReceiveRelfect(Reflect ReceiveReflect)
	{
		tunnelReflect = ReceiveReflect;
		if(tunnelReflect != null)
			tunnelReflectObj = tunnelReflect.hasParamaters(Received.class);
	}
	public final void clearReceiveList() throws PermissionException
	{
		Security.check("io.net.item.list", getName());
		tunnelReceived = null;
	}
	public final void removeRule(int RuleIndex) throws NumberException
	{
		if(RuleIndex < 0) throw new NumberException("RuleIndex", RuleIndex, false);
		if(RuleIndex > tunnelRules.size()) throw new NumberException("RuleIndex", RuleIndex, 0, tunnelRules.size());
		tunnelRules.remove(RuleIndex);
	}
	public final void addRule(TunnelRule NewRule) throws NullException
	{
		if(NewRule == null) throw new NullException("NewRule");
		tunnelRules.add(NewRule);
	}
	public final void removeRule(TunnelRule RemoveRule) throws NullException
	{
		if(RemoveRule == null) throw new NullException("RemoveRule");
		tunnelRules.remove(RemoveRule);
	}
	public final void setReceiveOnlyFrom(Computer ReceiveOnly) throws PermissionException
	{
		Security.check("io.net.item.com", ReceiveOnly != null ? ReceiveOnly.computerName : null);
		tunnelReceiveOnly = ReceiveOnly;
	}
	public final void setReceiveList(List<Received> ReceiveList) throws PermissionException
	{
		Security.check("io.net.item.list", getName());
		tunnelReceived = ReceiveList;
	}
	public final void setAllowInternetConnections(boolean AllowInternet) throws PermissionException
	{
		Security.check("io.net.item.inet", getName());
		tunnelAcceptsInternet = AllowInternet;
	}
	public final void setAdapter(TunnelAdapter Adapter) throws PermissionException, FormatException
	{
		Security.check("io.net.item.adp", Adapter.getClass());
		if(tunnelAdapter != null && !tunnelAdapter.canReplaceAdapter())
			throw new FormatException("The current Adapter denied changing the adapter!");
		tunnelAdapter = Adapter;
	}
	public final void setMaxThreadCount(int ThreadCount) throws PermissionException, NumberException
	{
		if(ThreadCount <= 0) throw new NumberException("ThreadCount", ThreadCount, true);
		if(ThreadCount > Short.MAX_VALUE) throw new NumberException("ThreadCount", ThreadCount, 0, Short.MAX_VALUE);
		if(ThreadCount < tunnelThreads.length)
		{
			for(short a = (short)ThreadCount; a < tunnelThreads.length; a++)
				if(tunnelThreads[a] != null)
				{
					tunnelThreads[a].tunnelAlive = false;
					tunnelThreads[a] = null;
				}
			tunnelThreads = Arrays.copyOf(tunnelThreads, ThreadCount);
		}
		else if(ThreadCount > tunnelThreads.length)
			tunnelThreads = Arrays.copyOf(tunnelThreads, ThreadCount);
	}
	
	public final boolean hasRules()
	{
		return !tunnelRules.isEmpty();
	}
	public final boolean acceptsFiles()
	{
		return tunnelAcceptsFiles;
	}
	public final boolean acceptsInternetConnections()
	{
		return tunnelAcceptsInternet;
	}
	public final boolean conatinsRule(TunnelRule FindRule) throws NullException
	{
		if(FindRule == null) throw new NullException("FindRule");
		return tunnelRules.contains(FindRule);
	}
	
	public final int hashCode()
	{
		return super.hashCode() + tunnelQueue.hashCode() + tunnelRules.hashCode();
	}
	public final int getQueueSize()
	{
		return tunnelQueue.size();
	}
	public final int getRuleSize()
	{
		return tunnelRules.size();
	}
	public final int getThreadCount()
	{
		return tunnelThreads.length;
	}
	
	public final String toString()
	{
		return "Tunnel(NT) @" + getReceivingPort() + ">" + networkSendPort;
	}
	
	public final QueueRecipt peek(String Destination) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort);
	}
	public final QueueRecipt peek(Computer Destination) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort);
	}
	public final QueueRecipt peek(InetAddress Destination) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort);
	}
	public final QueueRecipt addQueue(String Destination, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt peek(String Destination, int SendPort) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, networkProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}
	public final QueueRecipt peek(String Destination, SecurityProvider ItemProvider) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort, ItemProvider);
	}
	public final QueueRecipt peek(Computer Destination, int SendPort) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, networkProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}
	public final QueueRecipt peek(Computer Destination, SecurityProvider ItemProvider) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort, ItemProvider);
	}
	public final QueueRecipt peek(InetAddress Destination, SecurityProvider ItemProvider) throws NullException, PermissionException
	{
		return peek(Destination, networkSendPort, ItemProvider);
	}
	public final QueueRecipt peek(InetAddress Destination, int SendPort) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, networkProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}
	public final QueueRecipt addQueue(String Destination, boolean WaitForPeek, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, boolean WaitForPeek, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, Item SendItem) throws NullException, PermissionException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, boolean WaitForPeek, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(String Destination, int SendPort, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(String Destination, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, int SendPort, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, int SendPort, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, networkProvider, SendItem);
	}
	public final QueueRecipt peek(String Destination, int SendPort, SecurityProvider ItemProvider) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, ItemProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}
	public final QueueRecipt peek(Computer Destination, int SendPort, SecurityProvider ItemProvider) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, ItemProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}	
	public final QueueRecipt peek(InetAddress Destination, int SendPort, SecurityProvider ItemProvider) throws NullException, NumberException, PermissionException
	{
		QueueRecipt a = addQueue(Destination, SendPort, false, ItemProvider, Packet.PACKET_PEEK);
		a.queueWaitResp = true;
		return a;
	}
	public final QueueRecipt addQueue(String Destination, int SendPort, boolean WaitForPeek, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(String Destination, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, int SendPort, boolean WaitForPeek, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException, PermissionException
	{
		return addQueue(Destination, networkSendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, PermissionException
	{
		return addQueue(Destination, networkSendPort, WaitForPeek, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, int SendPort, boolean WaitForPeek, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, WaitForPeek, networkProvider, SendItem);
	}
	public final QueueRecipt addQueue(String Destination, int SendPort, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, int SendPort, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, int SendPort, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		return addQueue(Destination, SendPort, false, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(String Destination, int SendPort, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		if(SendItem == null) throw new NullException("SendItem");
		if(Destination == null) throw new NullException("Destination");
		return addQueue(Address.getInetFromString(Destination), SendPort, WaitForPeek, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(Computer Destination, int SendPort, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		if(SendItem == null) throw new NullException("SendItem");
		if(Destination == null) throw new NullException("Destination");
		return addQueue(Destination.getInetAddress(), SendPort, WaitForPeek, ItemProvider, SendItem);
	}
	public final QueueRecipt addQueue(InetAddress Destination, int SendPort, boolean WaitForPeek, SecurityProvider ItemProvider, Item SendItem) throws NullException, NumberException, PermissionException
	{
		if(SendItem == null) throw new NullException("SendItem");
		if(Destination == null) throw new NullException("Destination");
		if(SendPort != -1 && SendPort <= 0) throw new NumberException("SendPort", SendPort, true);
		if(SendPort > Constants.MAX_USHORT_SIZE) throw new NumberException("SendPort", SendPort, 0, Constants.MAX_USHORT_SIZE);
		Security.check("io.net.item.sid", Byte.valueOf((byte)SendItem.getItemID()));
		ItemQueue a;
		NetAddress b = new NetAddress(SendPort, Destination);
		if(!tunnelQueue.isEmpty() && tunnelQueue.containsElement(b))
		{
			a = tunnelQueue.get(b);
			if(a.queueAddItem(SendItem))
			{
				b = null;
				a.setSecurityProvider(ItemProvider);
				Reporter.debug(Reporter.REPORTER_NETWORK, "Adding an Item \"" + SendItem + "\" to an existing Queue routed to \"" + Destination.getHostAddress() + "\"");
				return a;
			}
		}
		a = new ItemQueue(Destination, b, SendItem, WaitForPeek);
		a.setSecurityProvider(ItemProvider);
		tunnelQueue.putElement(b, a);
		Reporter.debug(Reporter.REPORTER_NETWORK, "Adding an Item \"" + SendItem + "\" to a new Queue routed to \"" + Destination.getHostAddress() + "\"");
		return a;
	}
	
	public final Tunnel clone() throws CloneException
	{
		try
		{
			return new Tunnel(1000 + Constants.RNG.nextInt(5000), networkSendPort);
		}
		catch (IOException Exception)
		{
			throw new CloneException(Exception.getMessage());
		}
	}
	
	protected final void doOperations() throws IOException
	{
		processSendQueue();
		try
		{
			Socket a = networkSocket.accept();
			boolean b = true;
			for(short c = 0; c < tunnelThreads.length; c++)
			{
				if(tunnelThreads[c] == null)
				{
					tunnelThreads[c] = new TunnelSocketThread(this);
					tunnelThreads[c].tunnelSocket = a;
					b = false;
					break;
				}
				else if(tunnelThreads[c].tunnelSocket == null)
				{
					tunnelThreads[c].tunnelSocket = a;
					b = false;
					break;
				}
			}
			if(b)
				processReceivedData(a, tunnelSTemp);
		}
		catch (SocketTimeoutException Exception) { }
	}
	
	protected final String tunnelType()
	{
		return TUNNEL_TYPE;
	}

	private final void processSendQueue()
	{
		if(!tunnelQueue.isEmpty())
		{
			Socket a = null;
			ItemQueue b = getNextQueue();
			if(b != null && !b.queueIsCorrupted()) try
			{
				Reporter.info(Reporter.REPORTER_NETWORK, "Preparing to send data to \"" + b.queueAddress.addressInstance.getHostAddress() + "\"");
				a = b.queueProvider != null ? b.queueProvider.createSocket(b.queueAddress.addressInstance, b.getPortNumber()) : new Socket(b.queueAddress.addressInstance, b.getPortNumber());
				networkSending = true;
				Reporter.debug(Reporter.REPORTER_NETWORK, "Preparing encoder prime integer");
				int c = Encoder.generateLargePrime();
				Stream d = null;
				if(b.queueProvider != null)
					d = new CRCStream(b.queueProvider.modifyInputStream(a.getInputStream()), b.queueProvider.modifyOuputStream(a.getOutputStream()));
				else d = new CRCStream(a.getInputStream(), a.getOutputStream());
				Reporter.debug(Reporter.REPORTER_NETWORK, "Established a CRC connection!");
				d.writeInteger(c);
				d.flush();
				c *= d.readInteger();
				Reporter.debug(Reporter.REPORTER_NETWORK, "Seting encoder thread seed!");
				Item.setReceivingSeed(c);
				b.writeHeader(d);
				b.writeContents(d);
				Item.writeNullItem(null, d);
				Reporter.info(Reporter.REPORTER_NETWORK, "Sent " + b.queueData.size() + " items to \"" + b.queueAddress.addressInstance.getHostAddress() + "\"");
				Item.closeCurrentController(d);
				d.flush();
				b.setStatus(true);
				if(b.queueWaitResp)
					tunnelQueue.addElement(b);
				Header e = (Header)Item.getNextItemByID(d, Header.ITEM_CLASS_ID);
				if(e != null)
				{
					Reporter.info(Reporter.REPORTER_NETWORK, "Host \"" + e.headerSender.computerName + "\" has data to send back!");
					receiveSentData(a, d, e, tunnelSTemp);
					Item.closeCurrentController(d);
				}
				Item.closeCurrentController(d);
				d.close();
				a.close();
				a = null;
				networkSending = false;
			}
			catch (SocketException Exception)
			{
				if(b.queueAddAttempt())
				{
					Reporter.debug(Reporter.REPORTER_NETWORK, b + " failed to send to \"" + b.queueAddress.addressInstance.getHostAddress() + "\" retrying. " + b.queueAttempts + " out of 3");
					tunnelQueue.add(b);
				}
				else
				{
					b.setException(Exception);
					b.setStatus(false, 3);
					Reporter.error(Reporter.REPORTER_NETWORK, b + " failed to send to \"" + b.queueAddress.addressInstance.getHostAddress() + "\" reached max retries! Removing from queue");
				}
			}
			catch (IOException Exception)
			{
				Reporter.error(Reporter.REPORTER_NETWORK, Exception);
				if(a != null) try
				{
					a.close();
				}
				catch (IOException Exception1) { }
				a = null;
			}
		}
	}
	private final void processReceivedData(Socket SocketData, ArrayList<Item> TempQueue)
	{
		try
		{
			Reporter.info(Reporter.REPORTER_NETWORK, "Preparing to receive data");
			networkSending = true;
			Reporter.debug(Reporter.REPORTER_NETWORK, "Preparing encoder prime integer");
			int a = Encoder.generateLargePrime();
			Stream b = null;
			if(networkProvider != null)
				b = new CRCStream(networkProvider.modifyInputStream(SocketData.getInputStream()), networkProvider.modifyOuputStream(SocketData.getOutputStream()));
			else b = new CRCStream(SocketData.getInputStream(), SocketData.getOutputStream());
			Reporter.debug(Reporter.REPORTER_NETWORK, "Established a CRC connection!");
			b.writeInteger(a);
			b.flush();
			a *= b.readInteger();
			Reporter.debug(Reporter.REPORTER_NETWORK, "Seting encoder thread seed!");
			Item.setReceivingSeed(a);
			Header c = (Header)Item.getNextItemByID(b, Header.ITEM_CLASS_ID);
			if(c != null)
			{
				Reporter.debug(Reporter.REPORTER_NETWORK, "Received header from \"" + c.headerSender.computerName + "\", preparing to transfer data!");
				receiveSentData(SocketData, b, c, TempQueue);
				Item.closeCurrentController(b);
				b.flush();
				NetAddress d = new NetAddress(SocketData.getPort(), SocketData.getInetAddress());
				ItemQueue e = tunnelQueue.get(d);
				if(tunnelAdapter != null)
				{
					tunnelAdapter.addDataToSend(SocketData.getInetAddress(), c.headerSender, TempQueue);
					if(!TempQueue.isEmpty())
					{
						if(e != null) e.queueData.addAll(TempQueue);
						else
						{
							e = new ItemQueue(d);
							e.queueData.addAll(TempQueue);
						}
						TempQueue.clear();
					}
				}
				if(e != null)
				{
					Reporter.info(Reporter.REPORTER_NETWORK, "There is data in the queue to send to \"" + c.headerSender.computerName + "\", Transferring now!");
					e.writeHeader(b);
					e.writeContents(b);
					Item.writeNullItem(null, b);
					Item.closeCurrentController(b);
					b.flush();
					e.setStatus(true);
					if(e.queueWaitResp)
						tunnelQueue.addElement(e);
				}
				else
					Item.writeNullItem(null, b);
				Item.closeCurrentController(b);
				b.flush();
				d = null;
			}
			else
				Reporter.info(Reporter.REPORTER_NETWORK, "Header from \"" + SocketData.getInetAddress().getHostAddress() + "\" is invalid!, Possible invalid Spire version!");
			Item.closeCurrentController(b);
			b.close();
			SocketData.close();
			networkSending = false;
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_NETWORK, "Error in data transmit to host!", Exception);
		}
	}
	private final void receiveSentData(Socket SocketData, Stream Stream, Header SenderHeader, ArrayList<Item> TempQueue) throws IOException
	{
		if(SenderHeader == null)
		{
			Reporter.info(Reporter.REPORTER_NETWORK, "Header from \"" + SocketData.getInetAddress().getHostAddress() + "\" is invalid!, Possible invalid Spire version!");
			return;
		}
		boolean a = SenderHeader.headerSender.getInetAddress().equals(SocketData.getInetAddress());
		if(tunnelAcceptsInternet || a)
		{
			if(!a)
				Reporter.debug(Reporter.REPORTER_NETWORK, "Sender \"" + SenderHeader.headerSender.computerName + "\" might be over ther Internet or VLAN!");
			if(tunnelReceiveOnly == null || tunnelReceiveOnly.computerID == SenderHeader.headerSender.computerID)
			{
				TempQueue.ensureCapacity(SenderHeader.headerDataSize);
				Reporter.debug(Reporter.REPORTER_NETWORK, "Preparing to receive " + SenderHeader.headerDataSize + " items from " + SenderHeader.headerSender.computerName);
				int b = 0, c = 0;
				for(Item d =  Item.getNextItem(Stream); d != null; d = Item.getNextItem(Stream), c++)
				{
					b += d.hashCode();
					if(canReceive(SenderHeader.headerSender, SocketData.getInetAddress(), this, d))
					{
						TempQueue.add(d);
						if(d instanceof FilePacket) //Change to FileHolder, When the file group is created
							((FileHolder)d).readFileFromStream(Stream);
					}
					else if(d instanceof FilePacket) sendVoidFile(d, Stream);
				}
				if(SenderHeader.checkHash(b, c))
				{
					Reporter.debug(Reporter.REPORTER_NETWORK, "Header hash match sucessfull, reading data");
					Received e = new Received(SenderHeader.headerSender, SocketData.getInetAddress(), TempQueue, SocketData.getPort());
					if(tunnelReflect != null)
					{
						if(tunnelReflectObj) tunnelReflect.inokeReflect(new Object[] { e });
						else tunnelReflect.inokeReflect();					
					}
					if(tunnelAdapter != null) tunnelAdapter.onItemReceive(e);
					if(tunnelReceived != null) tunnelReceived.add(e);
					else
					{
						if(Constants.PACKETS.containsKey(e.getKey()))
						{
							e = Constants.PACKETS.get(e.getKey()).combine(e);
							Constants.PACKETS.set(e.getKey(), e);
						}
						else
							Constants.PACKETS.putElement(SocketData.getInetAddress(), e);
					}
					NetAddress f = new NetAddress(SocketData.getPort(), SocketData.getInetAddress());
					ItemQueue g = tunnelQueue.get(f);
					if(g != null && g.queueWaitResp)
					{
						g.queueReceivedData = e;
						tunnelQueue.removeElement(f);
					}
					f = null;
					g = null;
					Reporter.info(Reporter.REPORTER_NETWORK, "Received " + TempQueue.size() + " items from " + SenderHeader.headerSender.computerName);
				}
				else
					Reporter.info(Reporter.REPORTER_NETWORK, "Header hash mismatch from \"" + SenderHeader.headerSender.computerName + "\"!");
				TempQueue.clear();
			}
			else
				Reporter.info(Reporter.REPORTER_NETWORK, "Sender \"" + SenderHeader.headerSender.computerName + "\" does not match the receive only host!");
		}
		else
			Reporter.info(Reporter.REPORTER_NETWORK, "Sender \"" + SenderHeader.headerSender.computerName + "\" is over the Internet of VLAN which is not allowed!");
	}
	
	private final ItemQueue getNextQueue()
	{
		for(byte a = 0; a < tunnelQueue.size(); a++)
			if(!tunnelQueue.get(a).queueHasSent && !tunnelQueue.get(a).queueWaitPeek) return tunnelQueue.remove(a);
		return null;
	}
	
	private static final void sendVoidFile(Item FilePacket, Streamable InStream) throws IOException
	{
		Reporter.debug(Reporter.REPORTER_IO, "Received a file that was not allowed, voiding file!");
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		Stream b = new DataStream(a);
		FileData c = null;
		for(int d = 0; d < ((FilePacket)FilePacket).filePacketsCount(); d++)
		{
			c = (FileData)Item.getNextItemByID(InStream, 9);
			b.writeByteArray(c.fileData, 0, c.fileSize);
			c = null;
		}
		b.close();
		a.close();
		b = null;
		a = null;
	}
	
	private static final boolean canReceive(Computer Sender, InetAddress SenderAddress, Tunnel ReceiveTnnel, Item ReceivedItem)
	{
		for(int a = 0; a < ReceiveTnnel.tunnelRules.size(); a++)
			if(!ReceiveTnnel.tunnelRules.get(a).canReceive(Sender, SenderAddress, ReceivedItem)) return false;
		if(ReceivedItem instanceof FilePacket && !ReceiveTnnel.tunnelAcceptsFiles) return false;
		return Security.valid("io.net.item.rid", Byte.valueOf((byte)ReceivedItem.getItemID()));
	}

	private static final class TunnelSocketThread extends Thread
	{
		private final Tunnel tunnelInstance;
		private final ArrayList<Item> tunnelTemp; 
		
		private Socket tunnelSocket;
		private boolean tunnelAlive;
		
		public final void run()
		{
			while(tunnelAlive && tunnelInstance.networkAlive)
			{
				if(tunnelSocket != null)
				{
					tunnelInstance.processReceivedData(tunnelSocket, tunnelTemp);
					tunnelSocket = null;
				}
				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private TunnelSocketThread(Tunnel Instance)
		{
			tunnelAlive = true;
			tunnelInstance = Instance;
			tunnelTemp = new ArrayList<Item>();
			setPriority(2);
			setName("TunnelSocketThread (" + tunnelInstance.getReceivingPort() + ")");
			start();
		}
	}
}