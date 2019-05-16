package com.spire.net;

import java.util.List;
import com.spire.io.Item;
import java.net.InetAddress;

public interface TunnelAdapter
{
	void onItemReceive(Received ReceivedData);
	void addDataToSend(InetAddress TransferHost, Computer TransferComputer, List<Item> DataToAdd);
	
	boolean canReplaceAdapter();
}