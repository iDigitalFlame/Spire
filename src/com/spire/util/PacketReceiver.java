package com.spire.util;

import com.spire.net.Packet;
import com.spire.net.Received;

public interface PacketReceiver
{
	void receivePacket(Packet ReceivedPacket, Received ReceivedGroup);
	
	boolean runAsThread();
}