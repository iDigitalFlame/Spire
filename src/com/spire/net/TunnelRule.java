package com.spire.net;

import com.spire.io.Item;
import java.net.InetAddress;

public interface TunnelRule
{
	boolean canReceive(Computer Sender, InetAddress SenderAddress, Item ReceivedItem);
}