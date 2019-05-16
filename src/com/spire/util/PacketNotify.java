package com.spire.util;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.spire.net.Packet;
import com.spire.net.Received;

public final class PacketNotify
{
	private final NotifyBaseThread notifyThread;
	private final HashMap<Byte, NotifyGroup> notifyGroups;
	
	private boolean notifySleep;
	private boolean notifyRunning;
	private List<Received> notifyList;
	private NotifyGroup notifyDefault;
	
	public PacketNotify()
	{
		notifyList = Constants.PACKETS;
		notifyThread = new NotifyBaseThread(this);
		notifyGroups = new HashMap<Byte, NotifyGroup>();
		notifyThread.start();
	}
	public PacketNotify(List<Received> PacketList)
	{
		this();
		notifyList = PacketList;
		boolean a = true;
	}
	
	
	
	private static final class NotifyGroup
	{
		private final ArrayList<PacketReceiver> groupList;
		
		private NotifyGroup()
		{
			groupList = new ArrayList<PacketReceiver>();
		}
		
		private final void processGroup(Packet GroupPacket, Received GroupData)
		{
			for(int a = 0; a < groupList.size(); a++)
				if(groupList.get(a).runAsThread())
					new NotifyThread(groupList.get(a), GroupPacket, GroupData).start();
				else groupList.get(a).receivePacket(GroupPacket, GroupData);
		}
	}
	private static final class NotifyThread extends Thread
	{
		private final Packet threadPacket;
		private final Received threadData;
		private final PacketReceiver threadReceiver;
		
		public final void run()
		{
			threadReceiver.receivePacket(threadPacket, threadData);
		}
		
		private NotifyThread(PacketReceiver Receiver, Packet Packet, Received Data)
		{
			threadData = Data;
			threadPacket = Packet;
			threadReceiver = Receiver;
			setName("PacketNotify Thread");
			setPriority(1);
		}
	}
	private static final class NotifyBaseThread extends Thread
	{
		private final PacketNotify threadHost;
		
		public final void run()
		{
			while(threadHost.notifyRunning)
			{
				if(threadHost.notifySleep) try
				{
					Thread.sleep(0x7FFFFFFFL);
				}
				catch (InterruptedException Exception) { }
				if(!threadHost.notifyList.isEmpty())
				{
					for(int a = 0; a < threadHost.notifyList.size(); a++)
					{
						Received b = threadHost.notifyList.remove(0);
						if(b != null)
						{
							NotifyGroup c = null;
							for(int d = 0; d < b.receivedData.length; d++)
								if(b.receivedData[d] instanceof Packet)
								{
									c = threadHost.notifyGroups.get(Byte.valueOf(((Packet)b.receivedData[d]).getID()));
									if(c != null) c.processGroup((Packet)b.receivedData[d], b);
									else if(threadHost.notifyDefault != null)
										threadHost.notifyDefault.processGroup((Packet)b.receivedData[d], b);
								}
							c = null;
							b = null;
						}
					}
				}
				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private NotifyBaseThread(PacketNotify BaseHost)
		{
			threadHost = BaseHost;
			setName("PacketNotify BaseThead");
			setPriority(2);
		}
	}
}