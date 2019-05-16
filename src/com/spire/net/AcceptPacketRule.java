package com.spire.net;

import com.spire.io.Item;
import java.net.InetAddress;
import com.spire.ex.NullException;

public final class AcceptPacketRule implements TunnelRule
{
	private final byte[] ruleIDList;
	private final boolean ruleMatchItemID;
	private final Class<?>[] ruleClassList;
	
	public AcceptPacketRule(int PacketID)
	{
		this(PacketID, false, (Class<?>[])null);
	}
	public AcceptPacketRule(int PacketID, boolean IsItemID)
	{
		this(PacketID, IsItemID, (Class<?>[])null);
	}
	public AcceptPacketRule(byte[] PacketIDs) throws NullException
	{
		this(PacketIDs, false, (Class<?>[])null);
	}
	public AcceptPacketRule(Class<?>... ClassList) throws NullException
	{
		if(ClassList == null) throw new NullException("ClassList");
		ruleIDList = null;
		ruleMatchItemID = false;
		ruleClassList = ClassList;
	}
	public AcceptPacketRule(int PacketID, boolean IsItemID, Class<?>... ClassList)
	{
		ruleClassList = ClassList;
		ruleMatchItemID = IsItemID;
		ruleIDList = new byte[] { (byte)PacketID };
	}
	public AcceptPacketRule(byte[] PacketIDs, boolean IsItemID) throws NullException
	{
		this(PacketIDs, IsItemID, (Class<?>[])null);
	}
	public AcceptPacketRule(byte[] PacketIDs, boolean IsItemID, Class<?>... ClassList) throws NullException
	{
		if(ClassList == null && PacketIDs == null) throw new NullException("PacketIDs");
		ruleIDList = PacketIDs;
		ruleClassList = ClassList;
		ruleMatchItemID = IsItemID;
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof AcceptPacketRule && CompareObject.hashCode() == hashCode();
	}
	public final boolean canReceive(Computer Sender, InetAddress SenderAddress, Item ReceivedItem)
	{
		if(ruleIDList == null && ruleClassList == null) return true;
		boolean a = false, b = ReceivedItem instanceof Packet;
		if(ruleIDList != null && (ruleMatchItemID || b))
			for(int c = 0; c < ruleIDList.length; c++)
			{
				if(ruleMatchItemID && ruleIDList[c] == ReceivedItem.getItemID())
					a = true;
				if(!ruleMatchItemID && b && ((Packet)ReceivedItem).getID() == ruleIDList[c])
					a = true;
			}
		if(ruleClassList != null)
			for(int d = 0; d < ruleClassList.length; d++)
				if(ruleClassList[d].isAssignableFrom(ReceivedItem.getClass()))
					a = true;
		return a;
	}
	
	public final int hashCode()
	{
		return (ruleIDList != null ? ruleIDList.hashCode() : 100) + (ruleClassList != null ? ruleClassList.hashCode() : 20) - (ruleMatchItemID ? 20: 45);
	}
	
	public final String toString()
	{
		return "AcceptPacketRule(TR) II(" + (ruleMatchItemID ? "Y" : "N") + ") D:" + (ruleIDList != null ? String.valueOf(ruleIDList.length) : "V") + "C:" + (ruleClassList != null ? String.valueOf(ruleClassList.length) : "V");
	}

	protected final AcceptPacketRule clone()
	{
		return new AcceptPacketRule(ruleIDList, ruleMatchItemID, ruleClassList);
	}
}