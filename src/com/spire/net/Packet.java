package com.spire.net;

import com.spire.io.Item;
import java.io.IOException;
import java.net.InetAddress;
import com.spire.io.Streamable;
import com.spire.util.Constants;
import com.spire.util.Stamp;
import com.spire.cred.HashCheck;
import com.spire.ex.CloneException;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.ReferenceException;

public class Packet extends Item
{
	public static final byte ITEM_CLASS_ID = 6;
	public static final Packet PACKET_PING = new Packet();
	public static final Packet PACKET_PEEK = new Packet(120);
	
	protected final Stamp packetStamp;
	protected final HashCheck packetCheck;
	
	private byte packetID;
	private Computer packetSender;
	private Computer packetCreator;
	
	public Packet()
	{
		this(ITEM_CLASS_ID, 0);
	}
	public Packet(int PacketID) throws NumberException
	{
		this(ITEM_CLASS_ID, PacketID);
	}
	
	public final void setSender()
	{
		packetSender = Constants.LOCAL;
	}
	public final void setSender(Computer SenderComputer)
	{
		packetSender = SenderComputer;
	}
	
	public boolean isValid()
	{
		return packetCheck.isValid(getPacketHash());
	}
	public boolean isSpecialPacket()
	{
		return packetCreator == null;
	}
	public final boolean isFowarded()
	{
		return packetSender != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Packet && ((Packet)CompareObject).packetID == packetID && packetCreator.equals(packetCreator) && hashCode() == CompareObject.hashCode();
	}
	
	public final byte getID()
	{
		return packetID;
	}
	
	public int hashCode()
	{
		return (packetID + 1) + packetCreator.hashCode() + (packetSender != null ? packetSender.hashCode() : 0) + packetStamp.hashCode();
	}
	public int getPacketHash()
	{
		return packetID + packetCreator.hashCode();
	}

	public String toString()
	{
		return getClass().getSimpleName() + "(" + getItemID() + ") P" + (packetID & 0xFF);
	}
	
	public final Computer getSender()
	{
		return packetSender != null ? packetSender : packetCreator;
	}
	public final Computer getComputer()
	{
		return packetCreator;
	}
	
	public final Stamp getCreatedStamp()
	{
		return packetStamp;
	}
	
	public final QueueRecipt sendPacket(String Destination) throws ReferenceException, NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(Constants.NETWORK == null) throw new ReferenceException("Constants.NETWORK");
		return Constants.NETWORK.addQueue(Destination, this);
	}
	public final QueueRecipt sendPacket(Tunnel NetworkTunnel, String Destination) throws NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(NetworkTunnel == null) throw new NullException("NetworkTunnel");
		return NetworkTunnel.addQueue(Destination, this);
	}
	public final QueueRecipt sendPacket(Computer Destination) throws ReferenceException, NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(Constants.NETWORK == null) throw new ReferenceException("Constants.NETWORK");
		return Constants.NETWORK.addQueue(Destination, this);
	}
	public final QueueRecipt sendPacket(Tunnel NetworkTunnel, Computer Destination) throws NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(NetworkTunnel == null) throw new NullException("NetworkTunnel");
		return NetworkTunnel.addQueue(Destination, this);
	}
	public final QueueRecipt sendPacket(InetAddress Destination) throws ReferenceException, NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(Constants.NETWORK == null) throw new ReferenceException("Constants.NETWORK");
		return Constants.NETWORK.addQueue(Destination, this);
	}
	public final QueueRecipt sendPacket(Tunnel NetworkTunnel, InetAddress Destination) throws NullException
	{
		if(Destination == null) throw new NullException("Destination");
		if(NetworkTunnel == null) throw new NullException("NetworkTunnel");
		return NetworkTunnel.addQueue(Destination, this);
	}
	
	public final Packet cloneSetOwner() throws CloneException
	{
		return cloneSetOwner(Constants.LOCAL);
	}
	public final Packet cloneSetSender() throws CloneException
	{
		return cloneSetSender(Constants.LOCAL);
	}
	public final Packet cloneSetSender(Computer SenderComputer) throws CloneException
	{
		Packet a = (Packet)clone();
		a.packetSender = SenderComputer;
		return a;
	}
	public final Packet cloneSetOwner(Computer OwnerComputer) throws NullException, CloneException
	{
		if(OwnerComputer == null) throw new NullException("OwnerComputer");
		Packet a = (Packet)clone();
		a.packetCreator = OwnerComputer;
		return a;
	}
	
	protected Packet(int ItemID, int PacketID) throws NumberException
	{
		super(ItemID);
		if(PacketID == -1) throw new NumberException("\"PaketID\" cannot be -1");
		if(PacketID > 127) throw new NumberException("PacketID", PacketID, -127, 127);
		if(PacketID < -127) throw new NumberException("PacketID", PacketID, -127, 127);
		packetID = (byte)PacketID;
		packetStamp = new Stamp();
		packetCheck = new HashCheck();
		packetCreator = Constants.LOCAL;
	}
	
	protected void readItemFailure()
	{
		packetID = -1;
		packetSender = null;
		packetCreator = null;
	}
	protected void changeLocalAddress(InetAddress Address)
	{
		if(packetCreator instanceof LocalComputer) packetCreator.changeLocalAddress(Address);
		else packetSender = Constants.LOCAL;
		if(packetSender instanceof LocalComputer) packetSender.changeLocalAddress(Address);
		packetCheck.createCheck(getPacketHash());
	}
	@SuppressWarnings("unused")	
	protected void readPacket(Streamable InStream) throws IOException { }
	protected final void readItem(Streamable InStream) throws IOException
	{
		packetID = itemEncoder.readByte(InStream);
		packetStamp.readStorage(InStream, itemEncoder);
		readPacket(InStream);
		packetSender = (Computer)Item.getNextItemByID(InStream, 1);
		packetCreator = (Computer)Item.getNextItemByID(InStream, 1);
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeByte(OutStream, packetID);
		packetStamp.writeStorage(OutStream, itemEncoder);
		writePacket(OutStream);
		Item.writeNullItem(packetSender, OutStream);
		packetCreator.writeStream(OutStream);
		
	}
	@SuppressWarnings("unused")
	protected void writePacket(Streamable OutStream) throws IOException { }
	
	protected Packet getCopy()
	{
		Packet a = new Packet(packetID);
		a.packetCheck.createCheck(getPacketHash());
		a.packetCreator = packetCreator;
		a.packetSender = packetSender;
		return a;
	}
}